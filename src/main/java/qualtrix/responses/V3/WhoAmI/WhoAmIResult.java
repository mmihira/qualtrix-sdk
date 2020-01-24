package qualtrix.responses.V3;

import lombok.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WhoAmIResult implements Serializable{
    @NonNull
    private String accountType;
    @NonNull
    private String userId;
    @NonNull
    private String brandId;
    @NonNull
    private String lastName;
    @NonNull
    private String userName;
    @NonNull
    private String firstName;
    @NonNull
    private String email;
}
