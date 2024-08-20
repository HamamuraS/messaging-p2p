package com.santi.messagesp2p.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FriendshipRequestDTO {

  private Long id;

  private Long senderId;

  private String senderName;

  private Long receiverId;

  private String receiverName;

  private Boolean accepted;

  private LocalDateTime timestamp;

  private String messageType = "FRIENDSHIP_REQUEST"; // MESSAGE, FRIENDSHIP_REQUEST

}
