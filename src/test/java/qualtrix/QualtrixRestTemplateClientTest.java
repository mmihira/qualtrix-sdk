package qualtrix;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import qualtrix.responses.V3.ResponseExport.CreateResponseExportInput;
import qualtrix.responses.V3.ResponseExport.ResponseExportFormat;
import qualtrix.responses.V3.SurveyList.SurveyListResponse;
import qualtrix.responses.V3.WhoAmI.WhoAmIResponse;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class QualtrixClientTest{
    @Test
    public void testWhoAmI() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> env = System.getenv();
        System.out.println(env);
        QualtrixRestTemplateClient client = new QualtrixRestTemplateClient(restTemplate, "1wsvDXM7sMvAbuTsoxBOIu8DjrI4Mdv4He5Lt6pb", null);
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
    public void testWhoAmIWebClient() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> env = System.getenv();
        TcpClient timeoutClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5*1000)
                .doOnConnected(c -> c
                        .addHandlerLast(new ReadTimeoutHandler(5))
                        .addHandlerLast(new WriteTimeoutHandler(5))
                );
        WebClient w = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(timeoutClient)))
                .build();
        QualtrixRestTemplateClient client = new QualtrixRestTemplateClient(restTemplate, "1wsvDXM7sMvAbuTsoxBOIu8DjrI4Mdv4He5Lt6pb", w);
        try {
            Flux<WhoAmIResponse> ret = client.whoAmIWeb();
            var f = ret.blockFirst();
            System.out.println(f);
        } catch(WebClientResponseException e) {
            System.out.println("here");
            System.out.println(e.getStatusText());
            System.out.println(e.getMessage());
            System.out.println(e.getHeaders());
            System.out.println(e.getResponseBodyAsString());
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
        QualtrixRestTemplateClient client = new QualtrixRestTemplateClient(restTemplate, "1wsvDXM7sMvAbuTsoxBOIu8DjrI4Mdv4He5Lt6pb", null);
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
        QualtrixRestTemplateClient client = new QualtrixRestTemplateClient(restTemplate, "1wsvDXM7sMvAbuTsoxBOIu8DjrI4Mdv4He5Lt6pb", null);
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
        QualtrixRestTemplateClient client = new QualtrixRestTemplateClient(restTemplate, "1wsvDXM7sMvAbuTsoxBOIu8DjrI4Mdv4He5Lt6pb", null);
        try {
            var ret = client.survey("SV_1MLZsPfqxmInXyB", G.class);
            System.out.print(ret.getBody());
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
        QualtrixRestTemplateClient client = new QualtrixRestTemplateClient(restTemplate, "1wsvDXM7sMvAbuTsoxBOIu8DjrI4Mdv4He5Lt6pb", null);
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

    @Test
    public void testResponseExportProgress() {
        RestTemplate restTemplate = new RestTemplate();
        QualtrixRestTemplateClient client = new QualtrixRestTemplateClient(restTemplate, "1wsvDXM7sMvAbuTsoxBOIu8DjrI4Mdv4He5Lt6pb", null);
        try {
            var  export= client.createResponseExport("SV_1MLZsPfqxmInXyB", new CreateResponseExportInput(ResponseExportFormat.csv));
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
