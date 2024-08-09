package com.santi.messagesp2p.service;

import com.santi.messagesp2p.model.Message;
import com.santi.messagesp2p.repository.MessageRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

  @Autowired
  private MessageRepository messageRepository;

  public List<Message> getMessages(Long channelId) {
    return messageRepository.findByChannelId(channelId);
  }

  public Message saveMessage(Message message) {
    return messageRepository.save(message);
  }

  public Page<Message> getMessages(Long channelId, Pageable pageable) {
    return messageRepository.findByChannelId(channelId, pageable);
  }


}
