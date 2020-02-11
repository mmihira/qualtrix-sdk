package qualtrix.responses.V3.ListContacts;

import lombok.*;
import qualtrix.responses.V3.BaseResponse;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ListContactsResponse extends BaseResponse {
    private ListContactsResponseResult result;
}
