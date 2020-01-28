package qualtrix.responses.V3.WhoAmI;

import lombok.*;
import qualtrix.responses.V3.BaseResponse;

import java.io.Serializable;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@NoArgsConstructor
public class WhoAmIResponse extends BaseResponse implements Serializable {
    @NonNull
    private WhoAmIResult result;
}

