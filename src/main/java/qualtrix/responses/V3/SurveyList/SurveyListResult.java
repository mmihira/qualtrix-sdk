
package qualtrix.responses.V3.SurveyList;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
class SurveyListResult {
    @NonNull
    private List<SurveyListResultElement> elements;
    // next page can be null if there is no next page
    private String nextPage;
}
