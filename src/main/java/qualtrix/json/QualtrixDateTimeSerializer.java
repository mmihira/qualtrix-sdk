package qualtrix.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class QualtrixDateTimeSerializer extends StdDeserializer<LocalDateTime> {
  public static final DateTimeFormatter dateFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]'Z'", Locale.ENGLISH);

  public QualtrixDateTimeSerializer() {
    super(LocalDateTime.class);
  }

  @Override
  public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    return LocalDateTime.parse(p.getValueAsString(), dateFormatter);
  }
}
