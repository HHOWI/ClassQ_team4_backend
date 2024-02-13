package com.kh.elephant.service;


import com.kh.elephant.domain.ChatMessage;

import com.kh.elephant.domain.QBlockUsers;
import com.kh.elephant.domain.QChatMessage;
import com.kh.elephant.repo.ChatMessageDAO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageDAO dao;

    public List<ChatMessage> showAll(Predicate predicate) {
        return (List<ChatMessage>) dao.findAll(predicate);
    }

    public ChatMessage show(int code) {
        return dao.findById(code).orElse(null);
    }



    public ChatMessage create(ChatMessage chatMessage) {
        return dao.save(chatMessage);
    }

    public ChatMessage update(ChatMessage chatMessage) {
        ChatMessage target = dao.findById(chatMessage.getChatMessageSEQ()).orElse(null);
        if(target!=null) {
            return dao.save(chatMessage);
        }
        return null;
    }

    public ChatMessage delete(int code) {
        ChatMessage data = dao.findById(code).orElse(null);
        dao.delete(data);
        return data;
    }

    public List<ChatMessage> messageFindByChatroomSEQ(int chatRoomSEQ, String userId) {
        // 차단한 사용자의 채팅은 거르고 가져오기
        QBlockUsers qBlockUsers = QBlockUsers.blockUsers;
        QChatMessage qChatMessage = QChatMessage.chatMessage;

        Predicate predicate = qChatMessage.chatRoom.chatRoomSEQ.eq(chatRoomSEQ)
                .and(qChatMessage.userInfo.userId.notIn(
                        JPAExpressions.select(qBlockUsers.blockInfo.userId)
                                .from(qBlockUsers)
                                .where(qBlockUsers.userInfo.userId.eq(userId))
                ));

        return showAll(predicate).stream().sorted(Comparator.comparing(ChatMessage::getChatMessageSEQ)).collect(Collectors.toList());
    }

    public void deleteChatMessages(int chatroomSeq) {
        dao.deleteByRoomSEQ(chatroomSeq);
    }
}


