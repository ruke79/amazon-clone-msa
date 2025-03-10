package com.project.userservice.constants;

import lombok.Getter;

@Getter
public enum AlarmEnum {
    
    CHAT("chatroom/","채팅");

    private final String path;
    private final String alias;

    AlarmEnum(String path, String alias) {
        this.path = path;
        this.alias = alias;
    }
}
