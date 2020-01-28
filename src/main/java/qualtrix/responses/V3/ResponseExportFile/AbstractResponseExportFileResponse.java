package qualtrix.responses.V3.ResponseExportFile;

import lombok.*;

import java.util.List;

@Data
@RequiredArgsConstructor
public abstract class AbstractResponseExportFileResponse<T extends AbstractResponseExportFileEntity> {
    @NonNull private List<T> responses = null;
}
