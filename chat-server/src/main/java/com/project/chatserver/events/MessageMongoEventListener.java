package com.project.chatserver.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

import com.project.chatserver.dto.MessageDTO;
import com.project.chatserver.service.SequenceGeneratorService;

public class MessageMongoEventListener extends AbstractMongoEventListener<MessageDTO> {

     private SequenceGeneratorService sequenceGenerator;

    @Autowired
    public MessageMongoEventListener(SequenceGeneratorService sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
    }

    @Override
    public void onBeforeConvert(BeforeConvertEvent<MessageDTO> event) {
        if (Integer.parseInt(event.getSource().getId()) < 1) {
            event.getSource().setId(String.valueOf(sequenceGenerator.generateSequence(MessageDTO.SEQUENCE_NAME)));
        }
    }


}
