package com.kh.elephant.service;

import com.kh.elephant.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatRoomService crService;
    @Autowired
    private UserInfoService uiService;
    @Autowired
    private UserChatRoomInfoService ucriService;
    @Autowired
    private PostService postService;
    @Autowired
    private ChatMessageService cmService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private NotificationMessageService nmService;
    @Autowired
    private MatchingUserInfoService muiService;

    // 채팅방 나가기
    @Transactional
    public void chatRoomLeave(ChatDTO dto) throws Exception {
        UserInfo userInfo = uiService.show(dto.getId());

        // 퇴장 메세지 발송, DB저장
        dto.setNickname(userInfo.getUserNickname());
        dto.setMessage(userInfo.getUserNickname() + "님이 채팅방에서 퇴장하였습니다.");
        dto.setLeave("Y");
        dto.setSendTime(new Date());
        dto.setProfileImg(userInfo.getProfileImg());
        this.createChatMessage(dto);

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
    }

    // 채팅방 최초 접속
    public void joinMessage(ChatDTO dto) {
        UserInfo userInfo = uiService.show(dto.getId());
        // 최조로 채팅방을 열었다면
        if(ucriService.joinMessage(dto.getId(), dto.getChatRoomSEQ()) != 0) {
            // 입장 메세지 발송, DB저장
            dto.setNickname(userInfo.getUserNickname());
            dto.setMessage(userInfo.getUserNickname() + "님이 채팅에 참여하였습니다.");
            dto.setSendTime(new Date());
            dto.setProfileImg(userInfo.getProfileImg());
            this.createChatMessage(dto);
        }
    }

    // 매칭신청시 채팅방 생성 및 접속
    public ChatRoom joinChatRoom(ChatDTO dto) {
        // 채팅방 생성
        ChatRoom chatRoom = this.createChatRoom(dto.getPostSEQ());
        // 채팅방 접속
        this.join(chatRoom.getChatRoomSEQ(), dto.getId());
        this.join(chatRoom.getChatRoomSEQ(), chatRoom.getPost().getUserInfo().getUserId());

        return chatRoom;
    }

    // 매칭 승락한 사람이 모두 접속한 채팅방 생성
    public ChatRoom createGroupChat(ChatDTO dto) {
        // 채팅방 생성
        ChatRoom chatRoom = this.createChatRoom(dto.getPostSEQ());

        // 채팅방 접속
        this.join(chatRoom.getChatRoomSEQ(), dto.getId());

        // 승락한 사람들 구하기
        List<MatchingUserInfo> matchingUserInfoList = muiService.findAccept(dto.getPostSEQ());

        // 승락한 사람들 초대
        for(MatchingUserInfo matchingUserInfo : matchingUserInfoList) {
            // 생성한 채팅방 seq 이용해여 유저채팅정보 생성(어느 채팅방에 접속해 있는지)
            this.join(chatRoom.getChatRoomSEQ(), matchingUserInfo.getUserInfo().getUserId());

            //알림처리
            if(!dto.getId().equals(matchingUserInfo.getUserInfo().getUserId())) {
                nmService.notifyProcessing(matchingUserInfo.getUserInfo(), "게시글 " + chatRoom.getPost().getPostTitle() + "의 그룹채팅방에 초대되었습니다.", chatRoom.getPost(), chatRoom);
            }
        }

        return chatRoom;
    }

    // 채팅 전송 및 db저장, 알림 처리
    @Transactional
    public void sendMessage (ChatDTO dto) {
        // 채팅 메세지 웹소켓 전송 & DB저장
        this.createChatMessage(dto);

        // 채팅 알림처리
        UserInfo userInfo = uiService.findByNickname(dto.getNickname());
        List<UserChatRoomInfo> userChatRoomInfoList = ucriService.findByUserChatRoomSEQ(dto.getChatRoomSEQ());
        for(UserChatRoomInfo user : userChatRoomInfoList) {
                // 발송자는 해당 채팅에 대한 알림 제외 처리
                if(!user.getUserInfo().getUserId().equals(userInfo.getUserId())) {
                    // 한 채팅방에 대한 알림은 확인전까지 한번만
                    if (nmService.checkDuplicateNotify(user.getUserInfo().getUserId(), user.getChatRoom().getChatRoomSEQ()) == 0) {
                        // 채팅알림처리
                        nmService.notifyProcessing(user.getUserInfo(), user.getChatRoom().getPost().getPostTitle() + "의 채팅방에서 새 메세지가 도착했습니다.", user.getChatRoom().getPost(), user.getChatRoom());
                    }
                }
        }
    }

    // 공통 메서드 - 채팅방 접속
    public UserChatRoomInfo join(int chatRoomSEQ, String userId) {
        UserChatRoomInfo userChatRoomInfo = UserChatRoomInfo.builder()
                .chatRoom(crService.show(chatRoomSEQ))
                .userInfo(uiService.show(userId))
                .build();
        UserChatRoomInfo result = ucriService.create(userChatRoomInfo);
        return result;
    }

    // 공통 메서드 - 채팅방 생성
    public ChatRoom createChatRoom(int postSEQ) {
        ChatRoom chatRoom = ChatRoom.builder()
                .post(postService.show(postSEQ))
                .build();
        ChatRoom result = crService.create(chatRoom);
        return result;
    }

    // 공통 메서드 - 채팅 저장 & 발송
    public ChatMessage createChatMessage(ChatDTO dto) {
        // 채팅 DB저장
        UserInfo userInfo = null;
        if (dto.getId() != null) {
            userInfo = uiService.show(dto.getId());
        } else if (dto.getNickname() != null) {
            userInfo = uiService.findByNickname(dto.getNickname());
        }
        ChatMessage chatMessage = ChatMessage.builder()
                .userInfo(userInfo)
                .chatRoom(crService.show(dto.getChatRoomSEQ()))
                .message(dto.getMessage())
                .build();

        ChatMessage result = cmService.create(chatMessage);

        // 채팅 웹소켓 전송
        messagingTemplate.convertAndSend("/sub/chat/room/" + dto.getChatRoomSEQ(), dto);

        return result;
    }


}
