
package qualtrix.responses.V3;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@JsonPropertyOrder({ "format", "embeddedDataIds" })
public class CreateResponseExportInput {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> embeddedDataIds;
    @NonNull
    private ResponseExportFormat format;
}
