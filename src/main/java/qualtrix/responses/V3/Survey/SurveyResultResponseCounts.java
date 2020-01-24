
package qualtrix.responses.V3;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
class SurveyResultResponseCounts implements Serializable {
    private long auditable;
    private long deleted;
    private long generated;
}
