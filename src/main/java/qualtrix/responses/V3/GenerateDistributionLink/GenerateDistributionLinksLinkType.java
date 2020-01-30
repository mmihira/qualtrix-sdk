package qualtrix.responses.V3.GenerateDistributionLink;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public enum GenerateDistributionLinksLinkType {
  Individual("Individual");
  @Getter private String value;
}
