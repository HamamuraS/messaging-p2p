package com.santi.messagesp2p.controller;

import com.santi.messagesp2p.dto.ChannelDTO;
import com.santi.messagesp2p.dto.MessageDTO;
import com.santi.messagesp2p.dto.UserChannelDTO;
import com.santi.messagesp2p.exception.BadRequestException;
import com.santi.messagesp2p.model.Channel;
import com.santi.messagesp2p.model.Message;
import com.santi.messagesp2p.model.User;
import com.santi.messagesp2p.model.user_channel.UserChannel;
import com.santi.messagesp2p.service.ChannelService;
import com.santi.messagesp2p.service.UserService;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/channels")
public class ChannelController {

  private static final Logger logger = LoggerFactory.getLogger(ChannelController.class);

  @Autowired
  private ChannelService channelService;
  @Autowired
  private UserService userService;

  @PostMapping
  public ResponseEntity<?> createChannel(@RequestBody ChannelDTO channelDTO) {
    if (channelDTO.getType() == null || channelDTO.getType().isEmpty()) {
      throw new BadRequestException("Must provide a channel type.");
    }
    logger.info("Received petition to create channel of type {}", channelDTO.getType());
    Channel newChannel = channelService.initializeChannel(channelDTO);
    logger.info("Channel {} created", newChannel.getId());
    return new ResponseEntity<>(
        newChannel.toDTO(userService.getUserFromContext()),
        HttpStatus.CREATED
    );
  }

  @DeleteMapping("/{channelId}")
  public ResponseEntity<?> deleteChannel(@PathVariable Long channelId) {
    // DANGEROUS OPERATION WHEN COMES TO CONTACT CHANNELS
    logger.info("Received petition to delete channel {}", channelId);
    channelService.deleteChannel(channelId);
    logger.info("Channel {} deleted", channelId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{channelId}")
  public ResponseEntity<?> getChannel(@PathVariable Long channelId) {
    logger.info("Requested channel {}", channelId);
    Channel channel = channelService.getChannel(channelId);
    logger.info("Channel {} found", channelId);
    return ResponseEntity.ok(channel.toDTO(userService.getUserFromContext()));
  }

  @DeleteMapping("/{channelId}/users/{userId}")
  public ResponseEntity<?> removeUserFromChannel(
      @PathVariable Long channelId,
      @PathVariable Long userId
  ) {
    logger.info("Received petition to remove user {} from channel {}", userId, channelId);
    Channel channel = channelService.removeUserFromChannel(channelId, userId);
    logger.info("User {} removed from channel {}", userId, channel.getId());
    return ResponseEntity.ok(channel.toDTO());
  }

  @PostMapping("/{channelId}/users/{userId}")
  public ResponseEntity<?> addUserToChannel(
      @PathVariable Long channelId,
      @PathVariable Long userId
  ) {
    logger.info("Received petition to add user {} to channel {}", userId, channelId);
    Channel updatedChannel = channelService.addUserToChannel(channelId, userId);
    logger.info("User {} added to channel {}", userId, channelId);
    return ResponseEntity.ok(updatedChannel.toDTO());
  }

  @PutMapping("/{channelId}/users/{userId}/role/{roleName}")
  public ResponseEntity<?> updateUserRole(
      @PathVariable Long channelId,
      @PathVariable Long userId,
      @PathVariable String roleName
  ) {
    logger.info("Received petition to update role of user {} in channel {} to {}",
        userId, channelId, roleName);
    UserChannel userChannel = channelService.updateUserRole(channelId, userId, roleName);
    logger.info("Role of user {} in channel {} updated to {}",
        userId, channelId, userChannel.getRole().getName());
    return ResponseEntity.ok(getUserChannelDTO(userChannel));
  }

  @GetMapping("/{channelId}/messages")
  public ResponseEntity<?> getMessagesByChannel(
      @PathVariable Long channelId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size
  ) {
    logger.info("Requested messages for channel {} with pagination, page: {}, size: {}", channelId, page, size);
    Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
    Page<Message> messages = channelService.getMessagesByChannel(channelId, pageable);
    Page<MessageDTO> messageDTOS = messages.map(Message::toDTO);
    logger.info("Messages for channel {} found", channelId);
    return ResponseEntity.ok(messageDTOS);
  }

  private UserChannelDTO getUserChannelDTO(UserChannel userChannel) {
    UserChannelDTO userChannelDTO = new UserChannelDTO();
    userChannelDTO.setChannelId(userChannel.getChannel().getId());
    userChannelDTO.setRole(userChannel.getRole());
    userChannelDTO.setUser(userChannel.getUser());
    return userChannelDTO;
  }

}
