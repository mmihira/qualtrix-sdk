package qualtrix;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import qualtrix.responses.V3.*;

import java.io.Serializable;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class QualtrixClientTest{
    @Test
    public void testWhoAmI() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> env = System.getenv();
        System.out.println(env);
        QualtrixClient client = new QualtrixClient(restTemplate, "1wsvDXM7sMvAbuTsoxBOIu8DjrI4Mdv4He5Lt6pb");
        try {
            ResponseEntity<WhoAmIResponse> ret = client.whoAmI();
            System.out.println(ret);
        } catch(HttpClientErrorException e) {
            System.out.println("here");
            System.out.println(e.getStatusText());
            System.out.println(e.getMessage());
        } catch(HttpMessageConversionException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("error");
            System.out.println(e);
        }
    }

    @Test
    public void testListSurvey() {
        RestTemplate restTemplate = new RestTemplate();
        QualtrixClient client = new QualtrixClient(restTemplate, "1wsvDXM7sMvAbuTsoxBOIu8DjrI4Mdv4He5Lt6pb");
        try {
            ResponseEntity<SurveyListResponse> ret = client.listSurveys();
            System.out.println(ret);
        } catch(HttpClientErrorException e) {
            System.out.println("here");
            System.out.println(e.getStatusText());
            System.out.println(e.getMessage());
        } catch(HttpMessageConversionException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("error");
            System.out.println(e);
        }
    }

    @Test
    public void testGetSurvey() {
        RestTemplate restTemplate = new RestTemplate();
        QualtrixClient client = new QualtrixClient(restTemplate, "1wsvDXM7sMvAbuTsoxBOIu8DjrI4Mdv4He5Lt6pb");
        try {
            var ret = client.survey("SV_1MLZsPfqxmInXyB");
            System.out.println(ret);
        } catch(HttpClientErrorException e) {
            System.out.println("here");
            System.out.println(e.getStatusText());
            System.out.println(e.getMessage());
        } catch(HttpMessageConversionException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("error");
            System.out.println(e);
        }
    }

    @Test
    public void testGetSurveyCustom() {
        RestTemplate restTemplate = new RestTemplate();
        QualtrixClient client = new QualtrixClient(restTemplate, "1wsvDXM7sMvAbuTsoxBOIu8DjrI4Mdv4He5Lt6pb");
        try {
            var ret = client.survey("SV_1MLZsPfqxmInXyB", G.class);
            System.out.print(ret.getBody().getResult().getQuestions());
        } catch(HttpClientErrorException e) {
            System.out.println("here");
            System.out.println(e.getStatusText());
            System.out.println(e.getMessage());
        } catch(HttpMessageConversionException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("error");
            System.out.println(e);
        }
    }

    @Test
    public void testCreateResponseExport() {
        RestTemplate restTemplate = new RestTemplate();
        QualtrixClient client = new QualtrixClient(restTemplate, "1wsvDXM7sMvAbuTsoxBOIu8DjrI4Mdv4He5Lt6pb");
        try {
            var ret = client.createResponseExport("SV_1MLZsPfqxmInXyB", new CreateResponseExportInput(ResponseExportFormat.csv));
            System.out.println(ret);
        } catch(HttpClientErrorException e) {
            System.out.println("here");
            System.out.println(e.getStatusText());
            System.out.println(e.getMessage());
            System.out.println(e.getResponseBodyAsString());
        } catch(HttpMessageConversionException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("error");
            System.out.println(e);
        }
    }
}
