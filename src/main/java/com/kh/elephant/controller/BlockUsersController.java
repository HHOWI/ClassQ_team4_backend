package com.kh.elephant.controller;

import com.kh.elephant.domain.BlockUsers;
import com.kh.elephant.service.BlockUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BlockUsersController {

    @Autowired
    private BlockUsersService service;

    @GetMapping("/blockUsers")
    public ResponseEntity<List<BlockUsers>> showAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.showAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/blockUsers/{id}")
    public ResponseEntity<BlockUsers> show(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.show(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/blockUsers")
    public ResponseEntity<BlockUsers> create(@RequestBody BlockUsers vo) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.create(vo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/blockUsers")
    public ResponseEntity<BlockUsers> update(@RequestBody BlockUsers vo) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.update(vo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/blockUsers/{id}")
    public ResponseEntity<BlockUsers> delete(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.delete(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

