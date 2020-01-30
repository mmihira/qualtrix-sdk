package qualtrix.responses.V3.MailingList;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"format", "embeddedDataIds"})
public class CreateMailingListBody {
  private String category;
  private String libraryId;
  private String name;
}
