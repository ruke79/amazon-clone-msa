package com.project.chatserver.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.project.chatserver.common.util.TokenHandler;
import com.project.chatserver.dto.ChatRoomDTO;
import com.project.chatserver.dto.request.ChatRoomRequest;
import com.project.chatserver.model.ChatRoom;
import com.project.chatserver.service.ChatService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Log4j2
@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatController {

    private TokenHandler tokenHandler;
    private ChatService chatService;


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
        List<ChatRoomDTO> all = new ArrayList<>();
        for (ChatRoom chatRoom : allRoom) {
            ChatRoomDTO chatRoomDto = ChatRoomDTO.builder()
                    .name(chatRoom.getRoomName())
                    .roomId(chatRoom.getRoomId())                    
                    .build();

            all.add(chatRoomDto);
        }

        return all;
    }

    @GetMapping("/room/{roomNo}")


    private String getUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        String token = auth.substring(7, auth.length());
        String email = tokenHandler.getUid(token);
        return email;
    }

}
