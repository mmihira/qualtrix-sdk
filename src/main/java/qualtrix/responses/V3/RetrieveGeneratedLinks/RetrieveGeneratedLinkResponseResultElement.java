package qualtrix.responses.V3.RetrieveGeneratedLinks;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import qualtrix.json.LinkExpiryDeserializer;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

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
  @JsonDeserialize(using = LinkExpiryDeserializer.class)
  private OffsetDateTime linkExpiration;

  private String status;
  private int unsubscribed;
}
