package com.santi.messagesp2p.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {

  /**
   * Should be either username or email.
   */
  @NotBlank(message = "Identifier is required")
  private String identifier;

  @NotBlank(message = "Password is required")
  private String password;

}