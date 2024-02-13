package com.kh.elephant.controller;

import com.kh.elephant.domain.*;
import com.kh.elephant.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/qiri/*")
@CrossOrigin(origins = {"*"}, maxAge = 6000)
public class ChatController {


    @Autowired
    private UserChatRoomInfoService ucriService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatRoomService crService;
    @Autowired
    private ChatMessageService cmService;



    // 내가 참여중인 채팅방 리스트
    @GetMapping("/public/chatRooms/{id}")
    public ResponseEntity<List<UserChatRoomInfo>> findByUserId(@PathVariable String id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ucriService.findByUserId(id));
        } catch (Exception e) {
            return null;
        }
    }

    // 참여중인 채팅방의 내 참여정보 가져오기(참여메세지 발송여부 확인)
    @GetMapping("/public/chatRoomInfo/{userId}/{code}")
    public ResponseEntity<UserChatRoomInfo> findByUserId(@PathVariable String userId, @PathVariable int code) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ucriService.findByIdAndChatRoomSEQ(code, userId));
        } catch (Exception e) {
            return null;
        }
    }

    //채팅방 접속
    @GetMapping("/chat/room/{id}")
    public ResponseEntity<ChatRoom> show(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(crService.show(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //채팅방의 채팅보기
    @GetMapping("/chat/room/message/{chatRoomSEQ}_{userId}")
    public ResponseEntity<List<ChatMessage>> messageFindByChatroomSEQ(@PathVariable int chatRoomSEQ, @PathVariable String userId) {
        try {
            // SEQ순으로 정렬하여 반환
            return ResponseEntity.status(HttpStatus.OK).body(cmService.messageFindByChatroomSEQ(chatRoomSEQ, userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    //채팅방 나가기
    @PutMapping("/chatroom/leave")
    public ResponseEntity<UserChatRoomInfo> chatRoomLeave(@RequestBody ChatDTO dto) {
        try {
            chatService.chatRoomLeave(dto);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //채팅방에 참여중인 유저목록 가져오기
    @GetMapping("/chatroom/userlist/{code}")
    public ResponseEntity<List<UserChatRoomInfo>> findByChatRoomSEQ(@PathVariable int code) {
        try {
            // 가나다 순으로 정렬
            return ResponseEntity.status(HttpStatus.OK).body(ucriService.findByUserChatRoomSEQ(code));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //참여메세지 최초 접속시에만 발송 조건 처리
    @PutMapping("/chatroom/user/join")
    public ResponseEntity<UserChatRoomInfo> joinMessage(@RequestBody ChatDTO dto) {
        try {
            chatService.joinMessage(dto);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 매칭신청시 채팅방 생성 및 접속
    @PostMapping("/chatroom/join")
    public ResponseEntity<ChatRoom> joinChatRoom(@RequestBody ChatDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(chatService.joinChatRoom(dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 매칭 승락한 사람이 모두 접속한 채팅방 생성
    @PostMapping("/groupChat")
    public ResponseEntity<ChatRoom> createGroupChat(@RequestBody ChatDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(chatService.createGroupChat(dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 채팅 전송 및 db저장, 알림 처리

    @MessageMapping("/chat/message")
    public void sendMessage(ChatDTO dto) {
        try {
        chatService.sendMessage(dto);
        } catch (Exception e) {
            log.error("채팅메시지알림 db저장 오류");
        }
    }




}