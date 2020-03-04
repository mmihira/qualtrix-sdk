package qualtrix.responses.V3.CreateSurvey;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class CreateSurveyBody {
   @JsonProperty("SurveyName")
   public String surveyName;
   @JsonProperty("Language")
   public String language;
   @JsonProperty("ProjectCategory")
   public String projectCategory;
}
