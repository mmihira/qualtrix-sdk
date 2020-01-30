package qualtrix.responses.V3.WhoAmI;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WhoAmIResult implements Serializable {
  @NonNull private String accountType;
  @NonNull private String userId;
  @NonNull private String brandId;
  @NonNull private String lastName;
  @NonNull private String userName;
  @NonNull private String firstName;
  @NonNull private String email;
}
