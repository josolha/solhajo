package com.josolha.solhajo.domain.chat.service;


import com.josolha.solhajo.domain.chat.dto.request.ChatRequestDto;
import com.josolha.solhajo.domain.chat.dto.request.ChatRequestDto.ChatMessage;
import com.josolha.solhajo.domain.chat.dto.response.ChatResponseDto;

import com.josolha.solhajo.domain.chat.entity.ChatRoom;
import com.josolha.solhajo.domain.chat.entity.ChatroomMembers;
import com.josolha.solhajo.domain.chat.entity.Message;
import com.josolha.solhajo.domain.chat.repository.ChatRoomMembersRepository;
import com.josolha.solhajo.domain.chat.repository.ChatRoomRepository;
import com.josolha.solhajo.domain.chat.repository.MessageRepository;
import com.josolha.solhajo.domain.user.entity.User;
import com.josolha.solhajo.domain.user.repository.UserRepository;
import com.josolha.solhajo.util.ApiResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ApiResponse response;
    private final ChatRoomRepository chatRoomRepository;

    private final UserRepository userRepository;

    private final ChatRoomMembersRepository chatRoomMembersRepository;

    private final MessageRepository messageRepository;

    public ResponseEntity<?> register(ChatRequestDto.Register register,Long userId) {

        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return response.fail("해당 유저 없음");
        }
        ChatRoom savedChatRoom = chatRoomRepository.save(ChatRoom.builder()
                .name(register.getName())
                .capacity(register.getCapacity())
                .user(userOptional.get())
                .build());
        return response.success("성공적으로 등록했습니다",ChatResponseDto.roomInfo.builder()
                .id(savedChatRoom.getId()).build());
    }

    public ResponseEntity<?> getList() {
        List<ChatRoom> rooms = chatRoomRepository.findAll();

        List<ChatResponseDto.getList> resultList = rooms.stream()
                .map(room -> ChatResponseDto.getList.builder()
                        .id(room.getId())
                        .name(room.getName())
                        .capacity(room.getCapacity())
                        .build())
                .collect(Collectors.toList());

        return response.success("성공적으로 리스트를 가져왔습니다.", resultList);
    }


    @Transactional
    public ResponseEntity<?> addMember(Long roomId, Long userId) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(roomId);
        if (!chatRoomOptional.isPresent()) {
            return response.fail("해당 채팅방이 존재하지 않습니다.");
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            return response.fail("해당 유저가 존재하지 않습니다.");
        }

        ChatRoom chatRoom = chatRoomOptional.get();
        User user = userOptional.get();

        // 채팅방 인원 수 확인 및 추가
        if (chatRoom.getCurrentMembers() >= chatRoom.getCapacity()) {
            return response.fail("채팅방의 인원이 가득 찼습니다.");
        }

        // 멤버 객체 생성 및 추가
        ChatroomMembers member = ChatroomMembers.builder()
                .chatRoom(chatRoom)
                .user(user)
                .build();

        if (chatRoom.addMember(member)) {
            chatRoomMembersRepository.save(member);
            ChatResponseDto.ChatRoomInfo result = ChatResponseDto.ChatRoomInfo.builder()
                    .id(member.getId())
                    .roomName(chatRoom.getName())
                    .ownerName(chatRoom.getUser().getName())
                    .build();
            return response.success("성공적으로 등록되었습니다.", result);
        } else {
            return response.fail("멤버 추가에 실패하였습니다.");
        }
    }

    public ChatResponseDto.ChatMessage handleChatMessage(ChatMessage chatMessage) {
        Optional<ChatroomMembers> chatroomMembersOptional = chatRoomMembersRepository.findById(chatMessage.getChatRoomMemberId());

        //저장 로직 필요.
        Message savedMessage = messageRepository.save(Message.builder()
                .status(false)
                .content(chatMessage.getContent())
                .chatroomMembers(chatroomMembersOptional.get())
                .build());

        return ChatResponseDto.ChatMessage.builder()
                .content(savedMessage.getContent())
                .sender(chatMessage.getSender())
                .build();
    }


}
