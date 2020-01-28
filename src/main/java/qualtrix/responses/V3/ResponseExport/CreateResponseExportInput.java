
package qualtrix.responses.V3.ResponseExport;

import java.util.List;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
public class CreateResponseExportInput extends AbstractCreateResponseExportInput {
    public CreateResponseExportInput(List<String> embeddedDataIds, @NonNull ResponseExportFormat format) {
        super(embeddedDataIds, format);
    }

    public CreateResponseExportInput(@NonNull ResponseExportFormat format) {
        super(format);
    }
}
