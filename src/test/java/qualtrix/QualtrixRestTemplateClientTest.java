package qualtrix;

import com.google.common.io.ByteStreams;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import qualtrix.exceptions.ExportTimedout;
import qualtrix.responses.V3.CreateContact.CreateContactBody;
import qualtrix.responses.V3.GenerateDistributionLink.GenerateDistributionLinksBody;
import qualtrix.responses.V3.MailingList.CreateMailingListBody;
import qualtrix.responses.V3.ResponseExport.CreateResponseExportBody;
import qualtrix.responses.V3.ResponseExport.CreateResponseExportBodyFilterQuestions;
import qualtrix.responses.V3.ResponseExport.ResponseExportFormat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertThrows;

public class QualtrixRestTemplateClientTest {

  @FunctionalInterface
  private interface ClientRun {
    void Run(QualtrixRestTemplateClient r) throws IOException, InterruptedException, ExportTimedout;
  }

  private class ByteArrayClientResponseExtractor {
    public ResponseExtractor<ResponseEntity<ByteArrayOutputStream>> invoke() {
      return clientHttpResponse -> {
        try (ZipInputStream zipInputStream = new ZipInputStream(clientHttpResponse.getBody())) {
          zipInputStream.getNextEntry();
          try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ByteStreams.copy(zipInputStream, out);
            return new ResponseEntity<>(
                out, clientHttpResponse.getHeaders(), clientHttpResponse.getStatusCode());
          }
        }
      };
    }
  }

  private void runCatchExceptions(QualtrixRestTemplateClient client, ClientRun f) {
    try {
      f.Run(client);
    } catch (HttpClientErrorException e) {
      System.out.println("HttpClientErrorException");
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

  private QualtrixRestTemplateClient newClient() throws IOException {
    var restTemplate = new RestTemplate();
    return new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
  }

  private String createMailingListHelper(QualtrixRestTemplateClient c) throws IOException {
    var libraryId = TestProperties.properties().getLibraryId();
    var newCategory = "Qualtrix-SDK-Test";
    var name = UUID.randomUUID().toString();
    // First create  a mailing list
    var input = new CreateMailingListBody(newCategory, libraryId, name);
    var mailRet = c.createMailingList(input);
    Assert.assertEquals(mailRet.getStatusCode(), HttpStatus.OK);
    Assert.assertNotNull(mailRet.getBody().getResult().getId());
    Assert.assertEquals(mailRet.getBody().getMeta().getHttpStatus(), "200 - OK");
    return mailRet.getBody().getResult().getId();
  }

  @Test
  public void whoAmI() throws IOException {
    runCatchExceptions(
        newClient(),
        c -> {
          var result = c.whoAmI();
          Assert.assertEquals(result.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(result.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void listSurveys() throws IOException {
    runCatchExceptions(
        newClient(),
        c -> {
          var ret = c.listSurveys();
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void survey1() throws IOException {
    runCatchExceptions(
        newClient(),
        c -> {
          var ret = c.survey("SV_1MLZsPfqxmInXyB");
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void survey2() throws IOException {
    var surveyId = TestProperties.properties().getSurveyId();
    runCatchExceptions(
        newClient(),
        c -> {
          var ret = c.survey(surveyId, G.class);
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void createResponseExport() throws IOException {
    var surveyId = TestProperties.properties().getSurveyId();
    runCatchExceptions(
        newClient(),
        c -> {
          var ret =
              c.createResponseExport(
                  surveyId, new CreateResponseExportBody(ResponseExportFormat.json));
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
        });
  }

  @Test
  public void createResponseExportFileObject1() throws IOException {
    var surveyId = TestProperties.properties().getSurveyId();
    var fileId = TestProperties.properties().getExportFileId();
    runCatchExceptions(
        newClient(),
        c -> {
          var ret = c.createResponseExportFileObject(surveyId, fileId);
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
        });
  }

  @Test
  public void createResponseExportAndGetFileDefault() throws IOException {
    var surveyId = TestProperties.properties().getSurveyId();
    runCatchExceptions(
        newClient(),
        c -> {
          var ret =
              c.createResponseExportAndGetFileDefault(
                  surveyId,
                  Duration.ofSeconds(10),
                  new CreateResponseExportBody(ResponseExportFormat.json));
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
        });
  }

  @Test
  public void createResponseExportAndGetFileDefaultThrowsTimedout() throws IOException {
    var surveyId = TestProperties.properties().getSurveyId();
    var client = newClient();
    Exception exception =
        assertThrows(
            ExportTimedout.class,
            () -> {
              var ret =
                  client.createResponseExportAndGetFileDefault(
                      surveyId,
                      Duration.ofMillis(1),
                      new CreateResponseExportBody(ResponseExportFormat.json));
              Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
            });
  }

  /** Example of reading the export file to a raw byte stream */
  @Test
  public void createResponseExportAndGetFile() throws IOException {
    var surveyId = TestProperties.properties().getSurveyId();
    runCatchExceptions(
        newClient(),
        c -> {
          var exportBody =
              new CreateResponseExportBodyFilterQuestions(ResponseExportFormat.json, List.of());
          ResponseEntity<ByteArrayOutputStream> ret =
              c.createResponseExportAndGetFile(
                  surveyId,
                  Duration.ofSeconds(10),
                  exportBody,
                  new ByteArrayClientResponseExtractor().invoke());
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
        });
  }

  @Test
  public void createResponseExportAndGetFileTimedOut() throws IOException {
    var surveyId = TestProperties.properties().getSurveyId();
    var client = newClient();
    Exception exception =
        assertThrows(
            ExportTimedout.class,
            () -> {
              var exportBody =
                  new CreateResponseExportBodyFilterQuestions(ResponseExportFormat.json, List.of());
              ResponseEntity<ByteArrayOutputStream> ret =
                  client.createResponseExportAndGetFile(
                      surveyId,
                      Duration.ofMillis(1),
                      exportBody,
                      new ByteArrayClientResponseExtractor().invoke());
              Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
            });
  }

  @Test
  public void createMailingList() throws IOException {
    var libraryId = TestProperties.properties().getLibraryId();
    var newCategory = "Qualtrix-SDK-Test";
    var name = UUID.randomUUID().toString();
    runCatchExceptions(
        newClient(),
        c -> {
          var input = new CreateMailingListBody(newCategory, libraryId, name);
          var ret = c.createMailingList(input);
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertNotNull(ret.getBody().getResult().getId());
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");

          // Get the list
          var mailingListId = ret.getBody().getResult().getId();
          var mailingList = c.getMailingList(mailingListId);
          Assert.assertEquals(mailingList.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(mailingList.getBody().getResult().getId(), mailingListId);

          var delret = c.deleteMailingList(ret.getBody().getResult().getId());
          Assert.assertEquals(delret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(delret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void listMailingList() throws IOException {
    runCatchExceptions(
        newClient(),
        c -> {
          var ret = c.listMailingList();
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void deleteContact() throws IOException {
    runCatchExceptions(
        newClient(),
        c -> {
          var mailListId = createMailingListHelper(c);

          // First create a new contact
          var body =
              new CreateContactBody("test@gmal.com", null, null, "bob", "eng", "getRequest", true);
          var ret = c.createContact(mailListId, body);
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
          var contactId = ret.getBody().getResult().getId();

          // Now try and delete the contact
          var del = c.deleteContact(mailListId, contactId);
          Assert.assertEquals(del.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(del.getBody().getMeta().getHttpStatus(), "200 - OK");

          // Delete the mailing list
          var delret = c.deleteMailingList(mailListId);
          Assert.assertEquals(delret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(delret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void generateDistributionLinks() throws IOException {
    runCatchExceptions(
        newClient(),
        c -> {
          var mailListId = createMailingListHelper(c);
          var body =
              new GenerateDistributionLinksBody(
                  TestProperties.properties().getSurveyId(),
                  "Test link generation",
                  new Date(),
                  mailListId);

          var ret = c.generateDistributionLinks(body);
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void retrieveGeneratedLinks() throws IOException {
    runCatchExceptions(
        newClient(),
        c -> {
          var mailListId = createMailingListHelper(c);
          var body =
              new GenerateDistributionLinksBody(
                  TestProperties.properties().getSurveyId(),
                  "Test link generation",
                  new Date(),
                  mailListId);

          var genLinksRet = c.generateDistributionLinks(body);

          var ret =
              c.retrieveGeneratedLinks(
                  genLinksRet.getBody().getResult().getId(),
                  TestProperties.properties().getSurveyId());
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }
}
