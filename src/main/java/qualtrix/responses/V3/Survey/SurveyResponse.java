
package qualtrix.responses.V3;

import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@NoArgsConstructor
public abstract class SurveyResponse <T extends AbstractSurveyResult> extends BaseResponse {
    @NonNull T result;
}
