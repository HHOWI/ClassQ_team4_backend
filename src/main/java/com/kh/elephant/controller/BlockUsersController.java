package com.kh.elephant.controller;

import com.kh.elephant.domain.BlockUserDTO;
import com.kh.elephant.domain.BlockUsers;
import com.kh.elephant.service.BlockUsersService;
import com.kh.elephant.service.UserInfoService;
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
public class BlockUsersController {

    @Autowired
    private BlockUsersService service;


    // 유저간 밴 전체 보기
    @GetMapping("/blockUsers")
    public ResponseEntity<List<BlockUsers>> showAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.showAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 내가 차단한 유저 전체 보기
    @GetMapping("/blockUsers/{id}")
    public ResponseEntity<List<BlockUsers>> showBlocUsers(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.showBlockUser(id));
    }

    // 유저 차단하기
    @PostMapping("/blockUsers")
    public ResponseEntity<BlockUsers> create(@RequestBody BlockUserDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.addBlockUser(dto.getUserId(), dto.getBlockId(), dto.getBlockReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 차단 해제
    @Transactional
    @PutMapping("/blockUsers/delete")
    public ResponseEntity<Void> deleteBlockUser(@RequestBody BlockUserDTO dto) {
        try {
            service.deleteBlock(dto.getUserId(), dto.getBlockId());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}


