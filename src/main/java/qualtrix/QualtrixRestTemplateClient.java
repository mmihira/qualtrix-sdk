package qualtrix;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import qualtrix.exceptions.ExportTimedout;
import qualtrix.responses.V3.CreateContact.CreateContactBody;
import qualtrix.responses.V3.CreateContact.CreateContactResponse;
import qualtrix.responses.V3.DeleteContact.DeleteContactResponse;
import qualtrix.responses.V3.DeleteMailingList.DeleteMailingListResponse;
import qualtrix.responses.V3.GenerateDistributionLink.GenerateDistributionLinkResponse;
import qualtrix.responses.V3.GenerateDistributionLink.GenerateDistributionLinksBody;
import qualtrix.responses.V3.GetMailingList.GetMailingListResponse;
import qualtrix.responses.V3.ListContacts.ListContactsResponse;
import qualtrix.responses.V3.ListMailingLists.ListMailingListsResponse;
import qualtrix.responses.V3.MailingList.CreateMailingListBody;
import qualtrix.responses.V3.MailingList.CreateMailingListResponse;
import qualtrix.responses.V3.ResponseExport.AbstractCreateResponseExportBody;
import qualtrix.responses.V3.ResponseExport.CreateResponseExportResponse;
import qualtrix.responses.V3.ResponseExportFile.AbstractResponseExportFileEntity;
import qualtrix.responses.V3.ResponseExportFile.AbstractResponseExportFileResponse;
import qualtrix.responses.V3.ResponseExportFile.DefaultResponseExportFileResponse;
import qualtrix.responses.V3.ResponseExportProgress.ResponseExportProgressResponse;
import qualtrix.responses.V3.RetrieveGeneratedLinks.RetrieveGeneratedLinksResponse;
import qualtrix.responses.V3.Survey.AbstractSurveyResult;
import qualtrix.responses.V3.Survey.DefaultSurveyResponse;
import qualtrix.responses.V3.Survey.SurveyResponse;
import qualtrix.responses.V3.SurveyList.SurveyListResponse;
import qualtrix.responses.V3.WhoAmI.WhoAmIResponse;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.zip.ZipInputStream;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QualtrixRestTemplateClient extends QualtrixClientBase {
  @NonNull private final RestTemplate restClient;

  @Builder
  public QualtrixRestTemplateClient(@NonNull String accessToken, @NonNull RestTemplate restClient) {

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

  public ResponseEntity<DefaultSurveyResponse> survey(String surveyId)
      throws IOException, InterruptedException {

    return survey(surveyId, DefaultSurveyResponse.class);
  }

  public <T extends AbstractSurveyResult, U extends SurveyResponse<T>> ResponseEntity<U> survey(
      String surveyId, Class<U> tClass) throws IOException, InterruptedException {

    return this.getRequest(EndPoints.V3.GetSurvey.forSurvey(surveyId), tClass);
  }

  public <T extends AbstractCreateResponseExportBody>
      ResponseEntity<CreateResponseExportResponse> createResponseExport(String surveyId, T body)
          throws IOException, InterruptedException {

    String url = this.buildRequestUrl(EndPoints.V3.CreateResponseExport.forSurvey(surveyId));
    HttpEntity<T> entity = new HttpEntity<>(body, this.genericPostHeaders());
    return this.restClient.exchange(
        url, HttpMethod.POST, entity, CreateResponseExportResponse.class);
  }

  public ResponseEntity<ResponseExportProgressResponse> responseExportProgress(
      String surveyId, String exportProgressId) throws IOException, InterruptedException {

    return this.getRequest(
        EndPoints.V3.ResponseExportProgress.path(surveyId, exportProgressId),
        ResponseExportProgressResponse.class);
  }

  public <T> ResponseEntity<T> createResponseExportFile(
      String surveyId, String fileId, ResponseExtractor<ResponseEntity<T>> f) throws IOException {

    String url = this.buildRequestUrl(EndPoints.V3.ResponseExportFile.path(surveyId, fileId));
    return this.restClient.execute(
        url, HttpMethod.GET, nn -> nn.getHeaders().putAll(this.genericGetHeaders()), f);
  }

  public <
          T extends AbstractResponseExportFileEntity,
          U extends AbstractResponseExportFileResponse<T>>
      ResponseEntity<U> createResponseExportFileObject(
          String surveyId, String fileId, Class<U> uClass) throws IOException {

    return createResponseExportFile(
        surveyId,
        fileId,
        clientHttpResponse -> {
          try (ZipInputStream zipInputStream = new ZipInputStream(clientHttpResponse.getBody())) {
            zipInputStream.getNextEntry();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            U ret = objectMapper.readValue(zipInputStream, uClass);
            return new ResponseEntity<>(
                ret, clientHttpResponse.getHeaders(), clientHttpResponse.getStatusCode());
          }
        });
  }

  public ResponseEntity<DefaultResponseExportFileResponse> createResponseExportFileObject(
      String surveyId, String fileId) throws IOException {

    return createResponseExportFileObject(
        surveyId, fileId, DefaultResponseExportFileResponse.class);
  }

  /**
   * Request export of survey responses and also retrieve it. Waiting for an export timeouts after
   * the specified time.
   */
  public <
          T extends AbstractResponseExportFileEntity,
          U extends AbstractResponseExportFileResponse<T>,
          G extends AbstractCreateResponseExportBody>
      ResponseEntity<U> createResponseExportAndGetFile(
          String surveyId, G body, Duration timeout, Class<U> uClass)
          throws IOException, InterruptedException, ExportTimedout {

    var start = Instant.now();
    var export = this.createResponseExport(surveyId, body);

    while (true) {
      var progress =
          this.responseExportProgress(surveyId, export.getBody().getResult().getProgressId());
      if (progress.getBody().getResult().isComplete()) {
        return this.createResponseExportFileObject(
            surveyId, progress.getBody().getResult().getFileId(), uClass);
      }
      var elapsed = Duration.between(start, Instant.now());
      if (elapsed.compareTo(timeout) > 0) {
        throw new ExportTimedout(
            String.format("Exporting results timedout after %d seconds", elapsed.toSeconds()));
      }
    }
  }

  public <G extends AbstractCreateResponseExportBody, T>
      ResponseEntity<T> createResponseExportAndGetFile(
          String surveyId, Duration timeout, G body, ResponseExtractor<ResponseEntity<T>> extractor)
          throws IOException, InterruptedException, ExportTimedout {

    var export = this.createResponseExport(surveyId, body);
    var start = Instant.now();

    while (true) {
      var progress =
          this.responseExportProgress(surveyId, export.getBody().getResult().getProgressId());
      if (progress.getBody().getResult().isComplete()) {
        var fileId = progress.getBody().getResult().getFileId();
        return createResponseExportFile(surveyId, fileId, extractor);
      }
      var elapsed = Duration.between(start, Instant.now());
      if (elapsed.compareTo(timeout) > 0) {
        throw new ExportTimedout(
            String.format("Exporting results timedout after %d seconds", elapsed.toSeconds()));
      }
    }
  }

  public <G extends AbstractCreateResponseExportBody>
      ResponseEntity<DefaultResponseExportFileResponse> createResponseExportAndGetFileDefault(
          String surveyId, Duration timeout, G body)
          throws IOException, InterruptedException, ExportTimedout {

    return this.createResponseExportAndGetFile(
        surveyId, body, timeout, DefaultResponseExportFileResponse.class);
  }

  public ResponseEntity<CreateMailingListResponse> createMailingList(CreateMailingListBody body) {
    String url = this.buildRequestUrl(EndPoints.V3.CreateMailingList.path());
    var entity = new HttpEntity<>(body, this.genericPostHeaders());
    return this.restClient.exchange(url, HttpMethod.POST, entity, CreateMailingListResponse.class);
  }

  public ResponseEntity<DeleteMailingListResponse> deleteMailingList(String mailingListId) {
    String url = this.buildRequestUrl(EndPoints.V3.DeleteMailingList.path(mailingListId));
    var entity = new HttpEntity<>(this.genericPostHeaders());
    return this.restClient.exchange(
        url, HttpMethod.DELETE, entity, DeleteMailingListResponse.class);
  }

  public ResponseEntity<ListMailingListsResponse> listMailingList() {
    return this.getRequest(EndPoints.V3.ListMailingLists.path(), ListMailingListsResponse.class);
  }

  public ResponseEntity<GetMailingListResponse> getMailingList(String mailingListId) {
    return this.getRequest(
        EndPoints.V3.GetMailingList.path(mailingListId), GetMailingListResponse.class);
  }

  public ResponseEntity<CreateContactResponse> createContact(
      String mailingListId, CreateContactBody body) {

    String url = this.buildRequestUrl(EndPoints.V3.CreateContact.path(mailingListId));
    var entity = new HttpEntity<>(body, this.genericPostHeaders());
    return this.restClient.exchange(url, HttpMethod.POST, entity, CreateContactResponse.class);
  }

  public ResponseEntity<ListContactsResponse> listContacts(String mailingListId) {

    return this.getRequest(
        EndPoints.V3.ListContacts.path(mailingListId), ListContactsResponse.class);
  }

  public ResponseEntity<DeleteContactResponse> deleteContact(
      String mailingListId, String contactId) {

    String url = this.buildRequestUrl(EndPoints.V3.DeleteContact.path(mailingListId, contactId));
    var entity = new HttpEntity<>(this.genericPostHeaders());
    return this.restClient.exchange(url, HttpMethod.DELETE, entity, DeleteContactResponse.class);
  }

  public ResponseEntity<GenerateDistributionLinkResponse> generateDistributionLinks(
      GenerateDistributionLinksBody body) {

    String url = this.buildRequestUrl(EndPoints.V3.GenerateDistributionLinks.path());
    var entity = new HttpEntity<>(body, this.genericPostHeaders());
    return this.restClient.exchange(
        url, HttpMethod.POST, entity, GenerateDistributionLinkResponse.class);
  }

  public ResponseEntity<RetrieveGeneratedLinksResponse> retrieveGeneratedLinks(
      String distributionId, String surveyId) {

    String url = this.buildRequestUrl(EndPoints.V3.RetrieveGeneratedLinks.path(distributionId));
    var entity = new HttpEntity<>(this.genericPostHeaders());
    return this.restClient.exchange(
        url, HttpMethod.GET, entity, RetrieveGeneratedLinksResponse.class, surveyId);
  }
}
