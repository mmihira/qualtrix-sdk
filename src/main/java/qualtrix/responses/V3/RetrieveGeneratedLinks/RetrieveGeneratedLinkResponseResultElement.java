package qualtrix.responses.V3.RetrieveGeneratedLinks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
  private String linkExpiration;
  private String status;
  private int unsubscribed;
}
