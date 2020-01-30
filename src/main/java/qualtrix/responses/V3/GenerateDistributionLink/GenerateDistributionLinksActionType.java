package qualtrix.responses.V3.GenerateDistributionLink;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum GenerateDistributionLinksActionType {
  CreateDistribution("CreateDistribution");
  @Getter private String value;
}
