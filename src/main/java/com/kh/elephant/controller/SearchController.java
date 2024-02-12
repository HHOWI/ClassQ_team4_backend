package com.kh.elephant.controller;

import com.kh.elephant.domain.*;
import com.kh.elephant.service.PostService;
import com.kh.elephant.service.SearchService;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qiri/*")
@CrossOrigin(origins = {"*"}, maxAge = 6000)
public class SearchController {

    @Autowired
    private SearchService searchService;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PostService postService;


    //게시글 검색
    @GetMapping("search/{userId}_{keyword}/{page}")
    public ResponseEntity<List<Post>> headerSearch(@PathVariable("userId") String userId, @PathVariable("keyword") String keyword, @PathVariable("page") int page) {

        try{
            Sort sort = Sort.by("postSEQ").descending();
            Pageable pageable = PageRequest.of(page - 1, 12, sort);

            QPost qPost = QPost.post;
            QBlockUsers qBlockUsers = QBlockUsers.blockUsers;
            QMatchingCategoryInfo qMatchingCategoryInfo = QMatchingCategoryInfo.matchingCategoryInfo;

            BooleanBuilder builder = new BooleanBuilder();
            JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

            // 삭제하지 않았고, 매칭글인 게시물만 가져오도록 조건
            builder.and(qPost.postDelete.eq("N").and(qPost.board.boardSEQ.eq(1)));

            // 로그인했다면 차단한 유저의 게시글 필터링
            if (userId != null) {
                // 차단한 유저 목록에 있는 아이디와 게시물 작성자의 아이디가 일치하지 않는 게시물 가져오도록 조건 추가
                builder.andNot(qPost.userInfo.userId.in(
                        JPAExpressions.select(qBlockUsers.blockInfo.userId)
                                .from(qBlockUsers)
                                .where(qBlockUsers.userInfo.userId.eq(userId))
                ));
            }

            String likePattern = "%" + keyword + "%";
            builder.andAnyOf(
                    qPost.postTitle.likeIgnoreCase(likePattern),
                    qPost.postContent.likeIgnoreCase(likePattern),
                    qPost.userInfo.userNickname.likeIgnoreCase(likePattern),
                    qPost.place.placeName.likeIgnoreCase(likePattern),
                    qPost.place.placeType.placeTypeName.likeIgnoreCase(likePattern),
                    qPost.postSEQ.in(
                            JPAExpressions.select(qMatchingCategoryInfo.post.postSEQ)
                                    .from(qMatchingCategoryInfo)
                                    .where(qMatchingCategoryInfo.category.categoryName.likeIgnoreCase(likePattern))
                    ),
                    qPost.postSEQ.in(
                            JPAExpressions.select(qMatchingCategoryInfo.post.postSEQ)
                                    .from(qMatchingCategoryInfo)
                                    .where(qMatchingCategoryInfo.category.categoryType.ctName.likeIgnoreCase(likePattern))
                    )
            );

            Page<Post> pageResult = postService.showAll(pageable, builder);
            return ResponseEntity.status(HttpStatus.OK).body(pageResult.getContent());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


}
