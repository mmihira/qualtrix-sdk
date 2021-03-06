package qualtrix.responses.V3.Survey;

import lombok.*;
import qualtrix.responses.V3.BaseResponse;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@NoArgsConstructor
public abstract class SurveyResponse<T extends AbstractSurveyResult> extends BaseResponse {
  @NonNull T result;
}
