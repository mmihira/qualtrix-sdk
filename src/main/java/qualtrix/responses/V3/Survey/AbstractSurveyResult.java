package qualtrix.responses.V3.Survey;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import qualtrix.json.QualtrixDateTimeSerializer;
import qualtrix.json.QualtrixDateTimeToStringSerializer;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public abstract class AbstractSurveyResult {
  @JsonSerialize(using = QualtrixDateTimeToStringSerializer.class)
  @JsonDeserialize(using = QualtrixDateTimeSerializer.class)
  private LocalDateTime creationDate;

  @NonNull private SurveyResultExpiration expiration;
  @NonNull private String id;
  @NonNull private Boolean isActive;

  @JsonSerialize(using = QualtrixDateTimeToStringSerializer.class)
  @JsonDeserialize(using = QualtrixDateTimeSerializer.class)
  private LocalDateTime lastModifiedDate;

  @NonNull private String name;
  @NonNull private String organizationId;
  @NonNull private String ownerId;
  @NonNull private SurveyResultResponseCounts responseCounts;
}
