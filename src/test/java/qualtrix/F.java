package qualtrix;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import qualtrix.responses.V3.AbstractSurveyResult;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString(onlyExplicitlyIncluded = true, callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class F extends AbstractSurveyResult {
    @JsonProperty("questions")
    private Map<String, Object> questions = new HashMap<String, Object>();
}


