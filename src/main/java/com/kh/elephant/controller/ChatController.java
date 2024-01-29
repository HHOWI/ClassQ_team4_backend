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
import java.util.List;
import java.util.stream.Collectors;

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
    private PostService postService;
    @Autowired
    private MatchingUserInfoService muiService;
    @Autowired
    private NotificationMessageService nmService;
    @Autowired
    private NotificationMessageController notifyController;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;



    // 내가 참여중인 채팅방 리스트
    @GetMapping("/public/chatRooms/{id}")
    public ResponseEntity<List<UserChatRoomInfo>> findByUserId(@PathVariable String id) {
        try {
            List<UserChatRoomInfo> list = ucriService.findByUserId(id);
            // joinDate를 기준으로 정렬
            Collections.sort(list, Comparator.comparing(UserChatRoomInfo::getJoinDate).reversed());
            return ResponseEntity.status(HttpStatus.OK).body(list);
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
    @GetMapping("/chat/room/message/{id}")
    public ResponseEntity<List<ChatMessage>> messageFindByChatroomSEQ(@PathVariable int id) {
        try {
            // SEQ순으로 정렬하여 반환
            return ResponseEntity.status(HttpStatus.OK).body(cmService.messageFindByChatroomSEQ(id).stream().sorted(Comparator.comparing(ChatMessage::getChatMessageSEQ)).collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    //채팅방 나가기와 채팅방에 아무도 남아있지 않다면 해당 채팅방 관련 데이터 삭제
    @Transactional
    @PutMapping("/chatroom/leave")
    public ResponseEntity<UserChatRoomInfo> chatRoomLeave(@RequestBody ChatDTO dto) {
        UserInfo userInfo = uiService.show(dto.getId());
        try {
            // 채팅방 나가기(UPDATE쿼리문으로 LEAVE 컬럼값 Y로 변경)
            ucriService.chatRoomLeave(userInfo.getUserId(), dto.getChatRoomSEQ());
            // 해당 채팅방 관련 알림 db 삭제
            nmService.deleteByRoomSEQAndUserId(dto.getChatRoomSEQ(), dto.getId());
            //채팅방에 아무도 남아있지 않다면 해당 채팅방 관련 데이터 삭제
            if(ucriService.leaveChatRoom(dto.getChatRoomSEQ()) == 0) { //
                cmService.deleteChatMessages(dto.getChatRoomSEQ());
                ucriService.deleteUserChatRoomInfo(dto.getChatRoomSEQ());
                crService.deleteChatRoom(dto.getChatRoomSEQ());
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
            // 채팅방 생성
            ChatRoom chatRoom = CreateChatRoom(dto.getPostSEQ());

            // 채팅방 접속
            JoinChatRoom(chatRoom.getChatRoomSEQ(), dto.getId());
            JoinChatRoom(chatRoom.getChatRoomSEQ(), chatRoom.getPost().getUserInfo().getUserId());

            return ResponseEntity.status(HttpStatus.OK).body(chatRoom);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 매칭 승락한 사람이 모두 접속한 채팅방 생성
    @PostMapping("/groupChat")
    public ResponseEntity<ChatRoom> createGroupChat(@RequestBody ChatDTO dto) {
        try {
            // 채팅방 생성
            ChatRoom chatRoom = CreateChatRoom(dto.getPostSEQ());

            // 채팅방 접속
            JoinChatRoom(chatRoom.getChatRoomSEQ(), dto.getId());

            // 승락한 사람들 구하기
            List<MatchingUserInfo> matchingUserInfoList = muiService.findAccept(dto.getPostSEQ());
            
            // 승락한 사람들 초대
            for(MatchingUserInfo matchingUserInfo : matchingUserInfoList) {
                // 생성한 채팅방 seq 이용해여 유저채팅정보 생성(어느 채팅방에 접속해 있는지)
                JoinChatRoom(chatRoom.getChatRoomSEQ(), matchingUserInfo.getUserInfo().getUserId());
                
                //알림처리
                if(!dto.getId().equals(matchingUserInfo.getUserInfo().getUserId())) {
                    notifyController.notifyProcessing(matchingUserInfo.getUserInfo(), "게시글 " + chatRoom.getPost().getPostTitle() + "의 그룹채팅방에 초대되었습니다.", chatRoom.getPost(), chatRoom);
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body(chatRoom);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 채팅 전송 및 db저장, 알림 처리
    @Transactional
    @MessageMapping("/chat/message")
    public void message(ChatDTO dto) {
        // 채팅메세지 웹소켓으로 전송
        messagingTemplate.convertAndSend("/sub/chat/room/" + dto.getChatRoomSEQ(), dto);

        UserInfo userInfo = uiService.findByNickname(dto.getNickname());
        ChatRoom chatRoom = crService.show(dto.getChatRoomSEQ());
        try {
            ChatMessage chatMessage = ChatMessage.builder()
                    .userInfo(userInfo)
                    .chatRoom(chatRoom)
                    .message(dto.getMessage())
                    .build();
            // 채팅메세지 db저장
            cmService.create(chatMessage);
        } catch (Exception e) {
            log.error("메세지 db저장 오류");
        }

        // 채팅 알림처리
        List<UserChatRoomInfo> userChatRoomInfoList = ucriService.findByUserChatRoomSEQ(dto.getChatRoomSEQ());
        for(UserChatRoomInfo user : userChatRoomInfoList) {
            try {
                // 발송자는 해당 채팅에 대한 알림 제외 처리
                if(!user.getUserInfo().getUserId().equals(userInfo.getUserId())) {
                    // 한 채팅방에 대한 알림은 확인전까지 한번만
                    if (nmService.checkDuplicateNotify(user.getUserInfo().getUserId(), user.getChatRoom().getChatRoomSEQ()) == 0) {
                        // 채팅알림처리
                        notifyController.notifyProcessing(user.getUserInfo(), user.getChatRoom().getPost().getPostTitle() + "의 채팅방에서 새 메세지가 도착했습니다.", user.getChatRoom().getPost(), user.getChatRoom());
                    }
                }
            } catch (Exception e) {
                log.error("채팅메시지알림 db저장 오류");
            }
        }
    }


    // 공통 메서드 - 채팅방 접속
    private UserChatRoomInfo JoinChatRoom(int chatRoomSEQ, String userId) {
        UserChatRoomInfo userChatRoomInfo = UserChatRoomInfo.builder()
                .chatRoom(crService.show(chatRoomSEQ))
                .userInfo(uiService.show(userId))
                .build();
        UserChatRoomInfo result = ucriService.create(userChatRoomInfo);
        return result;
    }

    // 공통 메서드 - 채팅방 생성
    private ChatRoom CreateChatRoom(int postSEQ) {
        ChatRoom chatRoom = ChatRoom.builder()
                .post(postService.show(postSEQ))
                .build();
        ChatRoom result = crService.create(chatRoom);
        return result;
    }


}