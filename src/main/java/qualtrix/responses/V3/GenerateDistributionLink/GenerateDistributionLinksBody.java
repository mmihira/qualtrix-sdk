package qualtrix.responses.V3.GenerateDistributionLink;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class GenerateDistributionLinksBody {

  private String surveyId;
  private GenerateDistributionLinksLinkType linkType = GenerateDistributionLinksLinkType.Individual;
  private String description;
  private GenerateDistributionLinksActionType action =
      GenerateDistributionLinksActionType.CreateDistribution;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private Date expirationDate;

  private String mailingListId;

  public GenerateDistributionLinksBody(
      String surveyId, String description, Date expirationDate, String mailingListId) {
    this.surveyId = surveyId;
    this.description = description;
    this.expirationDate = expirationDate;
    this.mailingListId = mailingListId;
  }
}
