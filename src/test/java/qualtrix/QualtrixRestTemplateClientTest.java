package qualtrix;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import qualtrix.responses.V3.ResponseExport.CreateResponseExportInput;
import qualtrix.responses.V3.ResponseExport.ResponseExportFormat;
import qualtrix.responses.V3.SurveyList.SurveyListResponse;

import java.io.IOException;

public class QualtrixRestTemplateClientTest {

    @FunctionalInterface
    private interface ClientRun {
        void Run(QualtrixRestTemplateClient r) throws IOException, InterruptedException;
    }

    private void runCatchExceptions(QualtrixRestTemplateClient client, ClientRun f) {
        try {
            f.Run(client);
        } catch (HttpClientErrorException e) {
            System.out.println("HttpClientErrorException");
            System.out.println(e.getStatusText());
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (HttpMessageConversionException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("error");
            System.out.println(e);
            e.printStackTrace();
        }
    }

    @Test
    public void testWhoAmI() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        QualtrixRestTemplateClient client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
        runCatchExceptions(client, c -> {
            var result = c.whoAmI();
            Assert.assertEquals(result.getStatusCode(), HttpStatus.OK);
        });
    }

    @Test
    public void testListSurvey() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        QualtrixRestTemplateClient client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
        runCatchExceptions(client, c -> {
            ResponseEntity<SurveyListResponse> ret = client.listSurveys();
            Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
        });
    }

    @Test
    public void testGetSurvey() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        QualtrixRestTemplateClient client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
        runCatchExceptions(client, c -> {
            var ret = client.survey("SV_1MLZsPfqxmInXyB");
            Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
        });
    }

    @Test
    public void testGetSurveyCustom() throws IOException {
        var surveyId = TestProperties.properties().getSurveyId();
        RestTemplate restTemplate = new RestTemplate();
        QualtrixRestTemplateClient client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
        runCatchExceptions(client, c -> {
            var ret = client.survey(surveyId, G.class);
            Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
        });
    }

    @Test
    public void testCreateResponseExport() throws IOException {
        var surveyId = TestProperties.properties().getSurveyId();
        RestTemplate restTemplate = new RestTemplate();
        QualtrixRestTemplateClient client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
        runCatchExceptions(client, c -> {
            var ret = client.createResponseExport(surveyId, new CreateResponseExportInput(ResponseExportFormat.json));
            Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
        });
    }

    @Test
    public void testResponseExportProgressFile() throws IOException {
        var surveyId = TestProperties.properties().getSurveyId();
        var fileId = TestProperties.properties().getExportFileId();
        RestTemplate restTemplate = new RestTemplate();
        QualtrixRestTemplateClient client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
        runCatchExceptions(client, c -> {
            var ret = client.createResponseExportFileObject(surveyId, fileId);
            Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
        });
    }

    @Test
    public void testCreateResponseExportAndGetFileDefault() throws IOException {
        var surveyId = TestProperties.properties().getSurveyId();
        RestTemplate restTemplate = new RestTemplate();
        QualtrixRestTemplateClient client = new QualtrixRestTemplateClient(TestProperties.getQualtrixTestKey(), restTemplate);
        runCatchExceptions(client, c -> {
            var ret = client.createResponseExportAndGetFileDefault(
                    surveyId,
                    new CreateResponseExportInput(ResponseExportFormat.json));
            Assert.assertEquals(ret.getStatusCode(), HttpStatus.OK);
        });
    }
}
