package qualtrix.responses.V3.Survey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
class SurveyResultExpiration implements Serializable {
  private String endDate;
  private String startDate;
}
