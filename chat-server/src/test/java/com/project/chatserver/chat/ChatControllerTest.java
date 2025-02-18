package com.project.chatserver.chat;

import com.project.chatserver.client.ShoppingServiceClient;
import com.project.chatserver.constants.MessageType;
import com.project.chatserver.controller.ChatController;
import com.project.chatserver.dto.MessageDto;
// import com.project.chatserver.controller.ChatController;
import com.project.chatserver.dto.request.ChatRoomRequest;
import com.project.chatserver.service.ChatService;

// import com.example.plantchatservice.dto.chat.Message;
// import com.example.plantchatservice.dto.redis.ChatRoom;
// import com.example.plantchatservice.dto.vo.ChatRequestDto;
// import com.example.plantchatservice.dto.vo.ChatResponseDto;
// import com.example.plantchatservice.dto.vo.ChatRoomResponseDto;
// import com.example.plantchatservice.dto.vo.ChattingHistoryResponseDto;
// import com.example.plantchatservice.service.chat.ChatRoomService;
// import com.example.plantchatservice.service.chat.ChatService;
import com.project.chatserver.testUser.WithMockCustomAccount;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;


@AutoConfigureMockMvc
@WebMvcTest(controllers = ChatController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class ChatControllerTest extends MvcTestBasic {
    @MockBean
    ChatService chatService;

    @MockBean
    ShoppingServiceClient shoppingServiceClient;
    

    @Test
    @DisplayName("채팅방 생성 테스트")
    @WithMockCustomAccount
    void createChatRoomTest() throws Exception {
        ChatRoomRequest chatRequestDto = new ChatRoomRequest("room1");
        String requestJson = createStringJson(chatRequestDto);
        mvc.perform(post("/chat/room")
                        .headers(GenerateMockToken.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                        .andExpect(status().isOk())
                       .andDo(print());
    }
       

@Test
    @DisplayName("채팅 내역 리스트 조회 테스트")
    @WithMockCustomAccount
    void chattingListTest() throws Exception { 

        
        String roomId = "sdfdsfdfdfdfdf";
        // MessageDTO chat1 = new MessageDTO(MessageType.TEXT_TALK, roomId, "user2@example.com", "yun", "안녕하세요", "", 0, 
        // LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant()
        // );
        
        // MessageDTO chat2 = new MessageDTO(MessageType.TEXT_TALK, roomId, "user2@example.com", "yun", "안녕하세요2", "", 0, 
        // LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant()
        // );

        // MessageDTO chat3 = new MessageDTO(MessageType.TEXT_TALK, roomId, "user2@example.com", "yun", "안녕하세요3", "", 0, 
        // LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant()
        // );

        // MessageDTO chat4 = new MessageDTO(MessageType.TEXT_TALK, roomId, "user2@example.com", "yun", "안녕하세요4", "", 0, 
        // LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant()
        // );

        // given(chatService.saveMessage(chat1)).willReturn("안녕하세요1");
        // given(chatService.saveMessage(chat2)).willReturn("안녕하세요2");
        // given(chatService.saveMessage(chat3)).willReturn("안녕하세요3");
        // given(chatService.saveMessage(chat4)).willReturn("안녕하세요4");        

        
        // List<MessageDTO> chatList = List.of(chat1, chat2, chat3, chat4);

        // given(chatService.getRoomMessagesByCurser(any(), any())).willReturn(chatList);

        // mvc.perform(get("/chat/{roomId}", roomId)
        //  .headers(GenerateMockToken.getToken())
        // .param("cursor", "-1"))
        // .andExpect(status().isOk())
        //          .andExpect(jsonPath("$", hasSize(2)));
        //         // .andExpect(jsonPath("$.chatList[0].content", is("안녕하세요")));
    }

    

}