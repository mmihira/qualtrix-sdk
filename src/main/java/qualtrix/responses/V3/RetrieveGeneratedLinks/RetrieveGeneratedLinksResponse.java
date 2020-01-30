package qualtrix.responses.V3.RetrieveGeneratedLinks;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import qualtrix.responses.V3.BaseResponse;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RetrieveGeneratedLinksResponse extends BaseResponse {
  private RetrieveGeneratedLinksResponseResult result;
}
