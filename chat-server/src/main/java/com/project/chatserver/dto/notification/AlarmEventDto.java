package com.project.chatserver.dto.notification;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;

import com.project.chatserver.constants.AlarmEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlarmEventDto implements Serializable {
    private Integer senderNo;
    private Integer receiverNo;
    private AlarmEnum type;
    private String resource;
    private String content;

}
