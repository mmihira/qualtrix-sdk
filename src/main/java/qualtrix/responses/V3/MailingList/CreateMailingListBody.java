package qualtrix.responses.V3.MailingList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMailingListBody {
  private String category;
  private String libraryId;
  private String name;
}
