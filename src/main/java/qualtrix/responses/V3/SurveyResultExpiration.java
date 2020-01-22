
package qualtrix.responses.V3;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
class SurveyResultExpiration implements Serializable {
    private String endDate;
    private String startDate;
}
