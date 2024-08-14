package com.santi.messagesp2p.controller;


import com.santi.messagesp2p.dto.MessageDTO;
import com.santi.messagesp2p.exception.BadRequestException;
import com.santi.messagesp2p.model.Channel;
import com.santi.messagesp2p.model.Message;
import com.santi.messagesp2p.model.User;
import com.santi.messagesp2p.service.ChannelService;
import com.santi.messagesp2p.service.MessageService;
import com.santi.messagesp2p.service.UserService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/messages")
public class MessageController {

  private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

  @Autowired
  private MessageService messageService;
  @Autowired
  private ChannelService channelService;
  @Autowired
  private UserService userService;

  @PostMapping
  public ResponseEntity<?> createMessage(@RequestBody MessageDTO messageDto) {

    Message message = new Message();
    message.setContent(messageDto.getContent());

    Channel channel = channelService.getChannel(messageDto.getChannel());
    message.setChannel(channel);

    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!userService.existsByUsername(user.getUsername())) {
      throw new BadRequestException("User not found");
    }

    if (!channel.contains(user)) {
      return new ResponseEntity<>("User not in channel", HttpStatus.BAD_REQUEST);
    }
    message.setSender(user);

    message.setTimestamp(messageDto.getTimestamp());

    logger.info("User {} - {} : {}",
        message.getSender().getId(),
        message.getSender().getUsername(),
        message.getContent());

    messageService.saveMessage(message);

    return new ResponseEntity<>(messageService.saveMessage(message).toDTO(), HttpStatus.CREATED);
  }

}
