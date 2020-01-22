
package qualtrix.responses.V3;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
class CreateResponseExportResult {
    private long percentComplete;
    @NonNull private String progressId;
    @NonNull private String status;
}