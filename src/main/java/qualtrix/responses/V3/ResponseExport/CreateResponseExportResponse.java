
package qualtrix.responses.V3;

import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@NoArgsConstructor
public class CreateResponseExportResponse extends BaseResponse {
    @NonNull
    private CreateResponseExportResult result;
}
