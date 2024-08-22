package com.santi.messagesp2p.controller;

import com.santi.messagesp2p.model.Channel;
import com.santi.messagesp2p.model.User;
import com.santi.messagesp2p.service.FriendshipRequestService;
import com.santi.messagesp2p.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/friendship-requests")
public class FriendshipRequestController {

  private final FriendshipRequestService friendshipRequestService;
  private final UserService userService;

  @Autowired
  public FriendshipRequestController(FriendshipRequestService friendshipRequestService, UserService userService) {
    this.friendshipRequestService = friendshipRequestService;
    this.userService = userService;
  }

  @GetMapping("/health")
  public ResponseEntity<?> health() {
    return ResponseEntity.ok().build();
  }

  @PostMapping
  public ResponseEntity<?> createFriendshipRequest(
      @RequestParam Long senderId,
      @RequestParam Long receiverId
  ) {
    // exception managed by global exception handler
    this.friendshipRequestService.createFriendshipRequest(senderId, receiverId);
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<?> getFriendshipRequests(
      @RequestParam Long receiverId
  ) {
    return ResponseEntity.ok(this.friendshipRequestService.getFriendshipRequests(receiverId));
  }

  @PatchMapping
  public ResponseEntity<?> updateFriendshipRequestState(
      @RequestParam Long friendshipRequestId,
      @RequestParam Boolean accepted
  ) {
    Channel createdChannel = this.friendshipRequestService.setFriendshipRequestState(friendshipRequestId, accepted);
    User petitioner = userService.getUserFromContext();

    return createdChannel == null ? ResponseEntity.ok().build() : ResponseEntity.ok(createdChannel.toDTO(petitioner));
  }

}
