package qualtrix.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class QualtrixDateTimeToStringSerializer extends JsonSerializer<LocalDateTime> {
  public static final DateTimeFormatter dateFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]'Z'", Locale.ENGLISH);

  @Override
  public void serialize(LocalDateTime arg0, JsonGenerator arg1, SerializerProvider arg2)
      throws IOException {

    arg1.writeString(arg0.format(dateFormatter));
  }
}
