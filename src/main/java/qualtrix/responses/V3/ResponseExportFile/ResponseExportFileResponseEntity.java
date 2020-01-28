package qualtrix.responses.V3.ResponseExportFile;

import lombok.*;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ResponseExportFileResponseEntity extends AbstractResponseExportFileEntity {
    private ResponseExportFileValues values;
    private ResponseExportFileLabels labels;
    private ResponseExportFileDisplayedValues displayedValues;
}
