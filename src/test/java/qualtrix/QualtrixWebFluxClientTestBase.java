package qualtrix;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import qualtrix.exceptions.ExportTimedout;
import qualtrix.responses.V3.CreateContact.CreateContactBody;
import qualtrix.responses.V3.MailingList.CreateMailingListBody;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Stream;

public class QualtrixWebFluxClientTestBase {
  protected static Logger log = null;
  protected static boolean LOG_REQUEST = true;
  protected static String baseUrl = "https://au1.qualtrics.com";

  @BeforeAll
  public static void setLogger() {
    System.setProperty("log4j.configurationFile", "log4j2-testConfig.properties");
    log = LogManager.getLogger(QualtrixWebFluxClientTest.class);
  }

  @FunctionalInterface
  protected interface ClientRun {
    void Run(QualtrixWebFluxClient r) throws IOException, InterruptedException, ExportTimedout;
  }

  protected void runCatchExceptions(
      QualtrixWebFluxClient client, QualtrixWebFluxClientTest.ClientRun f) {
    try {
      f.Run(client);
    } catch (WebClientResponseException e) {
      System.out.println("WebClientResponseException");
      System.out.println(e.getStatusText());
      System.out.println(e.getMessage());
      e.printStackTrace();
      Assert.fail();
    } catch (HttpMessageConversionException e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
      Assert.fail();
    } catch (Exception e) {
      System.out.println("error");
      System.out.println(e);
      e.printStackTrace();
      Assert.fail();
    }
  }

  protected static ExchangeFilterFunction logRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(
        clientRequest -> {
          if (LOG_REQUEST) {
            var sb = new StringBuilder("Request: \n");
            sb.append(clientRequest.url().toASCIIString());
            sb.append(clientRequest.method());
            clientRequest
                .headers()
                .forEach((name, values) -> values.forEach(value -> sb.append(value)));
            log.info(sb.toString());
          }
          return Mono.just(clientRequest);
        });
  }

  protected static WebClient newWebClient() {
    return WebClient.builder()
        .filters(
            exchangeFilterFunctions -> {
              exchangeFilterFunctions.add(logRequest());
            })
        .clientConnector(new ReactorClientHttpConnector(HttpClient.create().wiretap(true)))
        .build();
  }

  protected static QualtrixWebFluxClient newClient(String key) throws IOException {
    return new QualtrixWebFluxClient(baseUrl, key, newWebClient());
  }

  protected static QualtrixWebFluxClient newRateLimitedClient(float requestsPerSecond)
      throws IOException {
    return QualtrixWebFluxClient.createRateLimited(
        baseUrl, TestProperties.getQualtrixTestKey(), newWebClient(), requestsPerSecond);
  }

  protected static QualtrixWebFluxClient newClient() throws IOException {
    return newClient(TestProperties.getQualtrixTestKey());
  }

  protected String createMailingListHelper(QualtrixWebFluxClient c) throws IOException {
    var libraryId = TestProperties.properties().getLibraryId();
    var newCategory = "Qualtrix-SDK-Test";
    var name = UUID.randomUUID().toString();

    var input = new CreateMailingListBody(newCategory, libraryId, name);
    var mailRet = c.createMailingList(input).block();

    Assert.assertEquals(mailRet.getStatusCode(), HttpStatus.OK);
    Assert.assertNotNull(mailRet.getBody().getResult().getId());
    Assert.assertEquals(mailRet.getBody().getMeta().getHttpStatus(), "200 - OK");
    return mailRet.getBody().getResult().getId();
  }

  protected String createMailingListWithContactHelper(QualtrixWebFluxClient c) throws IOException {
    var libraryId = TestProperties.properties().getLibraryId();
    var newCategory = "Qualtrix-SDK-Test";
    var name = UUID.randomUUID().toString();

    var input = new CreateMailingListBody(newCategory, libraryId, name);
    var mailRet = c.createMailingList(input).block();

    // First create a new contact
    var body = new CreateContactBody("test@gmal.com", null, null, "bob", "eng", "getRequest", true);
    var ret = c.createContact(mailRet.getBody().getResult().getId(), body).block();
    Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");

    Assert.assertEquals(mailRet.getStatusCode(), HttpStatus.OK);
    Assert.assertNotNull(mailRet.getBody().getResult().getId());
    Assert.assertEquals(mailRet.getBody().getMeta().getHttpStatus(), "200 - OK");
    return mailRet.getBody().getResult().getId();
  }

  static Stream<QualtrixWebFluxClient> clientProvider() throws IOException {
    return Stream.of(newClient(), newRateLimitedClient(1));
  }

  static String asPrettyJson(Object o) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
  }
}
