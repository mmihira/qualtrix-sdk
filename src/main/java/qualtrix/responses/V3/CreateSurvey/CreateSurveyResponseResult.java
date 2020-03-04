package qualtrix.responses.V3.CreateSurvey;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSurveyResponseResult {
    @JsonProperty("SurveyID")
    private String SurveyID;
    @JsonProperty("DefaultBlockID")
    private String DefaultBlockID;
}
