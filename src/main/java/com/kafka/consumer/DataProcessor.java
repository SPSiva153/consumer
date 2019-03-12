package com.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class DataProcessor {

    private static final Logger fileLogger = LoggerFactory.getLogger(DataProcessor.class);

    long receivedTimeStamp;
    ConsumerRecord<String, String> processedData;
    long prevTimestamp = 0, latency = 0, minLatency = 0,
            maxLatency = 0, minProdLatency = 0,
            maxProdLatency = 0, prodConsLatency = 0;
    List<Long> latencies = new ArrayList<Long>();
    List<Long> prodConsLatencies = new ArrayList<Long>();
    List<ConsumerRecord<String, String>> records = new ArrayList<ConsumerRecord<String, String>>();

    void processingQueue() {
        /*Latency calculation between two messages*/
        latency = (prevTimestamp != 0) ? receivedTimeStamp - prevTimestamp : 0;
        /*Latency calculation between producer and consumer */
        prodConsLatency = receivedTimeStamp - processedData.timestamp();

        if (latency != 0) {
            latencies.add(latency);
        }
        if (prodConsLatency != 0) {
            prodConsLatencies.add(prodConsLatency);
        }

        Collections.sort(latencies);
        Collections.sort(prodConsLatencies);

        if (latencies.size() > 0) {
            minLatency = latencies.get(0);
            maxLatency = latencies.get(latencies.size() - 1);
        }
        if (prodConsLatencies.size() > 0) {
            minProdLatency = prodConsLatencies.get(0);
            maxProdLatency = prodConsLatencies.get(prodConsLatencies.size() - 1);
        }
        fileLogger.info("Key => {}, Kafka seq number => {}, Producer timestamp => {}, " +
                        "Receiver timestamp => {}, Min latency b/w P&C => {}, Max latency b/w P&C => {}, " +
                        "Min latency b/w messages => {}, Max latency b/w messages => {}, Avg. latency => {}, total records => {}\n",
                processedData.key(), processedData.offset(), processedData.timestamp(),
                receivedTimeStamp, minProdLatency + "ms", maxProdLatency + "ms", minLatency + "ms",
                maxLatency + "ms", (minLatency + maxLatency / 2) + "ms", records.size());
        prevTimestamp = receivedTimeStamp;
    }

}
