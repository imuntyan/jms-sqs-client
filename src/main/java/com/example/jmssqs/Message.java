package com.example.jmssqs;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class Message {
    @Getter @Setter
    private String msg;
}
