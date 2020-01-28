package qualtrix.responses.V3.ResponseExport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum ResponseExportFormat {
    json("json");

    @Getter
    private String value;
}
