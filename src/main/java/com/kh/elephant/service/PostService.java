package com.kh.elephant.service;

import com.kh.elephant.domain.*;
import com.kh.elephant.repo.UserInfoDAO;
import com.kh.elephant.security.TokenProvider;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import com.kh.elephant.repo.PostDAO;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostService {

    @Autowired
    private PostDAO dao;
    @Autowired
    private MatchingCategoryInfoService mciService;
    @Autowired
    private PostAttachmentsService attachmentsService;
    @Autowired
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public Page<Post> showAll(Pageable pageable, BooleanBuilder builder) {
        
        return dao.findAll(builder, pageable); // findAll을 사용하여 게시물 전체의 정보를 조회할 수 있게함
    }

    // 하나의 서비스 안에 여러 가지 기능의 로직을 짤수 있음
    public Post show(int id) {
        
        Post post = dao.findById(id).orElse(null);
        // Spring Data JPA에서 제공하는 findById 사용하여
        // int값으로 설정한 id를 검색해서 id가 일치하면 일치한 게시물을 보여줌 id가 null일 경우 null 반환

        return post;
    }

    public List<MatchingCategoryInfo> getMci(int id){
        // MatchingCategoryInfo Service에 있는 게시물의 카테고리 정보 조회를 하는 findByPostSEQ 메소드를 호출함
        return mciService.findByPostSEQ(id);
    }

    public List<PostAttachments>getAttach(int id){
    // PostAttachments Service에 있는 게시물의 카테고리 정보 조회를 하는 findByPostSeq 메소드를 호출함
        return attachmentsService.findByPostSEQ(id);
    }

    public Post create(Post post){
       // save 메소드를 사용하여 게시글 저장
        return dao.save(post);
    }

    public Post update(Post post) {
    // findById로 수정할 게시물을 찾고
        Post target = dao.findById(post.getPostSEQ()).orElse(null);
        if (target != null) {// 게시물이 null이 아닐경우
            target.setPostTitle(post.getPostTitle());
            target.setPostContent(post.getPostContent());
            target.setPlace(post.getPlace());
            return dao.save(target); // 수정할 값들을 수정 후  Spring Data JPA에서 제공하는 save로 저장
        }
        return null;
    }

    public Post delete(int id) {
        Post post = dao.findById(id).orElse(null); // findById로 삭제할 게시물을 찾고 게시물이 null이 아닐경우
        dao.delete(post); // Spring Data JPA에서 제공하는 delete로 삭제
        return post;
    }

    // seq 숫자가 높은순으로 정렬해서 반환
    public List<Post> findPostByUserId(String userId) { return dao.findPostByUserId(userId).stream().sorted(Comparator.comparingInt(Post::getPostSEQ).reversed()).collect(Collectors.toList()); }

    // seq 숫자가 높은순으로 정렬해서 반환
    public List<Post> findNotMatchedPostByUserId(String userId) { return dao.findNotMatchedPostByUserId(userId).stream().sorted(Comparator.comparingInt(Post::getPostSEQ).reversed()).collect(Collectors.toList()); }

    public List<Post> getAllReview() {
        return dao.getAllReview();
    }

    public int MatchedPost(int postSEQ) { return dao.MatchedPost(postSEQ); }

    @Transactional(readOnly = true)
    public List<Post> matchingPostList(int page, String userId, Integer categoryTypeSEQ, Integer placeSEQ, Integer placeTypeSEQ, Integer onMyCategory) {
        Sort sort = Sort.by("postSEQ").descending();
        Pageable pageable = PageRequest.of(page - 1, 12, sort);

        QPost qPost = QPost.post;
        QBlockUsers qBlockUsers = QBlockUsers.blockUsers;
        QMatchingCategoryInfo qMatchingCategoryInfo = QMatchingCategoryInfo.matchingCategoryInfo;
        QPlace qPlace = QPlace.place;
        QUserCategoryInfo qUserCategoryInfo = QUserCategoryInfo.userCategoryInfo;

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

        // 카테고리타입을 받는다면 해당하는 카테고리타입으로 필터링
        if (categoryTypeSEQ != null) {
            builder.and(qPost.postSEQ.in(
                    JPAExpressions.select(qMatchingCategoryInfo.post.postSEQ)
                            .from(qMatchingCategoryInfo)
                            .where(qMatchingCategoryInfo.category.categoryType.ctSEQ.eq(categoryTypeSEQ))
            ));
        }

        // 지역값을 받는다면 필터링
        if(placeSEQ != null) {
            builder.and(qPost.place.placeSEQ.eq(placeSEQ));
        } else if(placeTypeSEQ != null) {
            builder.and(qPost.place.placeSEQ.in(
                    JPAExpressions.select(qPlace.placeSEQ)
                            .from(qPlace)
                            .where(qPlace.placeType.placeTypeSEQ.eq(placeTypeSEQ))
            ));
        }

        // 내 관심사만 필터링
        if (onMyCategory == 1) {
            // 내 아이디로 검색하여 결과에 있는 categorySEQ 리스트 가져오기
            List<Integer> categorySeqList = queryFactory
                    .select(qUserCategoryInfo.category.categorySEQ)
                    .from(qUserCategoryInfo)
                    .where(qUserCategoryInfo.userInfo.userId.eq(userId))
                    .fetch();

            // 매칭카테고리인포와 포스트를 postSEQ로 조인하여 카테고리SEQ가 포함되어 있는 POST만 필터링
            builder.and(qPost.postSEQ.in(
                    JPAExpressions.select(qMatchingCategoryInfo.post.postSEQ)
                            .from(qMatchingCategoryInfo)
                            .where(qMatchingCategoryInfo.category.categorySEQ.in(categorySeqList))
            ));
        }

        Page<Post> pageResult = this.showAll(pageable, builder);
        return pageResult.getContent();
    }

    @Transactional(readOnly = true)
    public List<Post> matchingSearch(String userId, String keyword, int page) {
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

        Page<Post> pageResult = this.showAll(pageable, builder);
        return pageResult.getContent();
    }
}
