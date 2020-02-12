package qualtrix.responses.V3.MailingList;

import lombok.*;
import qualtrix.responses.V3.BaseResponse;
import qualtrix.responses.V3.GeneralMeta;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CreateMailingListResponse extends BaseResponse {
  @NonNull private CreateMailingListResponseResult result;

  public CreateMailingListResponse(
      @NonNull GeneralMeta meta, @NonNull CreateMailingListResponseResult result) {
    super(meta);
    this.result = result;
  }
}
