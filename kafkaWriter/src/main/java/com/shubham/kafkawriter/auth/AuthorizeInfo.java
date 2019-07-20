package com.shubham.kafkawriter.auth;

import lombok.Data;

@Data
public class AuthorizeInfo {
  private String apiSecret;

  private long startTime;

  private long expiryTime;
}
