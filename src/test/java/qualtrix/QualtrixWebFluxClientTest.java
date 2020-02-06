package qualtrix;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import qualtrix.exceptions.ExportTimedout;
import qualtrix.responses.V3.CreateContact.CreateContactBody;
import qualtrix.responses.V3.GenerateDistributionLink.GenerateDistributionLinksBody;
import qualtrix.responses.V3.MailingList.CreateMailingListBody;
import qualtrix.responses.V3.ResponseExport.CreateResponseExportBody;
import qualtrix.responses.V3.ResponseExport.ResponseExportFormat;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Date;
import java.util.function.Function;

import static org.junit.Assert.assertThrows;

public class QualtrixWebFluxClientTest {
  @FunctionalInterface
  private interface ClientRun {
    void Run(QualtrixWebFluxClient r) throws IOException, InterruptedException, ExportTimedout;
  }

  private void runCatchExceptions(
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

  private QualtrixWebFluxClient newClient(String key) throws IOException {
    var timeoutClient =
        TcpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5 * 1000)
            .doOnConnected(
                c ->
                    c.addHandlerLast(new ReadTimeoutHandler(5))
                        .addHandlerLast(new WriteTimeoutHandler(5)));
    var w =
        WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(HttpClient.from(timeoutClient)))
            .build();
    return new QualtrixWebFluxClient(key, w);
  }

  private QualtrixWebFluxClient newClient() throws IOException {
    return newClient(TestProperties.getQualtrixTestKey());
  }

  @Test
  public void whoAmI() throws IOException {
    runCatchExceptions(
        newClient(),
        c -> {
          var result = c.whoAmI().block();
          Assert.assertNotNull(result);
          Assert.assertEquals(result.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(result.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void whoAmIWrongKey() throws IOException {
    assertThrows(
        WebClientResponseException.class,
        () -> {
          newClient("fakeKey").whoAmI().block();
        });
  }

  @Test
  public void listSurveys() throws IOException {
    runCatchExceptions(
        newClient(),
        c -> {
          var result = c.listSurveys().block();
          Assert.assertNotNull(result);
          Assert.assertEquals(result.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(result.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void survey1() throws IOException {
    runCatchExceptions(
        newClient(),
        c -> {
          var surveyId = TestProperties.properties().getSurveyId();
          var ret = c.survey(surveyId).block();
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void survey2() throws IOException {
    runCatchExceptions(
        newClient(),
        c -> {
          var surveyId = TestProperties.properties().getSurveyId();
          var ret = c.survey(surveyId, G.class).block();
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void createResponseExport() throws IOException {
    runCatchExceptions(
        newClient(),
        c -> {
          var surveyId = TestProperties.properties().getSurveyId();
          var ret =
              c.createResponseExport(
                      surveyId, new CreateResponseExportBody(ResponseExportFormat.json))
                  .block();
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
        });
  }

  /** Here's an example of how you can implement your own custom parse when retrieving a file */
  @Test
  public void createResponseExportFile() throws IOException {
    var surveyId = TestProperties.properties().getSurveyId();
    var fileId = TestProperties.properties().getExportFileId();

    BodyExtractor<Mono<ResponseEntity<String>>, ClientHttpResponse> clientHttpBodyExtractor =
        (clientHttpResponse, unused) -> {
          Flux<ResponseEntity<String>> bodyString =
              clientHttpResponse
                  .getBody()
                  .flatMap(
                      (DataBuffer dataBuffer) -> {
                        return Flux.just(
                            new ResponseEntity<String>(
                                dataBuffer.toString(Charset.defaultCharset()),
                                clientHttpResponse.getStatusCode()));
                      });
          return bodyString.next();
        };

    Function<ClientResponse, Mono<ResponseEntity<String>>> clientResponseHandler =
        clientResponse -> clientResponse.body(clientHttpBodyExtractor);

    runCatchExceptions(
        newClient(),
        c -> {
          var ret = c.createResponseExportFile(surveyId, fileId).flatMap(clientResponseHandler);
          Assert.assertEquals(ret.block().getStatusCode(), HttpStatus.OK);
        });
  }

  @Test
  public void createResponseExportFileObject1() throws IOException {
    var surveyId = TestProperties.properties().getSurveyId();
    var fileId = TestProperties.properties().getExportFileId();
    runCatchExceptions(
        newClient(),
        c -> {
          var ret = c.createResponseExportFileObject(surveyId, fileId).block();
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
        });
  }

  @Test
  public void createResponseExportAndGetFileDefault() throws IOException {
    var surveyId = TestProperties.properties().getSurveyId();
    var fileId = TestProperties.properties().getExportFileId();

    runCatchExceptions(
        newClient(),
        c -> {
          var ret =
              c.createResponseExportAndGetFileDefault(
                      surveyId,
                      Duration.ofMillis(200),
                      new CreateResponseExportBody(ResponseExportFormat.json))
                  .block(Duration.ofSeconds(10));
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
        });
  }

    @Test
    public void createMailingList() throws IOException {
        var libraryId = TestProperties.properties().getLibraryId();
        var newCategory = "Something";
        var name = "New Mailing List";
        runCatchExceptions(
                newClient(),
                c -> {
                    var input = new CreateMailingListBody(newCategory, libraryId, name);
                    var ret = c.createMailingList(input).block();
                    Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
                    Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
                });
    }

    @Test
    public void listMailingList() throws IOException {
        runCatchExceptions(
                newClient(),
                c -> {
                    var ret = c.listMailingList().block();
                    Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
                    Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
                });
    }

    @Test
    public void deleteContact() throws IOException {
        runCatchExceptions(
                newClient(),
                c -> {
                    // First create a new contact
                    var body =
                            new CreateContactBody("test@gmal.com", null, null, "bob", "eng", "getRequest", true);
                    var ret = c.createContact(TestProperties.properties().getMailingListId(), body).block();
                    Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
                    Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
                    var contactId = ret.getBody().getResult().getId();

                    // Now try and delete the contact
                    var del = c.deleteContact(TestProperties.properties().getMailingListId(), contactId).block();
                    Assert.assertEquals(del.getStatusCode(), HttpStatus.OK);
                    Assert.assertEquals(del.getBody().getMeta().getHttpStatus(), "200 - OK");
                });
    }

    @Test
    public void generateDistributionLinks() throws IOException {
        runCatchExceptions(
                newClient(),
                c -> {
                    var body =
                            new GenerateDistributionLinksBody(
                                    TestProperties.properties().getSurveyId(),
                                    "Test link generation",
                                    new Date(),
                                    TestProperties.properties().getMailingListId());
                    var ret = c.generateDistributionLinks(body).block();
                    Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
                    Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
                });
    }

    @Test
    public void retrieveGeneratedLinks() throws IOException {
        runCatchExceptions(
                newClient(),
                c -> {
                    var ret =
                            c.retrieveGeneratedLinks(
                                    TestProperties.properties().getDistributionLinksId(),
                                    TestProperties.properties().getSurveyId()).block();
                    Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
                    Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
                });
    }

}
