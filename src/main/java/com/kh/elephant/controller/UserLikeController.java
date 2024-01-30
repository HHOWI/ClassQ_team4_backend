package com.kh.elephant.controller;

import com.kh.elephant.domain.Board;
import com.kh.elephant.domain.UserInfo;
import com.kh.elephant.domain.UserLike;
import com.kh.elephant.domain.UserLikeDTO;
import com.kh.elephant.service.BoardService;
import com.kh.elephant.service.NotificationMessageService;
import com.kh.elephant.service.UserInfoService;
import com.kh.elephant.service.UserLikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qiri/*")
@CrossOrigin(origins = {"*"}, maxAge = 6000)

public class UserLikeController {

    @Autowired
    private UserLikeService userLikeService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    NotificationMessageController notifyController;

    @GetMapping("/userLike")
    public ResponseEntity<List<UserLike>> showAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userLikeService.showAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/userLike/{id}")
    public ResponseEntity<UserLike> show(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userLikeService.show(id));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    // 유저 좋아요 버튼누르면 작동할 코드
    @Transactional
    @PostMapping("/userInfo/userlike")
    public ResponseEntity<UserLike> create(@RequestBody UserLikeDTO dto) {
        try {
            // 좋아요db에 중복된 데이터가 없는지(좋아요 중복 방지), 누르는 유저와 타겟유저가 다른지(셀프 좋아요 방지)
            if(userLikeService.duplicateCheck(dto.getLikeUpUser(), dto.getLikeUpTarget()) == null && !dto.getLikeUpUser().equals(dto.getLikeUpTarget())) {
                UserInfo targetUser = userInfoService.show(dto.getLikeUpTarget());
                UserLike userLike = UserLike.builder()
                        .likeUpUser(userInfoService.show(dto.getLikeUpUser()))
                        .likeUpTarget(targetUser)
                        .build();
                // 좋아요 정보 DB에 추가
                userLikeService.create(userLike);
                // 유저 좋아요 정보 DB에서 ID 검색후 카운트만큼 유저DB의 좋아요 수치 업데이트
                targetUser.setPopularity(userLikeService.findByTarget(dto.getLikeUpTarget()));
                userInfoService.update(targetUser);
                // 좋아요 받았다는 알림 발송, 알림정보 db저장
                notifyController.notifyProcessing(targetUser, "좋아요를 받았습니다.", null, null);

                return ResponseEntity.status(HttpStatus.OK).body(null);
            }
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/userLike")
    public ResponseEntity<UserLike> update(@RequestBody UserLike vo){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userLikeService.update(vo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/userLike/{id}")
    public ResponseEntity<UserLike> delete(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userLikeService.delete(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
