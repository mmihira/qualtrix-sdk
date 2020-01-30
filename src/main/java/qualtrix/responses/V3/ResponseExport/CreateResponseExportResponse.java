package qualtrix.responses.V3.ResponseExport;

import lombok.*;
import qualtrix.responses.V3.BaseResponse;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@NoArgsConstructor
public class CreateResponseExportResponse extends BaseResponse {
  @NonNull private CreateResponseExportResult result;
}
