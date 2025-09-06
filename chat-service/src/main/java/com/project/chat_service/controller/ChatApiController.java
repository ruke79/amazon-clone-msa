package com.project.chat_service.controller;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.project.chat_service.common.util.TokenHandler;
import com.project.chat_service.constants.MessageType;
import com.project.chat_service.dto.ChatRoomDto;
import com.project.chat_service.dto.MessageDto;
import com.project.chat_service.dto.request.ChatRoomRequest;
import com.project.chat_service.dto.response.RoomMessagesResponse;
import com.project.chat_service.model.ChatRoom;
import com.project.chat_service.service.ChatService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Log4j2
@RequiredArgsConstructor
@RestController // RestContro
@RequestMapping("/api/chat")
public class ChatApiController {


        private final TokenHandler tokenHandler;
    private final ChatService chatService;

      @PostMapping("/room")
    @ResponseBody
    public ChatRoomDto CreateChatRoom(@RequestBody ChatRoomRequest chatroomReqest, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ChatRoomDto dto = chatService.createChatRoom(chatroomReqest.getRoomName(), getUserId(request));

        return dto;
    }
    
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoomDto> room(){
        List<ChatRoom> allRoom = chatService.fildAllRooms();
        log.info(allRoom.size());
        List<ChatRoomDto> all = new ArrayList<>();
        for (ChatRoom chatRoom : allRoom) {
            ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                    .name(chatRoom.getRoomName())
                    .roomId(chatRoom.getRoomUid())                    
                    .build();
            
            all.add(chatRoomDto);
        }
        
        return all;
    }

     @GetMapping("/room/{roomId}")
    public ResponseEntity<List<MessageDto>> getRoomMessages(@PathVariable("roomId") String roomId,
                @RequestParam(name = "cursor") String cursor) {
                    log.info("cursor:", cursor);
        
        return ResponseEntity.status(HttpStatus.OK)
			.body(chatService.getRoomMessagesByCurser(roomId, cursor));

    }


    private String getUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        String token = auth.substring(7, auth.length());
        String email = tokenHandler.getUid(token);
        return email;
    }


}
