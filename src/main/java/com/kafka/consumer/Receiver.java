package com.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class Receiver {

    @Autowired
    private DataProcessor dataProcessor;

    @KafkaListener(topics = "${spring.kafka.topic}")
    public void listen(ConsumerRecord<String, String> message) {
        dataProcessor.receivedTimeStamp = System.currentTimeMillis();
        dataProcessor.processedData = message;
        dataProcessor.records.add(message);
        dataProcessor.processingQueue();
    }
}
