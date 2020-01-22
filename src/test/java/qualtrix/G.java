package qualtrix;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import qualtrix.responses.V3.SurveyResponse;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class G extends SurveyResponse<F> {
}

