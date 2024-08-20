package com.santi.messagesp2p.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDTO {

  private Long messageId;
  private Long senderId;
  private String sender;
  private Long channel;
  private String content;
  private LocalDateTime timestamp;
  private String messageType = "MESSAGE"; // MESSAGE, FRIENDSHIP_REQUEST

}

