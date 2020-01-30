package qualtrix.responses.V3.ListMailingLists;

import lombok.*;
import qualtrix.responses.V3.BaseResponse;
import qualtrix.responses.V3.MailingList.ListMailingListsResponseResult;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ListMailingListsResponse extends BaseResponse {
  private ListMailingListsResponseResult result;
}
