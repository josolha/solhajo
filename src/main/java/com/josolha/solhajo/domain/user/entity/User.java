package com.josolha.solhajo.domain.user.entity;

import com.josolha.solhajo.domain.BaseEntity;
import com.josolha.solhajo.domain.chat.entity.ChatRoom;
import com.josolha.solhajo.domain.chat.entity.ChatroomMembers;
import com.josolha.solhajo.domain.user.dto.internal.LoginType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "user")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "password")
    String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    private LoginType loginType;

    //별도의 집합 db 테이블에 저장함
    @Column
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    //chatroomMembers와 연관관계 매핑
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ChatroomMembers> chatroomMembersList = new ArrayList<>();

    //chatRoom와 연관관계 매핑
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ChatRoom> chatroom = new ArrayList<>();

    @Builder
    public User(String name, String email, String password,LoginType loginType ,List<String> roles) {
        this.name = name;
        this.email = email;
        this.loginType = loginType;
        this.password = password;
        this.roles = roles;
    }
    @Builder
    public User(Long id, List<String> roles) {
        this.id = id;
        this.roles = roles;
    }
}
