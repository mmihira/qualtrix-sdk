package qualtrix.responses.V3;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class GetResponseTest extends BaseResponse {
  private Map<String, Object> properties = new HashMap<>();

  @JsonAnyGetter
  public Map<String, Object> getProperties() {
    return properties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.properties.put(name, value);
  }
}
