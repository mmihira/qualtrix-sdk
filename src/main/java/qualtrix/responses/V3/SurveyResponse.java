
package qualtrix.responses.V3;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public abstract class SurveyResponse <T extends AbstractSurveyResult> {
    @NonNull T result;
    @NonNull private GeneralMeta meta;
}
