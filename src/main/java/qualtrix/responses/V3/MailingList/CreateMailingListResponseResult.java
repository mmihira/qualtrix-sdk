package qualtrix.responses.V3.MailingList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMailingListResponseResult {
    @NonNull private String id;
}
