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
@Table(name = "chatroom")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHATROOM_ID")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private int currentMembers = 0;  // 현재 인원을 추적하는 필드 추가

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatroomMembers> chatroomMembersList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID")
    private User user;

    @Builder
    public ChatRoom(String name, int capacity, User user) {
        this.name = name;
        this.capacity = capacity;
        this.user = user;
    }

    // 멤버 추가 메소드
    public boolean addMember(ChatroomMembers member) {
        if (currentMembers < capacity) {
            chatroomMembersList.add(member);
            currentMembers++;  // 인원 수 증가
            return true;
        }
        return false;
    }

    // 멤버 제거 메소드
    public boolean removeMember(ChatroomMembers member) {
        if (chatroomMembersList.remove(member)) {
            currentMembers--;  // 인원 수 감소
            return true;
        }
        return false;
    }
}

