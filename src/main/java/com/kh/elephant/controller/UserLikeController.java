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

    @PostMapping("/userInfo/userlike")
    public ResponseEntity<UserLike> create(@RequestBody UserLikeDTO dto) {
        try {
           userLikeService.userLikeProcess(dto);
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
