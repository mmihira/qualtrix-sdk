package qualtrix.responses.V3;

import lombok.*;
import java.io.Serializable;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class WhoAmIResponse implements Serializable {
    @NonNull
    private WhoAmIResult result;
    @NonNull
    private GeneralMeta meta;
}

