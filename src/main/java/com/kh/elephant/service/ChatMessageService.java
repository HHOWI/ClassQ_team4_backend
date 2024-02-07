package com.kh.elephant.service;


import com.kh.elephant.domain.ChatMessage;

import com.kh.elephant.repo.ChatMessageDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageDAO dao;

    public List<ChatMessage> showAll() {
        return dao.findAll();
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

    public List<ChatMessage> messageFindByChatroomSEQ(int id) {
        return dao.messageFindByChatroomSEQ(id);
    }

    public void deleteChatMessages(int chatroomSeq) {
        dao.deleteByRoomSEQ(chatroomSeq);
    }
}


