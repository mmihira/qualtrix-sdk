package qualtrix.responses.V3.GetMailingList;

import lombok.*;
import qualtrix.responses.V3.BaseResponse;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class GetMailingListResponse extends BaseResponse {
    GetMailingListResponseResult result;
}
