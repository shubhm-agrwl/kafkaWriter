package com.shubham.kafkawriter;

import com.google.common.cache.CacheBuilderSpec;
import com.shubham.kafkawriter.auth.KafkaWriterAuthenticator;
import com.shubham.kafkawriter.auth.User;
import com.shubham.kafkawriter.configuration.KafkaWriterConfiguration;
import com.shubham.kafkawriter.health.KafkaWriterHealthCheck;
import com.shubham.kafkawriter.kafka.KafkaEngine;
import com.shubham.kafkawriter.resource.KafkaWriterResource;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaWriterService extends Application<KafkaWriterConfiguration> {

  @Override
  protected void bootstrapLogging() {}

  public static void main(String[] args) throws Exception {

    new KafkaWriterService().run(args);

    // TODO Need to add Vertx Implementation

    // Vertx vertx = Vertx.factory.vertx();
    // vertx.deployVerticle(new AnalyticsVerticle());

  }

  @Override
  public void run(KafkaWriterConfiguration configuration, Environment environment) throws Exception {

    new KafkaWriterAuthenticator(configuration.getAuthServerConfig());

    CachingAuthenticator<BasicCredentials, User> cachingAuthenticator =
        new CachingAuthenticator<BasicCredentials, User>(environment.metrics(),
            new KafkaWriterAuthenticator(configuration.getAuthServerConfig()),
            CacheBuilderSpec.parse("maximumSize=10000,expireAfterWrite=10m"));

    environment.jersey()
        .register(new AuthDynamicFeature(
            new BasicCredentialAuthFilter.Builder<User>().setAuthenticator(cachingAuthenticator)
                .setRealm("BASIC-AUTH-REALM").buildAuthFilter()));

    environment.jersey().register(new AuthValueFactoryProvider.Binder<User>(User.class));

    new KafkaEngine(configuration);

    environment.healthChecks().register("Kafka Writer Server Health Check",
        new KafkaWriterHealthCheck());
    environment.jersey().register(new KafkaWriterResource());

    log.info("Kafka Writer Server started successfully.");

  }
}
