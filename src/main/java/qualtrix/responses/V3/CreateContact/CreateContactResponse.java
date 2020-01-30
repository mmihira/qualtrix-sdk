package qualtrix.responses.V3.CreateContact;

import lombok.*;
import qualtrix.responses.V3.BaseResponse;
import qualtrix.responses.V3.GeneralMeta;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CreateContactResponse extends BaseResponse {
  @NonNull private CreateContactResult result;

  public CreateContactResponse(@NonNull GeneralMeta meta, @NonNull CreateContactResult result) {
    super(meta);
    this.result = result;
  }
}
