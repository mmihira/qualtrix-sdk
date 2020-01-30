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

  @Test
  public void whoAmI() throws IOException {
    var restTemplate = new RestTemplate();
    var client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
    runCatchExceptions(
        client,
        c -> {
          var result = c.whoAmI();
          Assert.assertEquals(result.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(result.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void listSurveys() throws IOException {
    var restTemplate = new RestTemplate();
    var client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
    runCatchExceptions(
        client,
        c -> {
          var ret = c.listSurveys();
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void survey1() throws IOException {
    var restTemplate = new RestTemplate();
    var client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
    runCatchExceptions(
        client,
        c -> {
          var ret = c.survey("SV_1MLZsPfqxmInXyB");
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void survey2() throws IOException {
    var surveyId = TestProperties.properties().getSurveyId();
    var restTemplate = new RestTemplate();
    var client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
    runCatchExceptions(
        client,
        c -> {
          var ret = c.survey(surveyId, G.class);
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void createResponseExport() throws IOException {
    var surveyId = TestProperties.properties().getSurveyId();
    var restTemplate = new RestTemplate();
    var client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
    runCatchExceptions(
        client,
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
    var restTemplate = new RestTemplate();
    var client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
    runCatchExceptions(
        client,
        c -> {
          var ret = c.createResponseExportFileObject(surveyId, fileId);
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
        });
  }

  @Test
  public void createResponseExportAndGetFileDefault() throws IOException {
    var surveyId = TestProperties.properties().getSurveyId();
    var restTemplate = new RestTemplate();
    var client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
    runCatchExceptions(
        client,
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
    var restTemplate = new RestTemplate();
    var client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
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
    var restTemplate = new RestTemplate();
    var client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
    runCatchExceptions(
        client,
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
    var restTemplate = new RestTemplate();
    var client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
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
    var restTemplate = new RestTemplate();
    var client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
    var libraryId = TestProperties.properties().getLibraryId();
    var newCategory = "Something";
    var name = "New Mailing List";
    runCatchExceptions(
        client,
        c -> {
          var input = new CreateMailingListBody(newCategory, libraryId, name);
          var ret = c.createMailingList(input);
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void listMailingList() throws IOException {
    var restTemplate = new RestTemplate();
    var client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
    runCatchExceptions(
        client,
        c -> {
          var ret = client.listMailingList();
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void deleteContact() throws IOException {
    var restTemplate = new RestTemplate();
    var client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
    runCatchExceptions(
        client,
        c -> {
          // First create a new contact
          var body =
              new CreateContactBody("test@gmal.com", null, null, "bob", "eng", "getRequest", true);
          var ret = client.createContact(TestProperties.properties().getMailingListId(), body);
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
          var contactId = ret.getBody().getResult().getId();

          // Now try and delete the contact
          var del = client.deleteContact(TestProperties.properties().getMailingListId(), contactId);
          Assert.assertEquals(del.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(del.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void generateDistributionLinks() throws IOException {
    var restTemplate = new RestTemplate();
    var client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
    runCatchExceptions(
        client,
        c -> {
          var body =
              new GenerateDistributionLinksBody(
                  TestProperties.properties().getSurveyId(),
                  "Test link generation",
                  new Date(),
                  TestProperties.properties().getMailingListId());
          var ret = client.generateDistributionLinks(body);
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }

  @Test
  public void retrieveGeneratedLinks() throws IOException {
    var restTemplate = new RestTemplate();
    var client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
    runCatchExceptions(
        client,
        c -> {
          var ret =
              client.retrieveGeneratedLinks(
                  TestProperties.properties().getDistributionLinksId(),
                  TestProperties.properties().getSurveyId());
          Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
          Assert.assertEquals(ret.getBody().getMeta().getHttpStatus(), "200 - OK");
        });
  }
}
