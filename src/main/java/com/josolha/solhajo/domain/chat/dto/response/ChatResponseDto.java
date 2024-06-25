package com.josolha.solhajo.domain.chat.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class ChatResponseDto {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class getList{
        private Long id;
        private String name;
        private int capacity;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class roomInfo{
        private Long id;
    }

    @Getter
    @Builder
    @ToString
    public static class ChatMessage{
        private String sender;
        private String content;
    }
    @Getter
    @Builder
    @ToString
    public static class ChatRoomInfo{
        private Long id;
        private String roomName;
        private String ownerName;
    }

}
