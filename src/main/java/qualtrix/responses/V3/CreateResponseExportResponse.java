
package qualtrix.responses.V3;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class CreateResponseExportResponse {
    @NonNull
    private GeneralMeta meta;
    @NonNull
    private CreateResponseExportResult result;
}
