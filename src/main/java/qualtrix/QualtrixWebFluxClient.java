package qualtrix;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import qualtrix.responses.V3.WhoAmI.WhoAmIResponse;
import reactor.core.publisher.Flux;

import java.io.IOException;


@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class QualtrixWebFluxClient extends QualtrixClientBase {
    private final WebClient webClient;

    @Builder
    public QualtrixWebFluxClient(@NonNull String accessToken, WebClient webClient) {
        super(accessToken);
        this.webClient = webClient;
    }

    /**
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public Flux<WhoAmIResponse> whoAmIWeb() throws IOException, InterruptedException {
        return this.webClient
                .get()
                .uri(this.buildRequestUrl(EndPoints.V3.WhoAmI.path()))
                .header("X-API-TOKEN", this.getAccessToken())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(WhoAmIResponse.class);
    }
}
