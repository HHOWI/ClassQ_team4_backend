package com.kh.elephant.controller;

import com.kh.elephant.domain.*;
import com.kh.elephant.security.TokenProvider;
import com.kh.elephant.service.*;
import com.kh.elephant.domain.UserInfo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/qiri/*")
@CrossOrigin(origins = {"*"}, maxAge = 6000)
public class PostController {

    @Autowired
    private TokenProvider tokenProvider; // token을 이용한 유저 정보
    @Autowired
    private PostService postService; // post관련 서비스
    @Autowired
    private UserInfoService userInfoService; // 유저 관련 서비스
    @Autowired
    private PlaceService plService; // 지역 관련 서비스
    @Autowired
    private PlaceTypeService placeTypeService; // 지역 관련 서비스
    @Autowired
    private BoardService boardService; // 게시판 관련 서비스
    @Autowired
    private MatchingUserInfoService muiService;
    @Autowired
    private EntityManager entityManager;

    // 매칭글 리스트 받아오기 조건문, 쿼리DSL 통해 동시 필터링 구현
    @GetMapping("/public/post")
    public ResponseEntity<List<Post>> postList(@RequestParam(name = "page", defaultValue = "1") int page,
                                               @RequestParam(name = "userId", required = false) String userId,
                                               @RequestParam(name = "categoryTypeSEQ", required = false ) Integer categoryTypeSEQ,
                                               @RequestParam(name = "placeSEQ", required = false) Integer placeSEQ,
                                               @RequestParam(name = "placeTypeSEQ", required = false) Integer placeTypeSEQ,
                                               @RequestParam(name = "onMyCategory", required = false) Integer onMyCategory)  {

        try {
            return ResponseEntity.status(HttpStatus.OK).body(postService.matchingPostList(page, userId, categoryTypeSEQ, placeSEQ, placeTypeSEQ, onMyCategory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    // 게시글 상세 보기 http://localhost:8080/qiri/post/1 <--id
    @GetMapping("/public/post/{id}") //GetMapping을 위한 mapping 메소드 경로 설정
    public ResponseEntity<Post> show (@PathVariable int id){ // id를 경로 변수로 받고, 해당 id를 사용하여 게시물 정보를 조회
        try {
            Post result = postService.show(id);
            return ResponseEntity.status(HttpStatus.OK).body(result); // 게시물 정보를 body에 담아서 클라이언트에 전송해서 클라이언트에서 볼 수 있게함
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    @PostMapping("/post") //PostMapping을 위한 메소드 경로 설정
    public ResponseEntity<Post> createPost(@RequestBody PostDTO dto) {
        // dto를 이용한 post 방식이기 때문에 post에 데이터를 넣어주고 db에 저장을 해야함

        try {
            // 게시글 작성에 필요한 Service들
            Place place = plService.show(dto.getPlaceSEQ());

            PlaceType placeType = placeTypeService.show(dto.getPlaceTypeSEQ());

            place.setPlaceType(placeType);

            Board board = boardService.show(dto.getBoardSEQ());

            String userId = tokenProvider.validateAndGetUserId(dto.getToken());
            log.info(userId);
            UserInfo userInfo = userInfoService.show(userId);

            // Post 객체를 post로 변수명 지정해 주고 get으로 dto안에 있는 필요한 것만 뽑아서 생성 
            Post post = Post.builder()
                    .postTitle(dto.getPostTitle())
                    .postContent(dto.getPostContent())
                    .postView(dto.getPostView())
                    .place(place)
                    .userInfo(userInfo)
                    .board(board)
                    .build();

            Post result = postService.create(post);

            // 나의 매칭정보 저장(리뷰작성용)
            MatchingUserInfo matchingUserInfo = MatchingUserInfo.builder()
                    .post(result)
                    .userInfo(userInfo)
                    .matchingAccept("Y")
                    .build();
            muiService.create(matchingUserInfo);

            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            log.info("" + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //   매칭 게시글 수정 http://localhost:8080/qiri/post
    @PutMapping("/post")
    public ResponseEntity<Post> update(@RequestBody PostDTO dto) {
        try {
            // 게시글 수정에 필요한 service들
            Place place = plService.show(dto.getPlaceSEQ()); 

            String userId = tokenProvider.validateAndGetUserId(dto.getToken()); 

            UserInfo userinfo = userInfoService.show(userId); 

            Board board = boardService.show(dto.getBoardSEQ()); 

            // post 안에 있는 수정할 정보들
            Post post = Post.builder()
                    .postSEQ(dto.getPostSEQ()) 
                    .postTitle(dto.getPostTitle())
                    .postContent(dto.getPostContent())
                    .postDate(new Date())
                    .postView(dto.getPostView())
                    .postDelete(dto.getPostDelete())
                    .matched(dto.getMatched())
                    .place(place)
                    .userInfo(userinfo)
                    .board(board)
                    .build();
            log.info("수정 : " + post);
            log.info("dto : " + dto);
            return ResponseEntity.status(HttpStatus.OK).body(postService.update(post));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    //  매칭 게시글 삭제 http://localhost:8080/qiri/post/1 <--id

    @PutMapping("/post/{postSeq}")   // update 형식으로 db에 데이터는 남기고 클라이언트 쪽에선 안보이게 처리
    public ResponseEntity<String> hidePost(@PathVariable int postSeq) {
        try {
            Post post = postService.show(postSeq); 
            if(post==null){ // post가 null일 경우
                return ResponseEntity.badRequest().body("게시물을 찾을 수 없습니다."); // 문자열 반환
            }
            post.setPostDelete("Y"); // postDelete를 Y로 설정 후 db에 저장
            postService.update(post); // 삭제지만 update 형식으로 클라이언트 쪽에서만 안보이게 처리함
            log.info("삭제 ::: " + post);
            return ResponseEntity.ok().body("삭제된 게시물 입니다."); // 삭제 후 문자열 전송
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("게시물 삭제에 실패했습니다.");
        }
    }

    @DeleteMapping("/post/{postSeq}") // DELETEMapping을 위한 메소드 경로 설정
    public ResponseEntity<Post>delete(@PathVariable int id){
        log.info("삭제 ::: "+ postService.delete(id));
        return ResponseEntity.status(HttpStatus.OK).body(postService.delete(id));
        // postService에 Spring Data JPA에서 제공하는 delete로 일치하는 id를 찾아서 삭제
    }

    // 내가쓴 매칭글 가지고 오기
    @GetMapping("/post/get/{userId}")
    public ResponseEntity<List<Post>> findPostByUserId(@PathVariable String userId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(postService.findPostByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 내가쓴 매칭글 중 매칭진행중인 글만 가지고 오기
    @GetMapping("/post_not_matched/{userId}")
    public ResponseEntity<List<Post>> findNotMatchedPostByUserId(@PathVariable String userId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(postService.findNotMatchedPostByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 매칭글 매칭완료 처리
    @PutMapping("/matched_post/{postSEQ}")
    public ResponseEntity<Integer> MatchedPost(@PathVariable int postSEQ) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(postService.MatchedPost(postSEQ));
        } catch (Exception e) {
            return null;
        }
    }

    // 게시글 조회수 증가
    @PutMapping("/post_viewcount/{postSEQ}")
    public ResponseEntity viewCount(@PathVariable int postSEQ) {
        try {
            Post post = postService.show(postSEQ);
            post.setPostView(post.getPostView() + 1);
            postService.update(post);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //게시글 검색
    @GetMapping("search/{userId}_{keyword}/{page}")
    public ResponseEntity<List<Post>> headerSearch(@PathVariable("userId") String userId, @PathVariable("keyword") String keyword, @PathVariable("page") int page) {
        try{
            return ResponseEntity.status(HttpStatus.OK).body(postService.matchingSearch(userId, keyword, page));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }



}



