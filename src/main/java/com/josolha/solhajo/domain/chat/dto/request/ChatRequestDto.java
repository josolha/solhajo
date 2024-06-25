package com.josolha.solhajo.domain.chat.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class ChatRequestDto {

    @Getter
    @Builder
    @ToString
    public static class Register{
        private String name;
        private int capacity;
    }
    @Getter
    @Builder
    @ToString
    public static class ChatMessage{
        private String sender;
        private String content;
        private Long chatRoomMemberId; // 대화방 ID를 추가
        private Long roomId;
    }
}
