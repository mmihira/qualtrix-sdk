package qualtrix.responses.V3.CreateContact;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateContactBody {
  private String email;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Map<String,String> embeddedData;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String externalDataRef;

  private String firstName;
  private String language;
  private String lastName;
  private Boolean unsubscribed;
}
