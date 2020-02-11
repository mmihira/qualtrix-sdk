
package qualtrix.responses.V3.ListContacts;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListContactsResponseResult {
    private List<ListContactsResponseResultElement> elements;
    private String nextPage;
}
