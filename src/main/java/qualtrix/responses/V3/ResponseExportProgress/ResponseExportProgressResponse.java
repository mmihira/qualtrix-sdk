package qualtrix.responses.V3.ResponseExportProgress;

import lombok.*;
import qualtrix.responses.V3.BaseResponse;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@NoArgsConstructor
public class ResponseExportProgressResponse extends BaseResponse {
  @NonNull private ResponseExportProgressResult result;
}
