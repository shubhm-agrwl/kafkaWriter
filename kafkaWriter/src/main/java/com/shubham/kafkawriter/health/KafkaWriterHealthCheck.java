package com.shubham.kafkawriter.health;

import com.codahale.metrics.health.HealthCheck;

public class KafkaWriterHealthCheck extends HealthCheck {

  @Override
  protected Result check() throws Exception {
    return Result.healthy();
  }

}
