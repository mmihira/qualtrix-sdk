
package qualtrix.responses.V3.GetMailingList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMailingListResponseResult {
    private String category;
    private String id;
    private String libraryId;
    private String name;

}
