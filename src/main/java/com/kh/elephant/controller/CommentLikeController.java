package com.kh.elephant.controller;

import com.kh.elephant.domain.*;
import com.kh.elephant.service.BoardService;
import com.kh.elephant.service.CommentLikeService;
import com.kh.elephant.service.CommentsService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qiri/*")
@CrossOrigin(origins = {"*"}, maxAge = 6000)
public class CommentLikeController {


    @Autowired
    private CommentLikeService commentLikeService;
    @Autowired
    private CommentsService commentsService;

    @GetMapping("/commentLike")
    public ResponseEntity<List<CommentLike>> showAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(commentLikeService.showAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    // 댓글 하나에 대한 좋아요 개수 조회
//    http://localhost:8080/qiri/commentLike/106
    @GetMapping("/commentLike/{id}")
    public ResponseEntity<Integer> show(@PathVariable int id) {
        List<CommentLike> findByCommentSeq = commentLikeService.findByCommentSeq(id);
        List<CommentLikeDTO> response = new ArrayList<>();

        for(CommentLike item : findByCommentSeq) {
            CommentLikeDTO dto = new CommentLikeDTO();
            dto.setClSEQ(item.getClSEQ());
            dto.setClDate(item.getClDate());
            dto.setComments(item.getComments());
            dto.setUserInfo(item.getUserInfo());
            response.add(dto);
        }

        List<CommentLikeDTO> list = response;

        int total = response.size();

        // 객체방식.. ---> {
        //     List<CommentLikeDTO> list : response
        //     int total : response.size()
        // }
            return ResponseEntity.status(HttpStatus.OK).body(total);
    }

    // 댓글 하나에 대한 댓글좋아요 리스트 조회
    //    http://localhost:8080/qiri/commentLikeList/106
    @GetMapping("/commentLikeList/{id}")
    public ResponseEntity<List<CommentLikeDTO>> showList(@PathVariable int id) {
        List<CommentLike> findByCommentSeq = commentLikeService.findByCommentSeq(id);
        List<CommentLikeDTO> response = new ArrayList<>();

        for(CommentLike item : findByCommentSeq) {
            CommentLikeDTO dto = new CommentLikeDTO();
            dto.setClSEQ(item.getClSEQ());
            dto.setClDate(item.getClDate());
            dto.setComments(item.getComments());
            dto.setUserInfo(item.getUserInfo());
            response.add(dto);
        }

        List<CommentLikeDTO> list = response;

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    // 좋아요 추가
    @PostMapping("/commentLike")
    public ResponseEntity<CommentLike> create(@RequestBody CommentsDTO dto) {

        try {
            CommentLike commentLike = CommentLike.builder()
                    .comments(commentsService.show(dto.getCommentsSEQ()))
                    .userInfo(dto.getUserInfo())
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(commentLikeService.create(commentLike));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

//    @PutMapping("/commentLike")
//    public ResponseEntity<CommentLike> update(@RequestBody CommentLike vo){
//        try {
//            return ResponseEntity.status(HttpStatus.OK).body(commentLike.update(vo));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
//    }

    @Transactional
    @DeleteMapping("/commentLike/{id}")
    public ResponseEntity deleteLike(@PathVariable int id) {
        try {
            commentLikeService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

//    @DeleteMapping("/commentLike/{id}")
//    public ResponseEntity<CommentLike> delete(@PathVariable int id) {
//        try {
//            return ResponseEntity.status(HttpStatus.OK).body(commentLike.delete(id));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
//    }
//


    // 같은 댓글에 좋아요 중복 안되게
}
