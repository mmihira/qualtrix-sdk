package qualtrix.responses.V3.DeleteSurvey;

import lombok.*;
import qualtrix.responses.V3.BaseResponse;

@Data
@NoArgsConstructor

@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DeleteSurveyResponse extends BaseResponse {
  public DeleteSurveyResponseResult result;
}
