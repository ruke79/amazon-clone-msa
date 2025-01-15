package com.project.chatserver.dto.notification;

import lombok.Getter;

@Getter
public class AlarmDeleteRequest {
    private Long[] idList;
}
