package com.josolha.solhajo.domain.chat.repository;

import com.josolha.solhajo.domain.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message,Long> {
}
