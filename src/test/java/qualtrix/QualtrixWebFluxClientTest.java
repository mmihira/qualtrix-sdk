package qualtrix;

import assets.ExampleSurveyResponse;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.web.reactive.function.BodyExtractor;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import qualtrix.responses.V3.CreateContact.CreateContactBody;
import qualtrix.responses.V3.GenerateDistributionLink.GenerateDistributionLinksBody;
import qualtrix.responses.V3.GenerateDistributionLink.GenerateDistributionLinksBodyWithZonedDateTime;
import qualtrix.responses.V3.ResponseExport.CreateResponseExportBody;
import qualtrix.responses.V3.ResponseExport.ResponseExportFormat;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertThrows;

public class QualtrixWebFluxClientTest extends QualtrixWebFluxClientTestBase {
  @ParameterizedTest
  @MethodSource("clientProvider")
  public void whoAmI(QualtrixWebFluxClient client) {
    runCatchExceptions(
        client,
        c -> {
          var result = c.whoAmI().block();
          Assert.assertNotNull(result);
          Assert.assertEquals(result.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(result.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  /**
   * Test that the rateLimiter waits for signals in parallel mode Execute API calls on separate
   * threads and ensure that each waits in turn to get a ticket from the rate limiter.
   */
  @Test
  public void whoAmIRateLimited() throws IOException {
    runCatchExceptions(
        newRateLimitedClient(1),
        c -> {
          long startTime = ZonedDateTime.now().getSecond();

          Flux.fromIterable(List.of(c.whoAmI(), c.whoAmI(), c.whoAmI(), c.whoAmI()))
              .parallel()
              .runOn(Schedulers.parallel())
              .map(Mono::toFuture)
              .sequential()
              .collectList()
              .flatMap(
                  x ->
                      Mono.just(
                          CompletableFuture.allOf(x.toArray(new CompletableFuture[x.size()]))))
              .block()
              .join();

          long elapsedTimeSeconds = ZonedDateTime.now().getSecond() - startTime;
          log.info(String.format("whoAmIRateLimited: Elapsed time = %d", elapsedTimeSeconds));
          Assert.assertEquals(true, elapsedTimeSeconds >= 3);
        });
  }

  @Test
  public void whoAmIWrongKey() {
    assertThrows(
        WebClientResponseException.class,
        () -> {
          newClient("fakeKey").whoAmI().block();
        });
  }

  @ParameterizedTest
  @MethodSource("clientProvider")
  public void listSurveys(QualtrixWebFluxClient client) {
    runCatchExceptions(
        client,
        c -> {
          var result = c.listSurveys().block();
          Assert.assertNotNull(result);
          Assert.assertEquals(result.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(result.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @ParameterizedTest
  @MethodSource("clientProvider")
  public void survey1(QualtrixWebFluxClient client) {
    runCatchExceptions(
        client,
        c -> {
          var surveyId = TestProperties.properties().getSurveyId();
          var ret = c.survey(surveyId).block();
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @ParameterizedTest
  @MethodSource("clientProvider")
  public void survey2(QualtrixWebFluxClient client) {
    runCatchExceptions(
        client,
        c -> {
          var surveyId = TestProperties.properties().getSurveyId();
          var ret = c.survey(surveyId, ExampleSurveyResponse.class).block();
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @ParameterizedTest
  @MethodSource("clientProvider")
  public void createResponseExport(QualtrixWebFluxClient client) {
    runCatchExceptions(
        client,
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
  @ParameterizedTest
  @MethodSource("clientProvider")
  public void createResponseExportFile(QualtrixWebFluxClient client) throws IOException {
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
        client,
        c -> {
          var ret =
              c.createResponseExportFile(surveyId, fileId).flatMap(clientResponseHandler).block();
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
        });
  }

  public class InputStreamCollector {
    private InputStream is;

    public void collectInputStream(InputStream is) {
      if (this.is == null) this.is = is;
      this.is = new SequenceInputStream(this.is, is);
    }

    public InputStream getInputStream() {
      return this.is;
    }
  }

  @ParameterizedTest
  @MethodSource("clientProvider")
  public void createResponseExportFileObject1(QualtrixWebFluxClient client) throws IOException {
    var surveyId = TestProperties.properties().getSurveyId();
    var fileId = TestProperties.properties().getExportFileId();
    runCatchExceptions(
        client,
        c -> {
          var ret = c.createResponseExportFileObject(surveyId, fileId).block();
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
        });
  }

  @ParameterizedTest
  @MethodSource("clientProvider")
  public void createResponseExportAndGetFileDefault(QualtrixWebFluxClient client)
      throws IOException {
    var surveyId = TestProperties.properties().getSurveyId();
    var fileId = TestProperties.properties().getExportFileId();

    runCatchExceptions(
        client,
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

  @ParameterizedTest
  @MethodSource("clientProvider")
  public void createMailingList(QualtrixWebFluxClient client) {
    runCatchExceptions(
        client,
        c -> {
          var mailingListId = createMailingListHelper(c);

          // Get the list
          var mailingList = c.getMailingList(mailingListId).block();
          Assert.assertEquals(mailingList.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(mailingList.getBody().getResult().getId(), mailingListId);

          var delret = c.deleteMailingList(mailingListId).block();
          Assert.assertEquals(delret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(delret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @ParameterizedTest
  @MethodSource("clientProvider")
  public void listMailingList(QualtrixWebFluxClient client) {
    runCatchExceptions(
        client,
        c -> {
          var ret = c.listMailingList().block();
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @ParameterizedTest
  @MethodSource("clientProvider")
  public void deleteContact(QualtrixWebFluxClient client) {
    runCatchExceptions(
        client,
        c -> {
          // Create a new mailing list
          var mailingListId = createMailingListHelper(c);

          // First create a new contact
          var body =
              new CreateContactBody("test@gmal.com", null, null, "bob", "eng", "getRequest", true);
          var ret = c.createContact(mailingListId, body).block();
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
          var contactId = ret.getBody().getResult().getId();

          // Now try and delete the contact
          var del = c.deleteContact(mailingListId, contactId).block();
          Assert.assertEquals(del.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(del.getBody().getMeta().getHttpStatus(), "200 - OK");

          // Delete the mailing list
          var delret = c.deleteMailingList(mailingListId).block();
          Assert.assertEquals(delret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(delret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @ParameterizedTest
  @MethodSource("clientProvider")
  public void createListContacts(QualtrixWebFluxClient client) {
    runCatchExceptions(
        client,
        c -> {
          // Create a new mailing list
          var mailingListId = createMailingListHelper(c);

          // First create a new contact
          var body =
              new CreateContactBody("test@gmal.com", null, null, "bob", "eng", "getRequest", true);
          var ret = c.createContact(mailingListId, body).block();
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
          var contactId = ret.getBody().getResult().getId();

          var contactsList = c.listContacts(mailingListId).block();
          Assert.assertEquals(contactsList.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(contactsList.getBody().getMeta().getHttpStatus(), "200 - OK");

          // Now try and delete the contact
          var del = c.deleteContact(mailingListId, contactId).block();
          Assert.assertEquals(del.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(del.getBody().getMeta().getHttpStatus(), "200 - OK");

          // Delete the mailing list
          var delret = c.deleteMailingList(mailingListId).block();
          Assert.assertEquals(delret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(delret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  private void cleanUpDistributionMailingList(
      QualtrixWebFluxClient c, String distributionId, String mailingListId) {
    // Delete the distribution
    var delRet = c.deleteDistribution(distributionId).block();
    Assert.assertEquals(delRet.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(delRet.getBody().getMeta().getHttpStatus(), "200 - OK");

    // Delete the mailing list
    var delret = c.deleteMailingList(mailingListId).block();
    Assert.assertEquals(delret.getStatusCode(), HttpStatus.OK);
    Assert.assertEquals(delret.getBody().getMeta().getHttpStatus(), "200 - OK");
  }

  @ParameterizedTest
  @MethodSource("clientProvider")
  public void generateRetrieveDeleteDistributionLinks(QualtrixWebFluxClient client) {
    runCatchExceptions(
        client,
        c -> {
          var mailingListId = createMailingListWithContactHelper(c);
          var date =
              LocalDateTime.now()
                  .plus(1, ChronoUnit.DAYS)
                  .plus(8, ChronoUnit.HOURS)
                  .atZone(ZoneId.of("Australia/Melbourne"));

          var body =
              new GenerateDistributionLinksBodyWithZonedDateTime(
                  TestProperties.properties().getSurveyId(),
                  "Test link generation",
                  date,
                  mailingListId);
          var cret = c.generateDistributionLinks(body).block();
          Assert.assertEquals(cret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(cret.getBody().getMeta().getHttpStatus(), "200 - OK");

          var ret =
              c.retrieveGeneratedLinks(
                      cret.getBody().getResult().getId(), TestProperties.properties().getSurveyId())
                  .block();
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");

          // Check the expiry date is correct
          var expiryDate = ret.getBody().getResult().getElements().get(0).getLinkExpiration();
          var expiryDateAtLocal = expiryDate.atZoneSameInstant(ZoneId.of("Australia/Melbourne"));
          Assert.assertTrue(expiryDateAtLocal.equals(date.withNano(0)));

          cleanUpDistributionMailingList(c, cret.getBody().getResult().getId(), mailingListId);
        });
  }

  @ParameterizedTest
  @MethodSource("clientProvider")
  public void generateRetrieveDeleteDistributionLinksStringDate(QualtrixWebFluxClient client) {
    runCatchExceptions(
        client,
        c -> {
          var mailingListId = createMailingListWithContactHelper(c);
          var date = LocalDateTime.now().plus(1, ChronoUnit.DAYS).plus(8, ChronoUnit.HOURS);

          var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
          var body =
              new GenerateDistributionLinksBody(
                  TestProperties.properties().getSurveyId(),
                  "Test link generation",
                  dateFormatter.format(date),
                  mailingListId);

          var cret = c.generateDistributionLinks(body).block();
          Assert.assertEquals(cret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(cret.getBody().getMeta().getHttpStatus(), "200 - OK");

          var ret =
              c.retrieveGeneratedLinks(
                      cret.getBody().getResult().getId(), TestProperties.properties().getSurveyId())
                  .block();
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");

          // Check the expiry date is correct
          var expiryDate = ret.getBody().getResult().getElements().get(0).getLinkExpiration();
          var expiryDateAtMountainTime =
              expiryDate.atZoneSameInstant(ZoneId.of("America/Chihuahua"));
          Assert.assertEquals(
              dateFormatter.format(date), dateFormatter.format(expiryDateAtMountainTime));

          cleanUpDistributionMailingList(c, cret.getBody().getResult().getId(), mailingListId);
        });
  }
}
