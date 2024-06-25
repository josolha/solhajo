package com.josolha.solhajo.domain.chat.entity;

import com.josolha.solhajo.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "chatroomMembers")
public class ChatroomMembers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHATROOMMEBERS_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="CHATROOM_ID")
    private ChatRoom chatRoom;

    @OneToMany(mappedBy = "chatroomMembers", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messagesList = new ArrayList<>();

    @Builder
    public ChatroomMembers(User user, ChatRoom chatRoom) {
        this.user = user;
        this.chatRoom = chatRoom;
    }
}
