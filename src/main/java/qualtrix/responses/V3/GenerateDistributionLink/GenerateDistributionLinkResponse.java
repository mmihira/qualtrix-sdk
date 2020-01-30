package qualtrix.responses.V3.GenerateDistributionLink;

import lombok.*;
import qualtrix.responses.V3.BaseResponse;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class GenerateDistributionLinkResponse extends BaseResponse {
  private GenerateDistributionLinkResponseResult result;
}
