package com.project.chatserver.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

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

import com.project.chatserver.common.util.TokenHandler;
import com.project.chatserver.constants.MessageType;
import com.project.chatserver.dto.ChatRoomDTO;
import com.project.chatserver.dto.MessageDTO;
import com.project.chatserver.dto.request.ChatRoomRequest;
import com.project.chatserver.dto.response.RoomMessagesResponse;
import com.project.chatserver.model.ChatRoom;
import com.project.chatserver.service.ChatService;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Log4j2
@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatController {

    private final TokenHandler tokenHandler;
    private final ChatService chatService;

    // @PostConstruct
    // private void sampleData () {

    //     String roomId = "sdfdsfdfdfdfdf";
    //     MessageDTO chat1 = new MessageDTO("", MessageType.TEXT_TALK, roomId, "user2@example.com", "yun", "안녕하세요", "", 0, 
    //     LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant()
    //     );
        
    //     MessageDTO chat2 = new MessageDTO("",MessageType.TEXT_TALK, roomId, "user2@example.com", "yun", "안녕하세요2", "", 0, 
    //     LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant()
    //     );

    //     MessageDTO chat3 = new MessageDTO("",MessageType.TEXT_TALK, roomId, "user2@example.com", "yun", "안녕하세요3", "", 0, 
    //     LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant()
    //     );

    //     MessageDTO chat4 = new MessageDTO("",MessageType.TEXT_TALK, roomId, "user2@example.com", "yun", "안녕하세요4", "", 0, 
    //     LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant()
    //     );

    //     chatService.saveMessage(chat1);
    //     chatService.saveMessage(chat2);
    //     chatService.saveMessage(chat3);
    //     chatService.saveMessage(chat4);        

    // }


    @PostMapping("/room")
    @ResponseBody
    public ChatRoomDTO CreateChatRoom(@RequestBody ChatRoomRequest chatroomReqest, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ChatRoomDTO dto = chatService.createChatRoom(chatroomReqest.getRoomName(), getUserId(request));

        return dto;
    }
    
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoomDTO> room(){
        List<ChatRoom> allRoom = chatService.fildAllRooms();
        log.info(allRoom.size());
        List<ChatRoomDTO> all = new ArrayList<>();
        for (ChatRoom chatRoom : allRoom) {
            ChatRoomDTO chatRoomDto = ChatRoomDTO.builder()
                    .name(chatRoom.getRoomName())
                    .roomId(chatRoom.getRoomUid())                    
                    .build();
            
            all.add(chatRoomDto);
        }
        
        return all;
    }

    @MessageMapping("/chat/message")
    public void sendMessage(@Valid MessageDTO request) {
            chatService.sendMessage(request);
    }

    @MessageMapping("/chat/enter")
	public void enterChatRoom( @RequestBody MessageDTO msg, SimpMessageHeaderAccessor headerAccessor) {

        chatService.sendEnterMessage(msg, headerAccessor);
    }

    @MessageMapping("/chat/leave")
	public void leaveChatRoom( @RequestBody MessageDTO msg, SimpMessageHeaderAccessor headerAccessor) {

        chatService.sendLeaveMessage(msg, headerAccessor);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<MessageDTO>> getRoomMessages(@PathVariable("roomId") String roomId,
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
