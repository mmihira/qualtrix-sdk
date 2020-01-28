package qualtrix.responses.V3;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public abstract class BaseResponse {
    @NonNull
    private GeneralMeta meta;
}
