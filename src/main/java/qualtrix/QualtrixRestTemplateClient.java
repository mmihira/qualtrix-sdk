package qualtrix;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import qualtrix.responses.V3.MailingList.CreateMailingListInput;
import qualtrix.responses.V3.MailingList.CreateMailingListResponse;
import qualtrix.responses.V3.ResponseExport.AbstractCreateResponseExportInput;
import qualtrix.responses.V3.ResponseExport.CreateResponseExportInput;
import qualtrix.responses.V3.ResponseExport.CreateResponseExportResponse;
import qualtrix.responses.V3.ResponseExportFile.AbstractResponseExportFileEntity;
import qualtrix.responses.V3.ResponseExportFile.AbstractResponseExportFileResponse;
import qualtrix.responses.V3.ResponseExportFile.DefaultResponseExportFileResponse;
import qualtrix.responses.V3.ResponseExportProgress.ResponseExportProgressResponse;
import qualtrix.responses.V3.Survey.AbstractSurveyResult;
import qualtrix.responses.V3.Survey.DefaultSurveyResponse;
import qualtrix.responses.V3.Survey.SurveyResponse;
import qualtrix.responses.V3.SurveyList.SurveyListResponse;
import qualtrix.responses.V3.WhoAmI.WhoAmIResponse;

import java.io.*;
import java.util.zip.ZipInputStream;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QualtrixRestTemplateClient extends QualtrixClientBase {
    @NonNull
    private final RestTemplate restClient;

    @Builder
    public QualtrixRestTemplateClient(@NonNull String accessToken,
                                      @NonNull RestTemplate restClient) {

        super(accessToken);
        this.restClient = restClient;
    }

    private <T> ResponseEntity<T> getRequest(String path, Class<T> t) {
        String url = this.buildRequestUrl(path);
        HttpEntity entity = new HttpEntity(this.genericGetHeaders());
        return this.restClient.exchange(url, HttpMethod.GET, entity, t);
    }

    public ResponseEntity<WhoAmIResponse> whoAmI() throws IOException, InterruptedException {
        return this.getRequest(EndPoints.V3.WhoAmI.path(), WhoAmIResponse.class);
    }

    public ResponseEntity<SurveyListResponse> listSurveys() throws IOException, InterruptedException {
        return this.getRequest(EndPoints.V3.ListSurveys.path(), SurveyListResponse.class);
    }

    public ResponseEntity<DefaultSurveyResponse> survey(String surveyId) throws IOException, InterruptedException {
        return survey(surveyId, DefaultSurveyResponse.class);
    }

    public <T extends AbstractSurveyResult, U extends SurveyResponse<T>> ResponseEntity<U>
    survey(String surveyId, Class<U> tClass) throws IOException, InterruptedException {

        return this.getRequest(EndPoints.V3.GetSurvey.forSurvey(surveyId), tClass);
    }

    public <T extends AbstractCreateResponseExportInput> ResponseEntity<CreateResponseExportResponse> createResponseExport(
            String surveyId, T body) throws IOException, InterruptedException {

        String url = this.buildRequestUrl(EndPoints.V3.CreateResponseExport.forSurvey(surveyId));
        HttpEntity<T> entity = new HttpEntity<>(body, this.genericPostHeaders());
        return this.restClient.exchange(url, HttpMethod.POST, entity, CreateResponseExportResponse.class);
    }

    public ResponseEntity<ResponseExportProgressResponse> responseExportProgress(String surveyId, String exportProgressId)
            throws IOException, InterruptedException {

        return this.getRequest(EndPoints.V3.ResponseExportProgress.path(surveyId, exportProgressId), ResponseExportProgressResponse.class);
    }

    public <T> ResponseEntity<T> createResponseExportFile(
            String surveyId, String fileId, ResponseExtractor<ResponseEntity<T>> f) throws IOException {

        String url = this.buildRequestUrl(EndPoints.V3.ResponseExportFile.path(surveyId, fileId));
        return this.restClient.execute(url, HttpMethod.GET,
                nn -> nn.getHeaders().putAll(this.genericGetHeaders()), f);
    }

    public ResponseEntity<ClientHttpResponse> createResponseExportFileHttpResponse(
            String surveyId, String fileId) throws IOException {

        return createResponseExportFile(surveyId, fileId, clientHttpResponse ->
                new ResponseEntity<>(clientHttpResponse, clientHttpResponse.getHeaders(), clientHttpResponse.getStatusCode()));
    }

    public <T extends AbstractResponseExportFileEntity, U extends AbstractResponseExportFileResponse<T>>
    ResponseEntity<U> createResponseExportFileObject(String surveyId, String fileId, Class<U> uClass) throws IOException {

        return createResponseExportFile(surveyId, fileId, clientHttpResponse -> {
            try (ZipInputStream zipInputStream = new ZipInputStream(clientHttpResponse.getBody())) {
                zipInputStream.getNextEntry();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                U ret = objectMapper.readValue(zipInputStream, uClass);
                return new ResponseEntity<>(ret, clientHttpResponse.getHeaders(), clientHttpResponse.getStatusCode());
            }
        });
    }

    public ResponseEntity<DefaultResponseExportFileResponse> createResponseExportFileObject(
            String surveyId, String fileId) throws IOException {
        return createResponseExportFileObject(surveyId, fileId, DefaultResponseExportFileResponse.class);
    }


    public <T extends AbstractResponseExportFileEntity, U extends AbstractResponseExportFileResponse<T>, G extends CreateResponseExportInput>
    ResponseEntity<U> createResponseExportAndGetFile(String surveyId, G input, Class<U> uClass) throws IOException, InterruptedException {
        // TODO: Add a timeout to this function
        var export = this.createResponseExport(surveyId, input);
        boolean complete = false;
        while (!complete) {
            var progress = this.responseExportProgress(surveyId, export.getBody().getResult().getProgressId());
            complete = progress.getBody().getResult().isComplete();
            if (complete) {
                return this.createResponseExportFileObject(surveyId, progress.getBody().getResult().getFileId(), uClass);
            }
        }
        return null;
    }

    ResponseEntity<DefaultResponseExportFileResponse> createResponseExportAndGetFileDefault(String surveyId, CreateResponseExportInput input) throws IOException, InterruptedException {
        return this.createResponseExportAndGetFile(surveyId, input, DefaultResponseExportFileResponse.class);
    }

//    ResponseEntity<CreateMailingListResponse> createMailingLIst(CreateMailingListInput input) {
//    }
}

