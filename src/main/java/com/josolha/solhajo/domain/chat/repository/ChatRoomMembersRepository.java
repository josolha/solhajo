package com.josolha.solhajo.domain.chat.repository;

import com.josolha.solhajo.domain.chat.entity.ChatroomMembers;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomMembersRepository extends JpaRepository<ChatroomMembers,Long> {
    Optional<ChatroomMembers> findByUserIdAndChatRoomId(Long userId, Long chatRoomId);
}
