package qualtrix;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.junit.Test;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import qualtrix.responses.V3.WhoAmI.WhoAmIResponse;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.Map;

public class QualtrixWebFluxClientTest {

  @Test
  public void testWhoAmIWebClient() {
    RestTemplate restTemplate = new RestTemplate();
    Map<String, String> env = System.getenv();
    TcpClient timeoutClient =
        TcpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5 * 1000)
            .doOnConnected(
                c ->
                    c.addHandlerLast(new ReadTimeoutHandler(5))
                        .addHandlerLast(new WriteTimeoutHandler(5)));
    WebClient w =
        WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(HttpClient.from(timeoutClient)))
            .build();
    QualtrixWebFluxClient client =
        new QualtrixWebFluxClient("1wsvDXM7sMvAbuTsoxBOIu8DjrI4Mdv4He5Lt6pb", w);
    try {
      Flux<WhoAmIResponse> ret = client.whoAmIWeb();
      var f = ret.blockFirst();
      System.out.println(f);
    } catch (WebClientResponseException e) {
      System.out.println("here");
      System.out.println(e.getStatusText());
      System.out.println(e.getMessage());
      System.out.println(e.getHeaders());
      System.out.println(e.getResponseBodyAsString());
    } catch (HttpMessageConversionException e) {
      System.out.println(e.getMessage());
    } catch (Exception e) {
      System.out.println("error");
      System.out.println(e);
    }
  }
}
