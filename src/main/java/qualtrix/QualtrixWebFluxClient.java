package qualtrix;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.RateLimiter;
import lombok.*;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import qualtrix.exceptions.RateLimitAcquireFailed;
import qualtrix.responses.V3.CreateContact.CreateContactBody;
import qualtrix.responses.V3.CreateContact.CreateContactResponse;
import qualtrix.responses.V3.DeleteContact.DeleteContactResponse;
import qualtrix.responses.V3.DeleteDistribution.DeleteDistributionResponse;
import qualtrix.responses.V3.DeleteMailingList.DeleteMailingListResponse;
import qualtrix.responses.V3.GenerateDistributionLink.AbstractGenerateDistributionLinksBody;
import qualtrix.responses.V3.GenerateDistributionLink.GenerateDistributionLinkResponse;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.zip.ZipInputStream;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QualtrixWebFluxClient extends QualtrixClientBase {
  private final WebClient webClient;
  private final RateLimiter rateLimiter;
  private Duration internalRateLimiterCheckBackOff = Duration.ofMillis(1);

  @NoArgsConstructor
  class InCompleteException extends Exception {
    public InCompleteException(String message) {
      super(message);
    }
  }

  @Builder
  public QualtrixWebFluxClient(
      @NonNull String accessToken,
      WebClient webClient,
      RateLimiter rateLimiter,
      Duration internalRateLimiterCheckBackOff) {

    super(accessToken);
    this.webClient = webClient;
    this.rateLimiter = rateLimiter;
    this.internalRateLimiterCheckBackOff = internalRateLimiterCheckBackOff;
  }

  public QualtrixWebFluxClient(@NonNull String accessToken, WebClient webClient) {

    super(accessToken);
    this.webClient = webClient;
    this.rateLimiter = null;
  }

  private QualtrixWebFluxClient(
      @NonNull String accessToken, WebClient webClient, RateLimiter rateLimiter) {
    super(accessToken);
    this.rateLimiter = rateLimiter;
    this.webClient = webClient;
  }

  public static QualtrixWebFluxClient createRateLimited(
      @NonNull String accessToken, WebClient webClient, float requestsPerSecond) {
    return new QualtrixWebFluxClient(accessToken, webClient, RateLimiter.create(requestsPerSecond));
  }

  private Mono<Boolean> waitRate() {
    if (this.rateLimiter == null) {
      // Not rate limited
      return Mono.just(true);
    } else {
      return Flux.defer(
              () -> {
                if (rateLimiter.tryAcquire()) {
                  return Mono.just(true);
                } else {
                  return Mono.error(new RateLimitAcquireFailed("Acquire Failed"));
                }
              })
          .retryWhen(Retry.any().fixedBackoff(this.internalRateLimiterCheckBackOff))
          .onErrorContinue(RateLimitAcquireFailed.class, (z, x) -> {})
          .next();
    }
  }

  public RequestHeadersSpec<?> getRequest(String path) {
    return this.webClient
        .get()
        .uri(this.buildRequestUrl(path))
        .header("X-API-TOKEN", this.getAccessToken());
  }

  public RequestHeadersSpec<?> postRequest(String path, Object body) {
    return this.webClient
        .post()
        .uri(this.buildRequestUrl(path))
        .bodyValue(body)
        .header("X-API-TOKEN", this.getAccessToken())
        .header("Content-Type", MediaType.APPLICATION_JSON.toString());
  }

  public RequestHeadersSpec<?> deleteRequest(String path) {
    return this.webClient
        .delete()
        .uri(this.buildRequestUrl(path))
        .header("X-API-TOKEN", this.getAccessToken())
        .header("Content-Type", MediaType.APPLICATION_JSON.toString());
  }

  private <V> Mono<V> waitRateThen(Supplier<Mono<V>> f) {
    return this.waitRate().then(f.get());
  }

  public Mono<ResponseEntity<WhoAmIResponse>> whoAmI() {
    return this.waitRateThen(
        () ->
            this.getRequest(EndPoints.V3.WhoAmI.path()).retrieve().toEntity(WhoAmIResponse.class));
  }

  public Mono<ResponseEntity<SurveyListResponse>> listSurveys() {
    return this.waitRateThen(
        () ->
            this.getRequest(EndPoints.V3.ListSurveys.path())
                .retrieve()
                .toEntity(SurveyListResponse.class));
  }

  public Mono<ResponseEntity<DefaultSurveyResponse>> survey(String surveyId) {
    return this.waitRateThen(
        () ->
            this.getRequest(EndPoints.V3.GetSurvey.forSurvey(surveyId))
                .retrieve()
                .toEntity(DefaultSurveyResponse.class));
  }

  public <T extends AbstractSurveyResult, U extends SurveyResponse<T>>
      Mono<ResponseEntity<U>> survey(String surveyId, Class<U> tClass) {

    return this.waitRateThen(
        () ->
            this.getRequest(EndPoints.V3.GetSurvey.forSurvey(surveyId))
                .retrieve()
                .toEntity(tClass));
  }

  public <T extends AbstractCreateResponseExportBody>
      Mono<ResponseEntity<CreateResponseExportResponse>> createResponseExport(
          String surveyId, T body) {

    var endpoint = EndPoints.V3.CreateResponseExport.forSurvey(surveyId);
    return this.waitRateThen(
        () ->
            this.postRequest(endpoint, body)
                .retrieve()
                .toEntity(CreateResponseExportResponse.class));
  }

  public Mono<ResponseEntity<ResponseExportProgressResponse>> responseExportProgress(
      String surveyId, String exportProgressId) {

    return this.waitRateThen(
        () ->
            this.getRequest(EndPoints.V3.ResponseExportProgress.path(surveyId, exportProgressId))
                .retrieve()
                .toEntity(ResponseExportProgressResponse.class));
  }

  public Mono<ClientResponse> createResponseExportFile(String surveyId, String fileId) {
    var url = this.buildRequestUrl(EndPoints.V3.ResponseExportFile.path(surveyId, fileId));
    return this.waitRateThen(
        () ->
            this.webClient
                .get()
                .uri(url)
                .header("X-API-TOKEN", this.getAccessToken())
                .header("Content-Type", MediaType.APPLICATION_JSON.toString())
                .exchange());
  }

  /** Check the return code before consuming the response body */
  public <
          T extends AbstractResponseExportFileEntity,
          U extends AbstractResponseExportFileResponse<T>>
      Mono<ResponseEntity<U>> createResponseExportFileObject(
          String surveyId, String fileId, Class<U> uClass) {

    Function<ClientHttpResponse, Function<DataBuffer, Flux<ResponseEntity<U>>>> parseDataBuffer =
        clientHttpResponse ->
            dataBuffer -> {
              try (ZipInputStream zipInputStream = new ZipInputStream(dataBuffer.asInputStream())) {
                zipInputStream.getNextEntry();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                U ret = objectMapper.readValue(zipInputStream, uClass);
                return Flux.just(
                    new ResponseEntity<U>(
                        ret, clientHttpResponse.getHeaders(), clientHttpResponse.getStatusCode()));
              } catch (IOException e) {
                return Flux.error(e);
              }
            };

    BodyExtractor<Mono<ResponseEntity<U>>, ClientHttpResponse> clientResponseBodyExtractor =
        (clientHttpResponse, unused) -> {
          return clientHttpResponse
              .getBody()
              .flatMap(parseDataBuffer.apply(clientHttpResponse))
              .next();
        };

    Function<ClientResponse, Mono<ResponseEntity<U>>> clientResponseHandler =
        clientResponse -> clientResponse.body(clientResponseBodyExtractor);

    return this.waitRateThen(
        () -> this.createResponseExportFile(surveyId, fileId).flatMap(clientResponseHandler));
  }

  public Mono<ResponseEntity<DefaultResponseExportFileResponse>> createResponseExportFileObject(
      String surveyId, String fileId) {

    return this.waitRateThen(
        () ->
            createResponseExportFileObject(
                surveyId, fileId, DefaultResponseExportFileResponse.class));
  }

  /**
   * @param surveyId Responses will be exported for this surveyId
   * @param retryInterval Interval between successive calls to check the progress of the export
   * @param body
   * @param uClass
   */
  public <
          T extends AbstractResponseExportFileEntity,
          U extends AbstractResponseExportFileResponse<T>,
          G extends AbstractCreateResponseExportBody>
      Mono<ResponseEntity<U>> createResponseExportAndGetFile(
          String surveyId, Duration retryInterval, G body, Class<U> uClass) {

    // Don't need to cache here but make the intention to only create the export
    // once explicit
    var export = this.createResponseExport(surveyId, body).cache();

    Function<
            ResponseEntity<CreateResponseExportResponse>,
            Mono<ResponseEntity<ResponseExportProgressResponse>>>
        getProgress =
            response ->
                this.responseExportProgress(
                    surveyId, response.getBody().getResult().getProgressId());

    Function<ResponseEntity<ResponseExportProgressResponse>, Mono<String>> getFileId =
        response ->
            Optional.ofNullable(response.getBody().getResult().getFileId())
                .map(fileId -> Mono.just(fileId))
                .orElse(Mono.error(new InCompleteException()));

    return this.waitRateThen(
        () ->
            Flux.defer(() -> export.flatMap(getProgress).flatMap(getFileId))
                .retryWhen(Retry.any().fixedBackoff(retryInterval))
                .onErrorContinue(InCompleteException.class, (z, x) -> {})
                .flatMap(f -> createResponseExportFileObject(surveyId, f, uClass))
                .next());
  }

  /**
   * @param surveyId Responses will be exported for this surveyId
   * @param retryInterval Interval between successive calls to check the progress of the export
   * @param body
   */
  public <G extends AbstractCreateResponseExportBody>
      Mono<ResponseEntity<DefaultResponseExportFileResponse>> createResponseExportAndGetFileDefault(
          String surveyId, Duration retryInterval, G body) {

    return this.waitRateThen(
        () ->
            createResponseExportAndGetFile(
                surveyId, retryInterval, body, DefaultResponseExportFileResponse.class));
  }

  public Mono<ResponseEntity<CreateMailingListResponse>> createMailingList(
      CreateMailingListBody body) {

    return this.waitRateThen(
        () ->
            this.postRequest(EndPoints.V3.CreateMailingList.path(), body)
                .retrieve()
                .toEntity(CreateMailingListResponse.class));
  }

  public Mono<ResponseEntity<ListMailingListsResponse>> listMailingList() {

    return this.waitRateThen(
        () ->
            this.getRequest(EndPoints.V3.ListMailingLists.path())
                .retrieve()
                .toEntity(ListMailingListsResponse.class));
  }

  public Mono<ResponseEntity<CreateContactResponse>> createContact(
      String mailingListId, CreateContactBody body) {

    return this.waitRateThen(
        () ->
            this.postRequest(EndPoints.V3.CreateContact.path(mailingListId), body)
                .retrieve()
                .toEntity(CreateContactResponse.class));
  }

  public Mono<ResponseEntity<ListContactsResponse>> listContacts(String mailingListId) {

    return this.waitRateThen(
        () ->
            this.getRequest(EndPoints.V3.ListContacts.path(mailingListId))
                .retrieve()
                .toEntity(ListContactsResponse.class));
  }

  public Mono<ResponseEntity<DeleteMailingListResponse>> deleteMailingList(String mailingListId) {

    return this.waitRateThen(
        () ->
            this.deleteRequest(EndPoints.V3.DeleteMailingList.path(mailingListId))
                .retrieve()
                .toEntity(DeleteMailingListResponse.class));
  }

  public Mono<ResponseEntity<GetMailingListResponse>> getMailingList(String mailingListId) {

    return this.waitRateThen(
        () ->
            this.getRequest(EndPoints.V3.GetMailingList.path(mailingListId))
                .retrieve()
                .toEntity(GetMailingListResponse.class));
  }

  public Mono<ResponseEntity<DeleteContactResponse>> deleteContact(
      String mailingListId, String contactId) {

    return this.waitRateThen(
        () ->
            this.deleteRequest(EndPoints.V3.DeleteContact.path(mailingListId, contactId))
                .retrieve()
                .toEntity(DeleteContactResponse.class));
  }

  /**
   * The Qualtrix API for generating distribution links accepts a specific date time format which is
   * different from the rest of the API and it is interpreted as MST("America/Chihuahua")
   * https://api.qualtrics.com/reference#distribution-create-1
   * To get the correct date one option is to use {@link qualtrix.responses.V3.GenerateDistributionLink.GenerateDistributionLinksBodyWithZonedDateTime}
   * in the request body and zone the required expiry date to your timezone. The library will convert to
   * MST for you. The other option is to use {@link qualtrix.responses.V3.GenerateDistributionLink.GenerateDistributionLinksBody} and set the date
   * as a string yourself. Note the return expiry date will be given in UTC time.
   */
  public <T extends AbstractGenerateDistributionLinksBody>
      Mono<ResponseEntity<GenerateDistributionLinkResponse>> generateDistributionLinks(T body) {

    return this.waitRateThen(
        () ->
            this.postRequest(EndPoints.V3.GenerateDistributionLinks.path(), body)
                .retrieve()
                .toEntity(GenerateDistributionLinkResponse.class));
  }

  public Mono<ResponseEntity<RetrieveGeneratedLinksResponse>> retrieveGeneratedLinks(
      String distributionId, String surveyId) {

    return this.waitRateThen(
        () ->
            QualtrixWebFluxClient.this
                .getRequest(EndPoints.V3.RetrieveGeneratedLinks.path(distributionId, surveyId))
                .retrieve()
                .toEntity(RetrieveGeneratedLinksResponse.class));
  }

  public Mono<ResponseEntity<DeleteDistributionResponse>> deleteDistribution(
      String distributionId) {

    return this.waitRateThen(
        () ->
            QualtrixWebFluxClient.this
                .deleteRequest(EndPoints.V3.DeleteDistribution.path(distributionId))
                .retrieve()
                .toEntity(DeleteDistributionResponse.class));
  }
}
