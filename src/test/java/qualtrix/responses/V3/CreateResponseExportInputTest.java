package qualtrix.responses.V3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static qualtrix.responses.V3.ResponseExportFormat.csv;

public class CreateResponseExportInputTest {
    @Test
    public void testJsonDeSerialisationWithOnlyFormat() throws JsonProcessingException {
        var sub = new CreateResponseExportInput(ResponseExportFormat.csv);
        ObjectMapper objectMapper = new ObjectMapper();
        var jsonOut = objectMapper.writeValueAsString(sub);
        assertEquals(jsonOut, "{\"format\":\"csv\"}");
    }

    @Test
    public void testJsonDeSerialisation() throws JsonProcessingException {
        var sub = new CreateResponseExportInput(Arrays.asList("test"), ResponseExportFormat.csv);
        ObjectMapper objectMapper = new ObjectMapper();
        var jsonOut = objectMapper.writeValueAsString(sub);
        assertEquals(jsonOut, "{\"format\":\"csv\",\"embeddedDataIds\":[\"test\"]}");
    }
}