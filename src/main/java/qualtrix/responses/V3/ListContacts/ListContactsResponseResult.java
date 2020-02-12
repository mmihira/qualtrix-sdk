package qualtrix.responses.V3.ListContacts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListContactsResponseResult {
  private List<ListContactsResponseResultElement> elements;
  private String nextPage;
}
