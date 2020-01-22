package qualtrix;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import qualtrix.responses.V3.*;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
@Data
public class QualtrixClient {
    @NonNull private final RestTemplate restClient;
    @NonNull private final String accessToken;
    private String qualtrixApiBaseURL = "https://au1.qualtrics.com/";

    private String buildRequestUrl(String endpoint) {
        return String.format("%s/%s", this.qualtrixApiBaseURL, endpoint);
    }

    private HttpHeaders genericGetHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-TOKEN", this.accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private HttpHeaders genericPostHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-TOKEN", this.accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private <T> ResponseEntity<T> getReqeust(String path, Class<T> t) {
        String url = this.buildRequestUrl(path);
        HttpEntity entity = new HttpEntity(this.genericGetHeaders());
        return this.restClient.exchange(url, HttpMethod.GET, entity, t);
    }

    /**
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public ResponseEntity<WhoAmIResponse> whoAmI() throws IOException, InterruptedException {
        return this.getReqeust(EndPoints.V3.WhoAmI.path(), WhoAmIResponse.class);
    }

    /**
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public ResponseEntity<SurveyListResponse> listSurveys() throws IOException, InterruptedException {
        return this.getReqeust(EndPoints.V3.ListSurveys.path(), SurveyListResponse.class);
    }

    /**
     * getSurvey and deserealise the response ignoring much of the question data.
     * Use {@link #survey(String, Class)} and extend if you want to specify custom data.
     * @param surveyId
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public ResponseEntity<DefaultSurveyResponse> survey(String surveyId) throws IOException, InterruptedException {
        return this.getReqeust(EndPoints.V3.GetSurvey.forSurvey(surveyId), DefaultSurveyResponse.class);
    }

    /**
     *
     * @param surveyId
     * @param tClass
     * @param <T>
     * @param <U>
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public <T extends AbstractSurveyResult, U extends SurveyResponse<T>> ResponseEntity<U>
         survey(String surveyId, Class<U> tClass) throws IOException, InterruptedException {

        return this.getReqeust(EndPoints.V3.GetSurvey.forSurvey(surveyId), tClass);
    }

    /**
     *
     * @param surveyId
     * @param params
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public ResponseEntity<CreateResponseExportResponse> createResponseExport(String surveyId, CreateResponseExportInput params) throws IOException, InterruptedException {
        String url = this.buildRequestUrl(EndPoints.V3.CreateResponseExport.forSurvey(surveyId));
        HttpEntity entity = new HttpEntity(params, this.genericPostHeaders());
        return this.restClient.exchange(url, HttpMethod.POST, entity, CreateResponseExportResponse.class);
    }
}

