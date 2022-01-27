package com.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/message")
@Slf4j
public class MessageController {

    @Autowired
    KafkaMessageService<?, Message> kafkaMessageService;
    final String topic = "message_creation";

    @GetMapping("/send")
    public String get() {
        log.info("sending message");

        Map<String, String> headers = new HashMap<>();
        headers.put("name", "smith");
        kafkaMessageService.send(topic, Message.buildMessage(), null, headers);
        return "sent";
    }

    @PostMapping("/send")
    public String postMessage(@Valid @RequestBody Message msg) {
        log.info("sending message {}", msg);
        Map<String, String> headers = new HashMap<>();
        headers.put("name", "simon");
        kafkaMessageService.send(topic, msg, null, headers);
        return "sent";
    }


}


