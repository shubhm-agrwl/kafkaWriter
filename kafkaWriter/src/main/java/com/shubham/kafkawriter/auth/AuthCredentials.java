package com.shubham.kafkawriter.auth;

import lombok.Data;

@Data
public class AuthCredentials {
  private String userName;
  private String password;
  private String apiToken;
}
