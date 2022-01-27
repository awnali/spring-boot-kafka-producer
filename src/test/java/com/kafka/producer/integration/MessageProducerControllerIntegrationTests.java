package com.kafka.producer.integration;

import org.junit.jupiter.api.Test;
import com.kafka.producer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"test"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                                  "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
public class MessageProducerControllerIntegrationTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void sendMessageEvent() {
        ResponseEntity<String> res=  restTemplate.exchange("/message/send", HttpMethod.GET, null, String.class);
        assertEquals(res.getBody(), "sent");
    }

    @Test
    void postMessageEvent() {

        // given
        HttpHeaders headers = new HttpHeaders();
        headers.add("name", "nemo");
        headers.add("content-type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<Message> req = new HttpEntity<>(Message.buildMessage(), headers);

        //when
        ResponseEntity<String> res=  restTemplate.exchange("/message/send", HttpMethod.POST, req, String.class);

        // then
        assertEquals(res.getBody(), "sent");
    }


}
