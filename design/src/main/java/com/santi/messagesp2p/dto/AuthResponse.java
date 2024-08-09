package com.santi.messagesp2p.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
public class AuthResponse {

  private final String token;
  @Setter
  @Accessors(chain = true)
  private Long id;
  @Setter
  @Accessors(chain = true)
  private String username;
  @Setter
  @Accessors(chain = true)
  private String email;

  public AuthResponse(String token) {
    this.token = token;
  }

}