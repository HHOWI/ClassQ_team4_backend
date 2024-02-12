package com.kh.elephant.service;

import com.kh.elephant.domain.ChatRoom;
import com.kh.elephant.domain.NotificationMessage;
import com.kh.elephant.domain.Post;
import com.kh.elephant.domain.UserInfo;
import com.kh.elephant.repo.NotificationMessageDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationMessageService {

    @Autowired
    private NotificationMessageDAO dao;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public List<NotificationMessage> showAll() {
        return dao.findAll();
    }

    public NotificationMessage show(int code) {
        return dao.findById(code).orElse(null);
    }

    public NotificationMessage create(NotificationMessage notificationMessage) {
        return dao.save(notificationMessage);
    }


    public NotificationMessage update(NotificationMessage notificationMessage) {
        NotificationMessage target = dao.findById(notificationMessage.getNotificationMessageSEQ()).orElse(null);
        if(target!=null) {
            return dao.save(notificationMessage);
        }
        return null;
    }

    public NotificationMessage delete(int code) {
        NotificationMessage data = dao.findById(code).orElse(null);
        dao.delete(data);
        return data;
    }

    @Transactional
    public void deleteByRoomSEQAndUserId(int chatroomSEQ, String userId) {
        dao.deleteByRoomSEQAndUserId(chatroomSEQ, userId);
    }

    public List<NotificationMessage> findByUserId(String userId) {
        // 시간 내림차순으로 정렬 후 반환
        return dao.findByUserId(userId).stream()
                .sorted(Comparator.comparing(NotificationMessage::getSentTime).reversed())
                .collect(Collectors.toList());
    }

    public int unreadNotify(String userId) {
        return dao.unreadNotify(userId);
    }

    public void deleteMyNotify(String userId) {
        dao.deleteMyNotify(userId);
    }

    @Transactional
    public void notifyCheck(String id) {
        dao.notifyCheck(id);
    }

    public int checkDuplicateNotify(String userId, int chatRoomSEQ) {
        return dao.checkDuplicateNotify(userId, chatRoomSEQ);
    }

    // 공통 메서드 - 알림 저장, 웹소켓 전송
    public void notifyProcessing(UserInfo userInfo, String message, Post post, ChatRoom chatRoom) {
        // 알림 DB저장
        NotificationMessage notificationMessage = NotificationMessage.builder()
                .userInfo(userInfo)
                .message(message)
                .post(post)
                .chatRoom(chatRoom)
                .build();
        this.create(notificationMessage);
        // 웹소켓으로 알림 전송
        messagingTemplate.convertAndSend("/sub/notification/" + userInfo.getUserId(), notificationMessage);
    }
}
