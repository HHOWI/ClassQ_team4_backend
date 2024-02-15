package com.kh.elephant.controller;

import com.kh.elephant.domain.*;
import com.kh.elephant.security.TokenProvider;
import com.kh.elephant.service.*;
import com.kh.elephant.domain.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/qiri/*")
@CrossOrigin(origins = {"*"}, maxAge = 6000)
public class ReviewController {

    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private PostService postService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private PlaceService plService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private MatchingUserInfoService muiService;

    // 리뷰글 전체 가져오기
    @GetMapping("/review")
    public ResponseEntity<List<Post>> getAllReview() {
        try {
            // SEQ순으로 정렬후 반환
            return ResponseEntity.status(HttpStatus.OK).body(postService.getAllReview().stream()
                    .sorted(Comparator.comparingInt(Post::getPostSEQ).reversed())
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 리뷰글을 작성할 참여한 매칭 가져오기
    @GetMapping("/my_matching/{id}")
    public ResponseEntity<List<MatchingUserInfo>> getMyReview(@PathVariable String id) {
        try {
            // 내 아이디로 승락되었고 매칭이 성사되었지만 아직 리뷰글을 작성하지 않은 매칭정보 가져오기
            return ResponseEntity.status(HttpStatus.OK).body(muiService.findByUserIdForPostReview(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 리뷰 DB 저장
    @PostMapping("/reviewWrite")
    public ResponseEntity<Post> reviewCreate(@RequestBody PostDTO dto) {

        String userId = tokenProvider.validateAndGetUserId(dto.getToken());

        Post post = Post.builder()
                .postTitle(dto.getPostTitle())
                .postContent(dto.getPostContent())
                .userInfo(userInfoService.show(userId))
                .board(boardService.show(dto.getBoardSEQ()))
                .build();

        muiService.postReview(dto.getPostSEQ());

        return ResponseEntity.ok().body(postService.create(post));
    }

    // 리뷰 수정
    @PutMapping("/reviewUpdate")
    public ResponseEntity<Post> reviewUpdate(@RequestBody PostDTO dto) {

        Board board = boardService.show(dto.getBoardSEQ());
        String userId = tokenProvider.validateAndGetUserId(dto.getToken());
        UserInfo userinfo = userInfoService.show(userId);
        Place place = plService.show(dto.getPlaceSEQ());
        Post post = Post.builder()
                .postSEQ(dto.getPostSEQ())  // 여기서 ID를 설정해야 합니다.
                .postTitle(dto.getPostTitle())
                .postContent(dto.getPostContent())
                .place(place)
                .postDate(new Date())
                .userInfo(userinfo)
                .postDelete("N")
                .matched("N")
                .board(board)
                .build();
        Post updatedPost = postService.update(post);
        if (updatedPost == null) {
        }
        return ResponseEntity.ok().body(updatedPost);
    }



}