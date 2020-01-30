package qualtrix.responses.V3.SurveyList;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
class SurveyListResult {
  @NonNull private List<SurveyListResultElement> elements;
  // next page can be null if there is no next page
  private String nextPage;
}
