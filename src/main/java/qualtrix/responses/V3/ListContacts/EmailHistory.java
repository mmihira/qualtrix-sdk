package qualtrix.responses.V3.ListContacts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailHistory {
  private String date;
  private String emailDistributionId;
  private Boolean read;
  private String result;
  private String surveyId;
  private String type;
}
