package qualtrix.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class QualtrixLinkCreationExpiryZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {
  public static final DateTimeFormatter dateFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
          .withZone(ZoneId.of("America/Chihuahua"));

  @Override
  public void serialize(ZonedDateTime arg0, JsonGenerator arg1, SerializerProvider arg2)
      throws IOException {

    arg1.writeString(arg0.format(dateFormatter));
  }
}
