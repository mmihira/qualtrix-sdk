package qualtrix;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.web.reactive.function.client.WebClient;
import qualtrix.responses.V3.Survey.AbstractSurveyResult;
import qualtrix.responses.V3.Survey.DefaultSurveyResponse;
import qualtrix.responses.V3.Survey.SurveyResponse;
import qualtrix.responses.V3.SurveyList.SurveyListResponse;
import qualtrix.responses.V3.WhoAmI.WhoAmIResponse;
import reactor.core.publisher.Flux;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QualtrixWebFluxClient extends QualtrixClientBase {
  private final WebClient webClient;

  @Builder
  public QualtrixWebFluxClient(@NonNull String accessToken, WebClient webClient) {
    super(accessToken);
    this.webClient = webClient;
  }

  public WebClient.RequestHeadersSpec<?> getRequest(String path) {
    return this.webClient
        .get()
        .uri(this.buildRequestUrl(path))
        .header("X-API-TOKEN", this.getAccessToken());
  }

  public WebClient.RequestHeadersSpec<?> postRequest(String path, Object body) {
    return this.webClient
        .post()
        .uri(this.buildRequestUrl(path))
        .bodyValue(body)
        .header("X-API-TOKEN", this.getAccessToken());
  }

  public Flux<WhoAmIResponse> whoAmIWeb() {
    return this.getRequest(EndPoints.V3.WhoAmI.path()).retrieve().bodyToFlux(WhoAmIResponse.class);
  }

  public Flux<SurveyListResponse> listSurveys() {
    return this.getRequest(EndPoints.V3.ListSurveys.path())
        .retrieve()
        .bodyToFlux(SurveyListResponse.class);
  }

  public Flux<DefaultSurveyResponse> survey(String surveyId) {
    return this.getRequest(EndPoints.V3.GetSurvey.forSurvey(surveyId))
        .retrieve()
        .bodyToFlux(DefaultSurveyResponse.class);
  }

  public <T extends AbstractSurveyResult, U extends SurveyResponse<T>> Flux<U> survey(
      String surveyId, Class<U> tClass) {

    return this.getRequest(EndPoints.V3.GetSurvey.forSurvey(surveyId))
        .retrieve()
        .bodyToFlux(tClass);
  }
}
