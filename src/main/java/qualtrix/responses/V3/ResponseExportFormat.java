package qualtrix.responses.V3;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum ResponseExportFormat {
    csv("csv"),
    json("json");

    @Getter
    private String value;
}
