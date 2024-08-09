package com.santi.messagesp2p.dto;

import com.santi.messagesp2p.model.User;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageResponseDTO {

  private Long id;
  private User sender;
  private Long channel;
  private String content;
  private LocalDateTime timestamp;


}
