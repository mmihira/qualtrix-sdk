package qualtrix.responses.V3.SurveyList;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
public class SurveyListResultElement {
  @JsonSerialize(using = QualtrixDateTimeToStringSerializer.class)
  @JsonDeserialize(using = QualtrixDateTimeSerializer.class)
  @NonNull
  private LocalDateTime creationDate;

  @NonNull private String id;
  @NonNull private Boolean isActive;

  @JsonSerialize(using = QualtrixDateTimeToStringSerializer.class)
  @JsonDeserialize(using = QualtrixDateTimeSerializer.class)
  private LocalDateTime lastModified;

  @NonNull private String name;
  @NonNull private String ownerId;
}
