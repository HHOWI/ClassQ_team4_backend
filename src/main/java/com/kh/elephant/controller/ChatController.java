package com.kh.elephant.controller;

import com.kh.elephant.domain.*;
import com.kh.elephant.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qiri/*")
@CrossOrigin(origins = {"*"}, maxAge = 6000)
public class ChatController {

    @Autowired
    private ChatRoomService crService;
    @Autowired
    private UserChatRoomInfoService ucriService;
    @Autowired
    private ChatMessageService cmService;
    @Autowired
    private UserInfoService uiService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private PostService postService;
    @Autowired
    private MatchingUserInfoService muiService;
    @Autowired
    private NotificationMessageService nmService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;



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
    @Transactional
    @GetMapping("/public/chatRoomInfo/{userId}/{code}")
    public ResponseEntity<UserChatRoomInfo> findByUserId(@PathVariable String userId, @PathVariable int code) {
        try {
            UserChatRoomInfo userChatRoomInfo = ucriService.findByIdAndChatRoomSEQ(code, userId);
            return ResponseEntity.status(HttpStatus.OK).body(userChatRoomInfo);
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
    @GetMapping("/chat/room/message/{id}")
    public ResponseEntity<List<ChatMessage>> messageFindByChatroomSEQ(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(cmService.messageFindByChatroomSEQ(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    //채팅방 나가기와 채팅방에 아무도 남아있지 않다면 해당 채팅방 관련 데이터 삭제
    @PutMapping("/chatroom/leave")
    public ResponseEntity<UserChatRoomInfo> chatRoomLeave(@RequestBody ChatDTO dto) {
        UserInfo userInfo = uiService.show(dto.getId());
        try {
            // 채팅방 나가기(UPDATE쿼리문)
            int result = ucriService.chatRoomLeave(userInfo.getUserId(), dto.getChatRoomSEQ());
            // 해당 채팅방 관련 알림 db 삭제
            nmService.deleteByRoomSEQAndUserId(dto.getChatRoomSEQ(), dto.getId());
            //채팅방에 아무도 남아있지 않다면 해당 채팅방 관련 데이터 삭제(SELECT쿼리문, DELETE쿼리문)
            if(result > 0) {
                chatService.leaveChatRoom(dto.getChatRoomSEQ());
            }
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e) {
            log.info("delete error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //채팅방에 참여중인 유저목록 가져오기
    @GetMapping("/chatroom/userlist/{code}")
    public ResponseEntity<List<UserChatRoomInfo>> findByChatRoomSEQ(@PathVariable int code) {
        try {
            List<UserChatRoomInfo> userChatRoomInfos = ucriService.findByUserChatRoomSEQ(code);
            // 가나다순으로 정렬
            Collections.sort(userChatRoomInfos, Comparator.comparing(
                    ucri -> ucri.getUserInfo().getUserNickname()
            ));
            return ResponseEntity.status(HttpStatus.OK).body(userChatRoomInfos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //참여메세지 최초 접속시에만 발송 조건 처리
    @PutMapping("/chatroom/user/join")
    public ResponseEntity<UserChatRoomInfo> joinMessage(@RequestBody ChatDTO dto) {
        try {
            int result = ucriService.joinMessage(dto.getId(), dto.getChatRoomSEQ());
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 매칭신청시 채팅방 생성 및 접속
    @PostMapping("/chatroom/join")
    public ResponseEntity<ChatRoom> joinChatRoom(@RequestBody ChatDTO dto) {
        try {
            Post post = postService.show(dto.getPostSEQ());

            // 채팅방 생성
            ChatRoom chatRoom = ChatRoom.builder()
                    .post(post)
                    .build();
            ChatRoom result = crService.create(chatRoom);

            // 채팅방 접속
            JoinChatRoom(result, dto.getId());
            JoinChatRoom(result, post.getUserInfo().getUserId());

            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 매칭 승락한 사람이 모두 접속한 채팅방 생성
    @PostMapping("/groupChat")
    public ResponseEntity<ChatRoom> createGroupChat(@RequestBody ChatDTO dto) {
        try {
            UserInfo userInfo = uiService.show(dto.getId());
            Post post = postService.show(dto.getPostSEQ());

            // 채팅방 생성
            ChatRoom chatRoom = ChatRoom.builder()
                    .post(post)
                    .build();
            ChatRoom result = crService.create(chatRoom);

            // 채팅방 접속
            UserChatRoomInfo userChatRoomInfo = UserChatRoomInfo.builder()
                    .chatRoom(crService.show(result.getChatRoomSEQ()))
                    .userInfo(userInfo)
                    .build();
            ucriService.create(userChatRoomInfo);

            // 승락한 사람들 구하기
            List<MatchingUserInfo> matchingUserInfoList = muiService.findAccept(dto.getPostSEQ());
            
            // 승락한 사람들 초대
            for(MatchingUserInfo matchingUserInfo : matchingUserInfoList) {
                // 생성한 채팅방 seq 이용해여 유저채팅정보 생성(어느 채팅방에 접속해 있는지)
                userChatRoomInfo = UserChatRoomInfo.builder()
                        .chatRoom(crService.show(result.getChatRoomSEQ()))
                        .userInfo(matchingUserInfo.getUserInfo())
                        .build();
                        ucriService.create(userChatRoomInfo);
                
                        //알림 db 저장
                if(!dto.getId().equals(matchingUserInfo.getUserInfo().getUserId())) {
                        NotificationMessage notificationMessage = NotificationMessage.builder()
                                .userInfo(matchingUserInfo.getUserInfo())
                                .message("게시글 " + post.getPostTitle() + "의 그룹채팅방에 초대되었습니다.")
                                .post(post)
                                .chatRoom(chatRoom)
                                .build();
                        nmService.create(notificationMessage);

                        // 웹소켓으로 알림 전송
                    messagingTemplate.convertAndSend("/sub/notification/" + matchingUserInfo.getUserInfo().getUserId(), notificationMessage);
                }
                }

            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 공통 메서드 - 채팅방 접속
    private void JoinChatRoom(ChatRoom chatRoom, String userId) {
        UserChatRoomInfo userChatRoomInfo = UserChatRoomInfo.builder()
                .chatRoom(chatRoom)
                .userInfo(uiService.show(userId))
                .build();
        ucriService.create(userChatRoomInfo);
    }


}