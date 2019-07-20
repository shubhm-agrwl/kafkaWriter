package com.shubham.kafkawriter.configuration;

import lombok.Data;

@Data
public class AuthServerConfig {

  private Boolean authEnabled;

  private String authServerURL;

  private String authServerHost;

  private int authServerPort;

  private Long expiryInterval;

  private String authUserName;

  private String authPassword;

}
