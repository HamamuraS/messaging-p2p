package com.santi.messagesp2p.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessageDTO {

  private Long sender;
  private Long channel;
  private String content;
  private LocalDateTime timestamp;

}

