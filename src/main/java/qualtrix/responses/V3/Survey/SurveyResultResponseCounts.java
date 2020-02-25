package qualtrix.responses.V3.Survey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurveyResultResponseCounts implements Serializable {
  private long auditable;
  private long deleted;
  private long generated;
}
