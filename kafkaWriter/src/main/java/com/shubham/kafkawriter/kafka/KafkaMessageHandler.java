package com.shubham.kafkawriter.kafka;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KafkaMessageHandler {

  static Producer<Long, String> producer;
  static AtomicLong counter = new AtomicLong(0);
  static String queueName;

  public KafkaMessageHandler(Producer<Long, String> producer, String kafkaQueueName) {
    KafkaMessageHandler.producer = producer;
    KafkaMessageHandler.queueName = kafkaQueueName;
  }

  public static void handler(String message) throws InterruptedException, ExecutionException {
    final ProducerRecord<Long, String> record =
        new ProducerRecord<Long, String>(queueName, counter.getAndIncrement(), message);
    RecordMetadata metadata = producer.send(record).get();
    if (log.isDebugEnabled()) {
      log.debug("sent record(key={} value={}) meta(partition={}, offset={})\n", record.key(),
          record.value(), metadata.partition(), metadata.offset());
    }
  }

}
