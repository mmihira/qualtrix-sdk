package qualtrix.responses.V3;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralMeta {
    @NonNull
    private String requestId;
    @NonNull
    private String httpStatus;
}
