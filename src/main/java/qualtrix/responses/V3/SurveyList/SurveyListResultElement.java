package qualtrix.responses.V3.SurveyList;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
class SurveyListResultElement {
  @NonNull private String creationDate;
  @NonNull private String id;
  @NonNull private Boolean isActive;
  @NonNull private String lastModified;
  @NonNull private String name;
  @NonNull private String ownerId;
}
