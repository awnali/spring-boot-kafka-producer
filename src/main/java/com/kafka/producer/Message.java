package com.kafka.producer;

import com.github.javafaker.Faker;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Random;

@Value
public
class Message {

    @NotNull
    Integer id;

    @NotBlank
    String name;

    @NotBlank
    String address;

    @NotBlank
    String phone;

    boolean isActive;

    public static Message buildMessage() {
        Faker f = new Faker();
        return new Message(new Random().nextInt(1000), f.name().fullName(), f.address().fullAddress(),
                           f.phoneNumber().phoneNumber(), true);
    }
}
