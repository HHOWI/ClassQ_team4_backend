package com.kh.elephant.service;

import com.kh.elephant.domain.MatchingUserInfo;
import com.kh.elephant.domain.UserChatRoomInfo;
import com.kh.elephant.repo.UserChatRoomInfoDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserChatRoomInfoService {

    @Autowired
    private UserChatRoomInfoDAO dao;

    public List<UserChatRoomInfo> showAll() {
        return dao.findAll();
    }

    public UserChatRoomInfo show(int code) {
        return dao.findById(code).orElse(null);
    }

    public UserChatRoomInfo create(UserChatRoomInfo userChatRoomInfo) {
        return dao.save(userChatRoomInfo);
    }

    public UserChatRoomInfo update(UserChatRoomInfo userChatRoomInfo) {
        UserChatRoomInfo target = dao.findById(userChatRoomInfo.getUserChatRoomInfoSeq()).orElse(null);
        if(target!=null) {
            return dao.save(userChatRoomInfo);
        }
        return null;
    }


    public int chatRoomLeave(String id, int code) {
        return dao.updateLeaveStatus(id, code);
    }

    public List<UserChatRoomInfo> findByUserId(String id) {
        // 먼저 참여한 채팅방부터 정렬하여 반환
        return dao.findByUserId(id).stream()
                .sorted(Comparator.comparing(UserChatRoomInfo::getJoinDate).reversed())
                .collect(Collectors.toList());
    }

    public UserChatRoomInfo delete(int code) {
        UserChatRoomInfo data = dao.findById(code).orElse(null);
        dao.delete(data);
        return data;
    }

    public UserChatRoomInfo findByIdAndChatRoomSEQ(int code, String id) {
        return dao.findByIdAndChatRoomSEQ(code, id);
    }

    public List<UserChatRoomInfo> findByUserChatRoomSEQ(int code) {
        // 정렬 후 반환
        return dao.findByChatRoomSEQ(code).stream()
                .sorted(Comparator.comparing(ucri -> ucri.getUserInfo().getUserNickname()))
                .collect(Collectors.toList());
    }

    public int joinMessage(String id, int code) {
        return dao.joinMessage(id, code);
    }

    public void deleteUserChatRoomInfo(int chatroomSeq) {
        dao.deleteByChatroomSeq(chatroomSeq);
    }

    public int leaveChatRoom(int chatroomSeq) throws Exception { return dao.allUsersLeft(chatroomSeq); }

}
