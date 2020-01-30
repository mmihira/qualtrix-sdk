package qualtrix.responses.V3.SurveyList;

import lombok.*;
import qualtrix.responses.V3.BaseResponse;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@NoArgsConstructor
public class SurveyListResponse extends BaseResponse {
  @NonNull private SurveyListResult result;
}
