package com.santi.messagesp2p.service;

import com.santi.messagesp2p.controller.WebSocketHandler;
import com.santi.messagesp2p.dto.FriendshipRequestDTO;
import com.santi.messagesp2p.exception.NotFoundException;
import com.santi.messagesp2p.model.User;
import com.santi.messagesp2p.model.notification.FriendshipRequest;
import com.santi.messagesp2p.repository.FriendshipRequestRepository;
import com.santi.messagesp2p.repository.UserRepository;
import com.santi.messagesp2p.utils.JsonConverter;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendshipRequestService {

  private final FriendshipRequestRepository friendshipRequestRepository;
  private final UserRepository userRepository;
  private final WebSocketHandler webSocketHandler;

  Logger logger = LoggerFactory.getLogger(FriendshipRequestService.class);

  @Autowired
  public FriendshipRequestService(
      FriendshipRequestRepository friendshipRequestRepository,
      UserRepository userRepository,
      WebSocketHandler webSocketHandler
  ) {
    this.friendshipRequestRepository = friendshipRequestRepository;
    this.userRepository = userRepository;
    this.webSocketHandler = webSocketHandler;
  }


  @Transactional
  public void createFriendshipRequest(Long senderId, Long receiverId) {
    logger.info("Creating friendship request from {} to {}", senderId, receiverId);
    User sender = userRepository.findById(senderId)
        .orElseThrow(() -> new NotFoundException("Sender not found"));

    User receiver = userRepository.findById(receiverId)
        .orElseThrow(() -> new NotFoundException("Receiver not found"));

    // Create friendship request
    FriendshipRequest request = new FriendshipRequest();
    friendshipRequestRepository.save(request);

    request.setAccepted(null);
    request.setSender(sender);
    request.setReceiver(receiver);
    request.setTimestamp(LocalDateTime.now());

    //update users
    sender.getSentFriendshipRequests().add(request);
    receiver.getReceivedFriendshipRequests().add(request);

    // notify active websockets
    try {
      webSocketHandler.sendMessage(receiverId, JsonConverter.messageToJson(request.toDTO()));
    }
    catch (Exception e) {
      logger.info("Error sending message to user {}", receiverId);
    }
  }

  public Set<FriendshipRequestDTO> getFriendshipRequests(Long receiverId) {
    logger.info("Getting friendship requests for user {}", receiverId);
    return friendshipRequestRepository.findByReceiverId(receiverId).stream()
        .map(FriendshipRequest::toDTO)
        .collect(Collectors.toSet());
  }

}
