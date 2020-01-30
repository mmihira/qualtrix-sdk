package qualtrix.responses.V3.ResponseExport;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"format"})
public class AbstractCreateResponseExportBody {
  @NonNull private ResponseExportFormat format;
}
