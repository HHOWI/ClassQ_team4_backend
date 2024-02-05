package com.kh.elephant.repo;

import com.kh.elephant.domain.NotificationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationMessageDAO extends JpaRepository<NotificationMessage, Integer> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM NOTIFICATION_MESSAGE WHERE CHATROOM_SEQ = :roomSEQ AND USER_ID = :id", nativeQuery = true)
    int deleteByRoomSEQAndUserId(@Param("roomSEQ") int roomSEQ, @Param("id") String id);

    @Query(value = "SELECT * FROM NOTIFICATION_MESSAGE WHERE USER_ID = :id", nativeQuery = true)
    List<NotificationMessage> findByUserId(@Param("id") String id);

    @Query(value = "SELECT COUNT(*) FROM NOTIFICATION_MESSAGE WHERE USER_ID = :id AND IS_READ = 'N'", nativeQuery = true)
    int unreadNotify(@Param("id") String id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM NOTIFICATION_MESSAGE WHERE USER_ID = :id", nativeQuery = true)
    void deleteMyNotify(@Param("id") String id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE NOTIFICATION_MESSAGE SET IS_READ = 'Y' WHERE USER_ID = :id", nativeQuery = true)
    void notifyCheck(@Param("id") String id);

    @Query(value = "SELECT COUNT(*) FROM NOTIFICATION_MESSAGE WHERE USER_ID = :id AND CHATROOM_SEQ = :roomSEQ AND IS_READ = 'N'", nativeQuery = true)
    int checkDuplicateNotify(@Param("id") String id, @Param("roomSEQ") int roomSEQ);

}
