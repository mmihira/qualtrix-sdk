package qualtrix.responses.V3.WhoAmI;

import lombok.*;
import qualtrix.responses.V3.BaseResponse;
import qualtrix.responses.V3.GeneralMeta;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class WhoAmIResponse extends BaseResponse {
  @NonNull private WhoAmIResult result;

  public WhoAmIResponse(@NonNull GeneralMeta meta, @NonNull WhoAmIResult result) {
    super(meta);
    this.result = result;
  }
}
