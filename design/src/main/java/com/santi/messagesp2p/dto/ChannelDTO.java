package com.santi.messagesp2p.dto;

import jakarta.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChannelDTO {

  private Long id;
  private String name;
  private String type;
  private Set<Long> users = new HashSet<>();
  private String messageType = "NEW_CHANNEL";

  public ChannelDTO() {
  }

}
