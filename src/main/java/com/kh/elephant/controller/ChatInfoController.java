package com.kh.elephant.controller;

import com.kh.elephant.domain.ChatInfo;
import com.kh.elephant.service.ChatInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/qiri/*")
public class ChatInfoController {


    @Autowired
    private ChatInfoService chatInfo;

    @GetMapping("/chatInfo")
    public ResponseEntity<List<ChatInfo>> showAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(chatInfo.showAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/chatInfo/{id}")
    public ResponseEntity<ChatInfo> show(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(chatInfo.show(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @PostMapping("/chatInfo")
    public ResponseEntity<ChatInfo> create(@RequestBody ChatInfo vo) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(chatInfo.create(vo));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @PutMapping("/chatInfo")
    public ResponseEntity<ChatInfo> update(@RequestBody ChatInfo vo){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(chatInfo.update(vo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @DeleteMapping("/chatInfo/{id}")
    public ResponseEntity<ChatInfo> delete(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(chatInfo.delete(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}