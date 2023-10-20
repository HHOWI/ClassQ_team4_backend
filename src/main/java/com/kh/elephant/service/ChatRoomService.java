//package com.kh.elephant.service;
//
//import com.kh.elephant.domain.ChatRoom;
//import com.kh.elephant.repo.ChatRoomDAO;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.List;
//
//public class ChatRoomService {
//
//    @Autowired
//    private ChatRoomDAO dao;
//
//    public List<ChatRoom> showAll() {
//        return dao.findAll();
//    }
//
//    public ChatRoom show(int code) {
//        return dao.findById(code).orElse(null);
//    }
//
//    public ChatRoom create(ChatRoom chatRoom) {
//        return dao.save(chatRoom);
//    }
//
//    public ChatRoom update(ChatRoom chatRoom) {
//        ChatRoom target = dao.findById(chatRoom.getChatRoomSeq()).orElse(null);
//        if(target!=null) {
//            return dao.save(chatRoom);
//        }
//        return null;
//    }
//
//    public ChatRoom delete(int code) {
//        ChatRoom data = dao.findById(code).orElse(null);
//        dao.delete(data);
//        return data;
//    }
//}
//
