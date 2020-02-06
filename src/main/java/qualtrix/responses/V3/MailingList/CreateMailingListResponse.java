package qualtrix.responses.V3.MailingList;

import lombok.*;
import qualtrix.responses.V3.BaseResponse;
import qualtrix.responses.V3.ResponseExport.CreateResponseExportResult;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@NoArgsConstructor
public class CreateMailingListResponse extends BaseResponse {
  @NonNull private CreateResponseExportResult result;
}