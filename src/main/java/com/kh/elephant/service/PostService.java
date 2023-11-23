package com.kh.elephant.service;

import com.kh.elephant.domain.*;
import com.kh.elephant.repo.UserInfoDAO;
import com.kh.elephant.security.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import com.kh.elephant.repo.PostDAO;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;
@Slf4j
@Service
public class PostService {

    @Autowired
    private PostDAO dao;

    @Autowired
    private UserInfoDAO userDao;

    @Autowired
    private PostAttachmentsService postAttachmentsService;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private MatchingCategoryInfoService mciService;

    @Autowired
    private PostAttachmentsService attachmentsService;


    @Autowired UserInfoService userService;
    @Autowired
    private TokenProvider tokenProvider;

    public Page<Post> showAll(Pageable pageable, BooleanBuilder builder) {
        
        return dao.findAll(builder, pageable); // Spring Data JPA에서 제공하는 findAll을 사용하여 게시물 전체의 정보를 조회할수 있게함
    }

    // 하나의 서비스 안에 여러 가지 기능의 로직을 짤수 있음
    public Post show(int id) {
        
        Post post = dao.findById(id).orElse(null);
        // Spring Data JPA에서 제공하는 findById 사용하여
        // int값으로 설정한 id를 검색해서 id가 일치하면 일치한 게시물을 보여줌 id가 null일 경우 null 반환

        if(post != null){
            post.setPostView(post.getPostView()+1);// post가 null이 아닐시 조회수 1 증가

            dao.save(post); // 증가된 조회수 저장
        }

        return post;
    }


    public Post create(Post post){
       // Spring Data JPA에서 제공하는 save 메소드를 사용하여 게시글 저장
        return dao.save(post);
    }



    public Post update(Post post) {
// Spring Data JPA에서 제공하는 findById로 수정할 게시물을 찾고 
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
        Post post = dao.findById(id).orElse(null); // Spring Data JPA에서 제공하는 findById로 삭제할 게시물을 찾고 게시물이 null이 아닐경우
        dao.delete(post); // Spring Data JPA에서 제공하는 delete로 삭제
        return post;
    }


    public List<Post> findByBoardCode(int code) {
        //POST DAO에 작성한 쿼리문을 사용해 BOARD를 POST 테이블에서 검색
        return dao.findByBoardCode(code);
    }


    public Post findByBoardSeqAndPostTitle(int boardSEQ, String postTitle) {
        return dao.findByBoardSeqAndPostTitle(boardSEQ, postTitle)
                .orElse(null);
    }



    public List<MatchingCategoryInfo> getMci(int id){
    // MatchingCategoryInfo Service에 있는 게시물의 카테고리 정보 조회를 하는 findByPostSEQ 메소드를 호출함
        return mciService.findByPostSEQ(id);
    }

    public List<PostAttachments>getAttach(int id){
// PostAttachments Service에 있는 게시물의 카테고리 정보 조회를 하는 findByPostSeq 메소드를 호출함
        return attachmentsService.findByPostSeq(id);
    }

    public List<Post> findPostByUserId(String userId) { return dao.findPostByUserId(userId); }


}
