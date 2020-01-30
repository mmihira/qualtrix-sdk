package qualtrix.responses.V3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import qualtrix.responses.V3.ResponseExport.CreateResponseExportBody;
import qualtrix.responses.V3.ResponseExport.ResponseExportFormat;

import static org.junit.Assert.assertEquals;

public class CreateResponseExportInputTest {
  @Test
  public void testJsonDeSerialisationWithOnlyFormat() throws JsonProcessingException {
    var sub = new CreateResponseExportBody(ResponseExportFormat.json);
    ObjectMapper objectMapper = new ObjectMapper();
    var jsonOut = objectMapper.writeValueAsString(sub);
    assertEquals(jsonOut, "{\"format\":\"json\"}");
  }
}
