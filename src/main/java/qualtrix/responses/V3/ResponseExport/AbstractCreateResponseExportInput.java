package qualtrix.responses.V3.ResponseExport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@JsonPropertyOrder({ "format", "embeddedDataIds" })
public class AbstractCreateResponseExportInput {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> embeddedDataIds;
    @NonNull
    private ResponseExportFormat format;
}

