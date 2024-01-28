package com.kh.elephant.repo;

import com.kh.elephant.domain.MatchingUserInfo;
import com.kh.elephant.domain.UserChatRoomInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserChatRoomInfoDAO extends JpaRepository<UserChatRoomInfo, Integer> {

    @Query(value = "SELECT * FROM USER_CHATROOM_INFO WHERE USER_ID = :id AND LEAVE = 'N'", nativeQuery = true)
    List<UserChatRoomInfo> findByUserId(@Param("id") String id);

    @Query(value = "SELECT * FROM USER_CHATROOM_INFO WHERE CHATROOM_SEQ = :code AND LEAVE = 'N'", nativeQuery = true)
    List<UserChatRoomInfo> findByChatRoomSEQ(@Param("code") int code);

    @Modifying
    @Transactional
    @Query(value = "UPDATE USER_CHATROOM_INFO SET LEAVE = 'Y' WHERE USER_ID = :id AND CHATROOM_SEQ = :code", nativeQuery = true)
    int updateLeaveStatus(@Param("id") String id, @Param("code") int code);

    @Query(value = "SELECT COUNT(*) FROM USER_CHATROOM_INFO WHERE CHATROOM_SEQ = :chatroom_seq AND LEAVE = 'N'", nativeQuery = true)
    int allUsersLeft(@Param("chatroom_seq") int chatroomSeq);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM USER_CHATROOM_INFO WHERE CHATROOM_SEQ = :chatroom_seq", nativeQuery = true)
    int deleteByChatroomSeq(@Param("chatroom_seq") int chatroomSeq);

    @Modifying
    @Transactional
    @Query(value = "UPDATE USER_CHATROOM_INFO SET JOIN_MESSAGE_SENT = 'Y' WHERE USER_ID = :id AND CHATROOM_SEQ = :code", nativeQuery = true)
    int joinMessage(@Param("id") String id, @Param("code") int code);

    @Query(value = "SELECT * FROM USER_CHATROOM_INFO WHERE LEAVE = 'N' AND CHATROOM_SEQ = :code AND USER_ID = :id", nativeQuery = true)
    UserChatRoomInfo findByIdAndChatRoomSEQ(@Param("code") int code, @Param("id") String id);

}
