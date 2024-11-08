package com.example.client.dtos;

//TODO use openApi or something

public record Msg(MsgType type, String id, Task task, String error) {
}
