package qualtrix.responses.V3.GenerateDistributionLink;

import lombok.*;

@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GenerateDistributionLinksBody extends AbstractGenerateDistributionLinksBody {
  private String expirationDate;

  public GenerateDistributionLinksBody(
      String surveyId, String description, String expirationDate, String mailingListId) {
    super(surveyId, description, mailingListId);
    this.expirationDate = expirationDate;
  }
}
