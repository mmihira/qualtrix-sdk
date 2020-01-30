package qualtrix;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;

@RequiredArgsConstructor
@Data
class QualtrixClientBase {
  @NonNull private final String accessToken;
  private String qualtrixApiBaseURL = "https://au1.qualtrics.com";

  protected HttpHeaders genericGetHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-API-TOKEN", this.getAccessToken());
    return headers;
  }

  protected HttpHeaders genericPostHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-API-TOKEN", this.getAccessToken());
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  protected String buildRequestUrl(String endpoint) {
    return String.format("%s/%s", this.qualtrixApiBaseURL, endpoint);
  }
}
