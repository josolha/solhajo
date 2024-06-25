package com.josolha.solhajo.domain.chat.repository;

import com.josolha.solhajo.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
}
