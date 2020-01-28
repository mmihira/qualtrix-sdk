package qualtrix.responses.V3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import qualtrix.responses.V3.ResponseExport.CreateResponseExportInput;
import qualtrix.responses.V3.ResponseExport.ResponseExportFormat;

import java.util.Arrays;
import static org.junit.Assert.*;

public class CreateResponseExportInputTest {
    @Test
    public void testJsonDeSerialisationWithOnlyFormat() throws JsonProcessingException {
        var sub = new CreateResponseExportInput(ResponseExportFormat.json);
        ObjectMapper objectMapper = new ObjectMapper();
        var jsonOut = objectMapper.writeValueAsString(sub);
        assertEquals(jsonOut, "{\"format\":\"json\"}");
    }

    @Test
    public void testJsonDeSerialisation() throws JsonProcessingException {
        var sub = new CreateResponseExportInput(Arrays.asList("test"), ResponseExportFormat.json);
        ObjectMapper objectMapper = new ObjectMapper();
        var jsonOut = objectMapper.writeValueAsString(sub);
        assertEquals(jsonOut, "{\"format\":\"json\",\"embeddedDataIds\":[\"test\"]}");
    }
}