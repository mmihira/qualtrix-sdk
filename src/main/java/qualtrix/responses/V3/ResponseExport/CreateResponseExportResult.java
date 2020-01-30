package qualtrix.responses.V3.ResponseExport;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class CreateResponseExportResult {
  private long percentComplete;
  @NonNull private String progressId;
  @NonNull private String status;
}
