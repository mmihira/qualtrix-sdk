package qualtrix.responses.V3.Survey;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import qualtrix.json.QualtrixDateTimeSerializer;
import qualtrix.json.QualtrixDateTimeToStringSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurveyResultExpiration implements Serializable {
  @JsonSerialize(using = QualtrixDateTimeToStringSerializer.class)
  @JsonDeserialize(using = QualtrixDateTimeSerializer.class)
  private LocalDateTime endDate;

  @JsonSerialize(using = QualtrixDateTimeToStringSerializer.class)
  @JsonDeserialize(using = QualtrixDateTimeSerializer.class)
  private LocalDateTime startDate;
}
