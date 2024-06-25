package com.josolha.solhajo.domain.chat.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_ID")
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String content;

    @Column(nullable = false)
    private boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="CHATROOMMEMBERS_ID")
    private ChatroomMembers chatroomMembers;

    @Builder
    public Message(String content, boolean status, ChatroomMembers chatroomMembers) {
        this.content = content;
        this.status = status;
        this.chatroomMembers = chatroomMembers;
    }
}
