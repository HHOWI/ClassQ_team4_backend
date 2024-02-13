package com.kh.elephant.repo;

import com.kh.elephant.domain.Comments;
import com.kh.elephant.domain.Post;
import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// 첫번째는 사용할 엔티티 두번째는 primary키의 데이터타입
public interface PostDAO extends JpaRepository<Post, Integer>, QuerydslPredicateExecutor<Post> {

    @Query(value = "SELECT * FROM post WHERE USER_ID = :userId AND BOARD_SEQ = 1 AND POST_DELETE = 'N'", nativeQuery = true)
    List<Post> findPostByUserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM post WHERE USER_ID = :userId AND BOARD_SEQ = 1 AND POST_DELETE = 'N' AND MATCHED = 'N'" , nativeQuery = true)
    List<Post> findNotMatchedPostByUserId(@Param("userId") String userId);


    @Query(value = "SELECT * FROM post WHERE BOARD_SEQ = 2 AND POST_DELETE = 'N'" , nativeQuery = true)
    List<Post> getAllReview();

    @Query(value = "UPDATE POST SET MATCHED = 'Y' WHERE POST_SEQ =:postSEQ", nativeQuery = true)
    int MatchedPost(@Param("postSEQ") int postSEQ);

}
