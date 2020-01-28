
package qualtrix.responses.V3.ResponseExport;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
