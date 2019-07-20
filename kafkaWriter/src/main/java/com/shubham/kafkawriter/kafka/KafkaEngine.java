package com.shubham.kafkawriter.kafka;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import com.shubham.kafkawriter.configuration.KafkaWriterConfiguration;
import lombok.Getter;

public class KafkaEngine {

  @Getter
  Producer<Long, String> producer = null;
  @Getter
  KafkaMessageHandler kafkaMessageHandler = null;

  public KafkaEngine(KafkaWriterConfiguration configuration) {
    producer = createProducer(configuration);
    kafkaMessageHandler = new KafkaMessageHandler(producer, configuration.getKafkaQueueName());

  }

  private Producer<Long, String> createProducer(KafkaWriterConfiguration configuration) {
    Properties props = new Properties();
    props.putAll(configuration.getKafkaProducerConfig());
    return new KafkaProducer<Long, String>(props);
  }

}
