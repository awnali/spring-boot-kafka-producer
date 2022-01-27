package com.kafka.producer.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.producer.KafkaMessageService;
import com.kafka.producer.Message;
import com.kafka.producer.MessageController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(MessageController.class)
public class MessageProducerControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    KafkaMessageService messageService;

    @Test
    void postMessage() throws Exception {
        // GIVEN
        String body = objectMapper.writeValueAsString(Message.buildMessage());

        System.out.println(body);

        doNothing().when(messageService).send(isA(String.class), any(), any(), isA(Map.class));

        // WHEN
        MvcResult result = mockMvc.perform(post("/message/send")
                                .content(body.getBytes())
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                                  .andReturn();
        // THEN
        assertEquals(result.getResponse().getContentAsString(), "sent");
    }

    @Test
    void postMessageGet() throws Exception {

        doNothing().when(messageService).send(isA(String.class), any(), any(), isA(Map.class));
        mockMvc.perform(get("/message/send"));
    }
}
