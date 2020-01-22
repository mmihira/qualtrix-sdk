
package qualtrix.responses.V3;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class SurveyListResponse {
    @NonNull
    private SurveyListResult result;
    @NonNull
    private GeneralMeta meta;
}
