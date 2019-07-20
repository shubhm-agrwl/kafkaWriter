package com.shubham.kafkawriter.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AuthResponse {

  @JsonProperty
  private Long expiryTime;
}
