package qualtrix.responses.V3.GenerateDistributionLink;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractGenerateDistributionLinksBody {

  private String surveyId;
  private GenerateDistributionLinksLinkType linkType = GenerateDistributionLinksLinkType.Individual;
  private String description;
  private GenerateDistributionLinksActionType action =
      GenerateDistributionLinksActionType.CreateDistribution;
  private String mailingListId;

  public AbstractGenerateDistributionLinksBody(
      String surveyId, String description, String mailingListId) {
    this.surveyId = surveyId;
    this.description = description;
    this.mailingListId = mailingListId;
  }
}
