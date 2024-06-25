package com.josolha.solhajo.domain.chat.controller;


import com.josolha.solhajo.domain.chat.dto.request.ChatRequestDto;
import com.josolha.solhajo.domain.chat.service.ChatService;
import com.josolha.solhajo.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<?> chatRoomRegister(@RequestBody ChatRequestDto.Register register,@AuthenticationPrincipal UserDetails currentUser){
        Long userId = Long.parseLong(currentUser.getUsername());
        return chatService.register(register,userId);
    }
    @GetMapping
    public ResponseEntity<?> chatRoomList(){
        return chatService.getList();
    }
    @PostMapping("/{roomId}/members")
    public ResponseEntity<?> chatEnterRoom(@PathVariable Long roomId, @AuthenticationPrincipal UserDetails currentUser){
        Long userId = Long.parseLong(currentUser.getUsername());
        return chatService.addMember(roomId, userId);
    }
    @MessageMapping("/chatroom.sendMessage")
    public void sendMessage(ChatRequestDto.ChatMessage chatMessage) {
        LogUtil.customInfo("chatMessage :"+ chatMessage);
        messagingTemplate.convertAndSend("/chatroom/" + chatMessage.getRoomId(),  chatService.handleChatMessage(chatMessage));
    }
}
