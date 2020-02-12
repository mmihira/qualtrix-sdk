package qualtrix.responses.V3.ListContacts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseHistory {
  private String date;
  private String emailDistributionId;
  private Boolean finishedSurvey;
  private String responseId;
  private String surveyId;
}
