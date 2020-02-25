package qualtrix.responses.V3.RetrieveGeneratedLinks;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import qualtrix.json.QualtrixDateTimeSerializer;

import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveGeneratedLinkResponseResultElement {
  private String contactId;
  private String email;
  private Boolean exceededContactFrequency;
  private String externalDataReference;
  private String firstName;
  private String lastName;
  private String link;

  @JsonSerialize(using = ToStringSerializer.class)
  @JsonDeserialize(using = QualtrixDateTimeSerializer.class)
  private LocalDateTime linkExpiration;

  private String status;
  private int unsubscribed;
}
