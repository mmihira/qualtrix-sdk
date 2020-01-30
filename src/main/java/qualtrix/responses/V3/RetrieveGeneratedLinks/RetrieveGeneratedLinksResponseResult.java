package qualtrix.responses.V3.RetrieveGeneratedLinks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveGeneratedLinksResponseResult {
  private List<RetrieveGeneratedLinkResponseResultElement> elements;
}
