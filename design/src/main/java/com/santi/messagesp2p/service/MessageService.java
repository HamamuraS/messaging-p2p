package com.santi.messagesp2p.service;


import com.santi.messagesp2p.controller.WebSocketHandler;
import com.santi.messagesp2p.model.Message;
import com.santi.messagesp2p.repository.MessageRepository;
import com.santi.messagesp2p.utils.JsonConverter;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
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
      String messageToSend = JsonConverter.messageToJson(savedMessage.toDTO()); // uses DTO
      webSocketHandler.shareNewMessage(message.getChannel().getId(), messageToSend);
    } catch (IOException e) {
      logger.warning("Error sending message to all clients");
      throw new RuntimeException("Server error processing message");
    }

    return savedMessage;
  }

  public Page<Message> getMessages(Long channelId, LocalDateTime lastLoadedTimestamp, Pageable pageable) {
    return messageRepository.findByChannelIdAndTimestampLessThan(channelId, lastLoadedTimestamp, pageable);
  }

  public Page<Message> getMessages(Long channelId, Pageable pageable) {
    return messageRepository.findByChannelId(channelId, pageable);
  }


}
