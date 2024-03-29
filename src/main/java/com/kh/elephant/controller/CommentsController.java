package com.kh.elephant.controller;

import com.kh.elephant.domain.*;
import com.kh.elephant.service.CommentsService;
import com.kh.elephant.service.NotificationMessageService;
import com.kh.elephant.service.PostService;
import com.kh.elephant.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/qiri/*")
@CrossOrigin(origins = {"*"}, maxAge = 6000)
public class CommentsController {


    @Autowired
    private CommentsService commentsService;
    @Autowired
    private PostService postService;
    @Autowired
    private NotificationMessageService nmService;
    @Autowired
    private UserInfoService userInfoService;



    // 게시물 1개에 따른 댓글 전체 조회 : GET - http://localhost:8080/qiri/public/post/1/comments
    @GetMapping("/public/post/{id}/comments")
    public ResponseEntity<List<CommentsDTO>> commentList(@PathVariable int id) {
        try {
            List<Comments> topList = commentsService.getAllTopLevelComments(id);

            List<CommentsDTO> response = new ArrayList<>();

            for (Comments item : topList) {
                if ("N".equals(item.getCommentDelete())) {
                CommentsDTO dto = new CommentsDTO();
                dto.setPost(item.getPost().getPostSEQ());
                dto.setCommentsSEQ(item.getCommentsSEQ());
                dto.setCommentDesc(item.getCommentDesc());
                dto.setCommentDate(item.getCommentDate());
                dto.setUserInfo(item.getUserInfo());
                dto.setCommentDelete(item.getCommentDelete());
                List<Comments> result = commentsService.getRepliesByCommentId(item.getCommentsSEQ(), id);
                dto.setReplies(result);
                response.add(dto);
                }
            }

            return ResponseEntity.status(HttpStatus.OK).body(response.stream()
                    .sorted(Comparator.comparingInt(CommentsDTO::getCommentsSEQ))
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            return null;
        }
    }


    // 게시물 1개에 따른 댓글 개수 조회 : GET - http://localhost:8080/qiri/public/post/1/comments
    @GetMapping("/public/post/{id}/comment")
    public ResponseEntity<Integer> commentsize(@PathVariable int id) {
        List<Comments> topList = commentsService.findByPostSeq(id);
        log.info("top : " + topList);

        int total = 0;  // 댓글 개수 초기화

        List<CommentsDTO> response = new ArrayList<>();

        for (Comments item : topList) {
            if ("N".equals(item.getCommentDelete())) { // N 인 경우에만 개수 증가
                total++;
            }
            CommentsDTO dto = new CommentsDTO();
            dto.setPost(item.getPost().getPostSEQ());
            dto.setCommentsSEQ(item.getCommentsSEQ());
            dto.setCommentDesc(item.getCommentDesc());
            dto.setCommentDate(item.getCommentDate());
            dto.setUserInfo(item.getUserInfo());
            dto.setCommentDelete(item.getCommentDelete());
            List<Comments> result = commentsService.getRepliesByCommentId(item.getCommentsSEQ(), id);
            dto.setReplies(result);
            response.add(dto);
        }

        int responseSize = response.size();

        return ResponseEntity.status(HttpStatus.OK).body(total);

    }

    // 내가 쓴 댓글 가지고 오기
    @GetMapping("/comments/get/{userId}")
    public ResponseEntity<List<Comments>> getUserComments(@PathVariable String userId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(commentsService.findCommentsByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    // 댓글 추가 : POST - http://localhost:8080/qiri/post/comments
    @PostMapping("/post/comments")
    public ResponseEntity<Comments> create(@RequestBody CommentsDTO dto) {
        try {
        Post post = postService.show(dto.getPost());

            Comments comments = Comments.builder()
                    .post(post)
                    .commentsParentSeq(dto.getCommentsParentSeq())
                    .userInfo(userInfoService.show(dto.getUserId()))
                    .commentDesc(dto.getCommentDesc())
                    .build();

        Comments comm = null;
        if (dto.getCommentsParentSeq() != null) {
            comm = commentsService.show(dto.getCommentsParentSeq());
        }
        // 댓글 추가 알림 db 저장 및 웹소켓 알림 발송
        // 일반댓글 처리
        // 게시글 작성자와 댓글 작성자가 같지 않을때(본인이 작성한 게시글이 아닐때) 에만 알림처리
        if (!post.getUserInfo().getUserId().equals(dto.getUserId())) {
            // 부모댓글 여부를 통해 대댓글인지 일반댓글인지 확인
            if (dto.getCommentsParentSeq() == null) {
                // 일반댓글이라면 게시글 작성자에게 알림처리
                nmService.notifyProcessing(post.getUserInfo(), post.getPostTitle() + "에 댓글이 작성되었습니다.", post, null);
            }
            // 대댓글 이라면
            else {
                // 대댓글의 작성자와 댓글 작성자가 같지 않을때(본인이 작성한 댓글이 아닐때) 에만 알림처리
                if (!comm.getUserInfo().getUserId().equals(dto.getUserId())) {
                    nmService.notifyProcessing(commentsService.show(dto.getCommentsParentSeq()).getUserInfo(), post.getPostTitle() + "에 작성한 댓글에 대댓글이 작성되었습니다.", post, null);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(commentsService.create(comments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 댓글 수정 : PUT - http://localhost:8080/qiri/post/comments
    @PutMapping("/post/comments")
    public ResponseEntity<Comments> update(@RequestBody Comments vo) {

        try {
            Comments comments = commentsService.show(vo.getCommentsSEQ());
            comments.setCommentDesc(vo.getCommentDesc());

            return ResponseEntity.status(HttpStatus.OK).body(commentsService.update(comments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    // 댓글 삭제
    @PutMapping("/post/comments/delete")
    public ResponseEntity<Void> delete(@RequestBody Comments vo) {
        // 삭제 대상 댓글이 부모 댓글인지 확인
        if (vo.getCommentsParentSeq() == null) {
            // 부모 댓글 삭제 처리
            commentsService.deleteParentAndChildren(vo.getCommentsSEQ());
        } else {
            // 자식 댓글 삭제 처리
            Comments comments = commentsService.show(vo.getCommentsSEQ());
            comments.setCommentDelete("Y");
            commentsService.delete(comments);
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}

