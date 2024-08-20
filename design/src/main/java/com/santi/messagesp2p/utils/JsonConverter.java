package com.santi.messagesp2p.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonConverter {

  private static final Logger logger = LoggerFactory.getLogger(JsonConverter.class);

  /**
   * Object should be in DTO format.
   * @param object to convert
   * @return serialized JSON
   */
  public static String messageToJson(Object object) {
    ObjectMapper mapper = new ObjectMapper();

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    JavaTimeModule module = new JavaTimeModule();
    module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
    mapper.registerModule(module);

    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    try {
      return ow.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      logger.info("Error converting message to JSON");
      return "{}";
    }
  }

}
