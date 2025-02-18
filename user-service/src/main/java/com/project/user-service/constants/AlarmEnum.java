package com.project.user-service.constants;

import lombok.Getter;

@Getter
public enum AlarmEnum {
    SNS_COMMENT("snspostlist/","댓글"),    
    SNS_HEART("snspostlist/","좋아요"),
    KEYWORD("bbsdetail/", "키워드"),
    CHAT("chatroom/","채팅");

    private final String path;
    private final String alias;

    AlarmEnum(String path, String alias) {
        this.path = path;
        this.alias = alias;
    }
}
