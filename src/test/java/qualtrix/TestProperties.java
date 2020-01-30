package qualtrix;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class TestProperties {
  @JsonProperty("qualtrixKey")
  public String qualtrixKey;

  @JsonProperty("surveyId")
  public String surveyId;

  @JsonProperty("exportFileId")
  public String exportFileId;

  @JsonProperty("libraryId")
  public String libraryId;

  @JsonProperty("mailingListId")
  public String mailingListId;

  @JsonProperty("distributionLinksId")
  public String distributionLinksId;

  public static String getQualtrixTestKey() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper
        .readValue(
            new ClassPathResource("qualtrix-key.json").getInputStream(), TestProperties.class)
        .getQualtrixKey();
  }

  public static TestProperties properties() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(
        new ClassPathResource("qualtrix-test-ids.json").getInputStream(), TestProperties.class);
  }
}
