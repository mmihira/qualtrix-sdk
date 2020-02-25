package qualtrix.responses.V3.GenerateDistributionLink;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import qualtrix.json.QualtrixLinkCreationExpiryZonedDateTimeSerializer;

import java.time.ZonedDateTime;

@Data
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GenerateDistributionLinksBodyWithZonedDateTime extends AbstractGenerateDistributionLinksBody {
  @JsonSerialize(using = QualtrixLinkCreationExpiryZonedDateTimeSerializer.class)
  private ZonedDateTime expirationDate;

  public GenerateDistributionLinksBodyWithZonedDateTime(
      String surveyId, String description, ZonedDateTime expirationDate, String mailingListId) {
    super(surveyId, description, mailingListId);
    this.expirationDate = expirationDate;
  }
}
