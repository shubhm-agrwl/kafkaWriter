package com.shubham.kafkawriter.configuration;

import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import lombok.Data;

public @Data class KafkaWriterConfiguration extends Configuration {

  @NotNull
  @Valid
  @JsonProperty
  private Map<String, String> kafkaProducerConfig;

  @Valid
  @NotNull
  @JsonProperty
  private AuthServerConfig authServerConfig;
  
  @Valid
  @NotNull
  @JsonProperty
  private String kafkaQueueName;

}
