package com.kafka.producer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class KafkaMessageService<K,V> {

    @Autowired
    KafkaTemplate<K, V> template;

    /*
        Send message with key
     */
    public void send(String topic, V message, K key){
        ListenableFuture<SendResult<K, V>> res = template.send(topic, key, message);
        res.addCallback(messageCallBacks());
    }

    public void send(String topic, V message, K key, Map<String, String> headerList){
        List<Header> messageHeaders = headerList.keySet()
                  .stream()
                  .map(k -> new RecordHeader(k, headerList.get(k).getBytes()))
                                   .collect(Collectors.toList());
        ProducerRecord<K,V> record = new ProducerRecord<>(topic, null, key, message, messageHeaders);
        ListenableFuture<SendResult<K, V>> res = template.send(record);
        res.addCallback(messageCallBacks());
    }

    public ListenableFuture<SendResult<K, V>> send(String topic, V message){
        ListenableFuture<SendResult<K, V>> res = template.send(topic, message);
        res.addCallback(messageCallBacks());
        return res;
    }

    private ListenableFutureCallback<SendResult<K, V>> messageCallBacks(){
        return new ListenableFutureCallback<SendResult<K, V>>() {
            @SneakyThrows
            @Override
            public void onFailure(Throwable ex) {
                log.error(ex.getMessage());
                throw ex;
            }

            @Override
            public void onSuccess(SendResult<K, V> result) {
                log.info("Message sent on, {}", result.getRecordMetadata().topic());
            }
        };
    }
}
