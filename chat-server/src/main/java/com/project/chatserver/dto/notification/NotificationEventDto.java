package com.project.chatserver.dto.notification;

import com.project.chatserver.domain.NotifiTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEventDto implements Serializable {
    private Integer senderNo;
    private Integer receiverNo;
    private NotifiTypeEnum type;
    private String resource;
    private String content;

}
