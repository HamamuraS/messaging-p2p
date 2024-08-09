package com.santi.messagesp2p.dto;

import com.santi.messagesp2p.model.Role;
import com.santi.messagesp2p.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChannelDTO {

  private User user;
  private Long channelId;
  private Role role;

}
