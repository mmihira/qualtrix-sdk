package qualtrix.responses.V3.MailingList;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class ListMailingListsResponseResult {
  @NonNull private List<ListMailingListsResponseResultElement> elements;
  private String nextPage;
}
