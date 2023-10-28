package com.kh.elephant.repo;

import com.kh.elephant.domain.ChatMessage;
import com.kh.elephant.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatMessageDAO extends JpaRepository<ChatMessage, Integer> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM CHATMESSAGE WHERE CHATROOM_SEQ = :id", nativeQuery = true)
    int deleteByRoomSEQ(@Param("id") int id);

    @Query(value = "SELECT * FROM CHATMESSAGE WHERE CHATROOM_SEQ = :id", nativeQuery = true)
    List<ChatMessage> messageFindByChatroomSEQ(@Param("id") int id);
}
