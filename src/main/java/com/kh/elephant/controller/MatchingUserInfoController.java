package com.kh.elephant.controller;

import com.kh.elephant.domain.*;
import com.kh.elephant.repo.MatchingUserInfoDAO;
import com.kh.elephant.security.TokenProvider;
import com.kh.elephant.service.MatchingUserInfoService;
import com.kh.elephant.service.NotificationMessageService;
import com.kh.elephant.service.PostService;
import com.kh.elephant.service.UserInfoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/qiri/*")
@CrossOrigin(origins = {"*"}, maxAge = 6000)
@Slf4j
public class MatchingUserInfoController {

    @Autowired
    private MatchingUserInfoService muiService;
    @Autowired
    private UserInfoService uiService;
    @Autowired
    private PostService postService;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private NotificationMessageService nmService;




    @GetMapping("/matchingUserInfo")
    public ResponseEntity<List<MatchingUserInfo>> showAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(muiService.showAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/matchingUserInfo/{id}")
    public ResponseEntity<MatchingUserInfo> show(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(muiService.show(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 매칭신청
    @PostMapping("/matching_apply")
    public ResponseEntity<MatchingUserInfo> applyCreate(@RequestBody MatchingUserInfoDTO dto) {
        try {
        // 토큰에서 사용자 ID 추출
        String userId = tokenProvider.validateAndGetUserId(dto.getToken());
        UserInfo userInfo = uiService.show(userId);
        Post post = postService.show(dto.getPostSEQ());

        // 기존에 동일한 정보가 있는지 확인
        Optional<MatchingUserInfo> existingData = muiService.findByUserIdAndPostSEQ(userId, dto.getPostSEQ());
        if (existingData.isPresent()) {
            // 이미 존재하는 정보가 있다면, 해당 정보를 알리는 응답 반환
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        // 없다면 작동할 매서드
        // 매칭 신청 알림처리
            nmService.notifyProcessing(post.getUserInfo(), post.getPostTitle() + "에서의 끼리 신청이 있습니다.", post, null);

        // 매칭 신청정보 DB 저장
        MatchingUserInfo matchingUserInfo = MatchingUserInfo.builder()
                .userInfo(userInfo)
                .post(post)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(muiService.create(matchingUserInfo));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 특정 게시물의 매칭 신청 유저 리스트보기
    @PostMapping("/getApplyList")
    public ResponseEntity<List<MatchingUserInfo>> getApplyList(@RequestBody MatchingUserInfoDTO dto) {
        try {
            return ResponseEntity.ok(muiService.findMatchingByPostSEQ(dto.getId(), dto.getPostSEQ()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //매칭 승락
    @PutMapping("/matchingAccept")
    public ResponseEntity<MatchingUserInfo> matchingAccept(@RequestBody ChatDTO dto){
        try {
            //매칭 승락처리(Accept 컬럼값 변경)
            int result = muiService.matchingAccept(dto.getPostSEQ(), dto.getApplicantId());
            Post post = postService.show(dto.getPostSEQ());
            // 알림처리
            nmService.notifyProcessing(uiService.show(dto.getApplicantId()), "신청한 " + post.getPostTitle() + " 끼리가 승락되었습니다.", post, null);

            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    // 매칭 숨기기 처리
    @PutMapping("/hideMachingUser")
    public ResponseEntity<?> hideMachingUser(@RequestBody ChatDTO dto) {
        try {
            int result = muiService.hideMachingUser(dto.getPostSEQ(), dto.getApplicantId());
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    // 매칭 신청한 유저의 신청정보 확인
    @GetMapping("/check_user/{postSEQ}/{userId}")
    public ResponseEntity<MatchingUserInfo> findMuiByPostSEQAndUserId(@PathVariable int postSEQ, @PathVariable String userId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(muiService.findMuiByPostSEQAndUserId(postSEQ, userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    @PutMapping("/MatchingUserInfo")
    public ResponseEntity<MatchingUserInfo> update(@RequestBody MatchingUserInfo matchingUserInfo){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(muiService.update(matchingUserInfo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/MatchingUserInfo/{id}")
    public ResponseEntity<MatchingUserInfo> delete(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(muiService.delete(Integer.parseInt(id)));
    }

}