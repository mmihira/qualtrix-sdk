package qualtrix.responses.V3.ResponseExportProgress;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class ResponseExportProgressResult {
  private long percentComplete;
  @NonNull private String status;
  private String fileId;

  public boolean isComplete() {
    return this.status.equals("complete");
  }
}
