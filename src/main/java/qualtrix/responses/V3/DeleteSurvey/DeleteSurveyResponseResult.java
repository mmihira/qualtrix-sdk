package qualtrix.responses.V3.DeleteSurvey;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import qualtrix.json.QualtrixDateTimeSerializer;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteSurveyResponseResult {
  @JsonProperty("DeletedDate")
  @JsonSerialize(using = ToStringSerializer.class)
  @JsonDeserialize(using = QualtrixDateTimeSerializer.class)
  public LocalDateTime deletedTime;
}
