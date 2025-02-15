package com.project.chatserver.dto.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.chatserver.common.util.LocalDateTimeUtils;
import com.project.chatserver.model.Alarm;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AlarmResponse {
    private Long id;
    private String type;
    private String content;
    private String url;
    private Integer[] publishedAt;
    private Long senderId;

    @JsonProperty("checked")
    private boolean read;
    @JsonProperty("del")
    private boolean del;

    @Builder
    public AlarmResponse(Long id, String type, String content, String url, LocalDateTime publishedAt, Long senderId, boolean read, boolean del) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.url = url;
        this.publishedAt = LocalDateTimeUtils.toArray(publishedAt);
        this.senderId = senderId;
        this.read = read;
        this.del = del;
    }

    public static AlarmResponse toDto(Alarm notification) {
        return AlarmResponse.builder()
                .id(notification.getAlarmId())
                .type(notification.getTypeEnum().getAlias())
                .content(notification.getContent())
                .url(notification.getUrl())
                .publishedAt(notification.getRegDate())
                .read(notification.isRead())
                .del(notification.isDel())
                .senderId(notification.getSenderId())
                .build();

    }
}
