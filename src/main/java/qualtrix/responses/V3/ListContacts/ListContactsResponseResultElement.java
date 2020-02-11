
package qualtrix.responses.V3.ListContacts;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListContactsResponseResultElement {
    private String email;
    private List<EmailHistory> emailHistory;
    private EmbeddedData embeddedData;
    private Object externalDataReference;
    private String firstName;
    private String id;
    private Object language;
    private String lastName;
    private List<ResponseHistory> responseHistory;
    private Boolean unsubscribed;
}
