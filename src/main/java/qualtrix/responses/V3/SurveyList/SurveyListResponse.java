
package qualtrix.responses.V3;

import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@NoArgsConstructor
public class SurveyListResponse extends BaseResponse {
    @NonNull
    private SurveyListResult result;
}
