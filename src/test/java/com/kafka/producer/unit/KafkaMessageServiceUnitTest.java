package com.kafka.producer.unit;

import com.kafka.producer.KafkaMessageService;
import com.kafka.producer.Message;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaMessageServiceUnitTest {

    @InjectMocks
    KafkaMessageService<String, Message> messageService;

    @Mock
    KafkaTemplate<String, Message> template;

    final String topic = "test";


    @Test
    void SendMessageWithoutKey_failure() {

        // GIVEN

        Message m = Message.buildMessage();
        SettableListenableFuture<SendResult<String, Message>> future = new SettableListenableFuture<>();
        future.setException(new RuntimeException("broker not working"));
        when(template.send(isA(String.class), isA(Message.class))).thenReturn(future);

        // WHEN

        ListenableFuture<SendResult<String, Message>> res = messageService.send(topic, m);

        // THEN

        assertThrows(Exception.class, res::get);

    }

    @Test
    void SendMessageWithoutKey_success() throws ExecutionException, InterruptedException {

        // GIVEN

        int messagePartitionNumber = 3;
        Message m = Message.buildMessage();
        SettableListenableFuture<SendResult<String, Message>> future = new SettableListenableFuture<>();
        ProducerRecord<String, Message> producerRecord = new ProducerRecord<>(topic, m);

        RecordMetadata metadata = new RecordMetadata(new TopicPartition(topic,messagePartitionNumber), 1, 1, new Date().getTime(), 1,1);
        SendResult<String, Message> sendResult = new SendResult<>(producerRecord, metadata);
        future.set(sendResult);
        when(template.send(isA(String.class), isA(Message.class))).thenReturn(future);

        // WHEN

        ListenableFuture<SendResult<String, Message>> res = messageService.send(topic, m);

        // THEN

        SendResult<String, Message> r = res.get();

        assert r.getRecordMetadata().partition() == messagePartitionNumber;

    }
}
