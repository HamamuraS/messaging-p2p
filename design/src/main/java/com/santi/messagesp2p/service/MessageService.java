package com.santi.messagesp2p.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.santi.messagesp2p.controller.WebSocketHandler;
import com.santi.messagesp2p.model.Message;
import com.santi.messagesp2p.repository.MessageRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private final WebSocketHandler webSocketHandler;

  Logger logger = Logger.getLogger(MessageService.class.getName());

  public MessageService(WebSocketHandler webSocketHandler) {
    this.webSocketHandler = webSocketHandler;
  }

  @Transactional
  public Message saveAndShareMessage(Message message) {

    Message savedMessage = null;
    try {
      savedMessage = messageRepository.save(message);
      String messageToSend = messageToJson(savedMessage); // uses DTO
      webSocketHandler.shareNewMessage(message.getChannel().getId(), messageToSend);
    } catch (IOException e) {
      logger.warning("Error sending message to all clients");
      throw new RuntimeException("Server error processing message");
    }

    return savedMessage;
  }

  public Page<Message> getMessages(Long channelId, Pageable pageable) {
    return messageRepository.findByChannelId(channelId, pageable);
  }

  private String messageToJson(Message message) {
    ObjectMapper mapper = new ObjectMapper();

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    JavaTimeModule module = new JavaTimeModule();
    module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
    mapper.registerModule(module);

    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    try {
      return ow.writeValueAsString(message.toDTO());
    } catch (JsonProcessingException e) {
      logger.warning("Error converting message to JSON");
      return "{}";
    }
  }

}
