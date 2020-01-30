package qualtrix.responses.V3.Survey;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public abstract class AbstractSurveyResult {
  @NonNull private String creationDate;
  @NonNull private SurveyResultExpiration expiration;
  @NonNull private String id;
  @NonNull private Boolean isActive;
  @NonNull private String lastModifiedDate;
  @NonNull private String name;
  @NonNull private String organizationId;
  @NonNull private String ownerId;
  @NonNull private SurveyResultResponseCounts responseCounts;
}
