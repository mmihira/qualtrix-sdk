package qualtrix.responses.V3.MailingList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ListMailingListsResponseResultElement {
  private String category;
  private Object folder;
  private String id;
  private String libraryId;
  private String name;
}
