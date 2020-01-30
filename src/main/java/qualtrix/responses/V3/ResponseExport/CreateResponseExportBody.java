package qualtrix.responses.V3.ResponseExport;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
public class CreateResponseExportBody extends AbstractCreateResponseExportBody {
  public CreateResponseExportBody(@NonNull ResponseExportFormat format) {
    super(format);
  }
}
