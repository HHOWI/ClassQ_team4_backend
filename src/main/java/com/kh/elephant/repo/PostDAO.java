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


    @Transactional
    // 게시물 등록을 할때 POST와 관련된 데이터들은 POST 테이블에 저장되고 선택한 CATEGORY의 CATEGORY_SEQ와 POST_SEQ는 MATCHING_CATEGORY_INFO 테이블에 저장되게함
    @Query(value = "INSERT INTO matching_category_info (post_seq, category_seq) VALUES (:post_seq, :category_seq)", nativeQuery = true)
    void savePostAndCategorySeq(@Param("post_seq") int post_seq, @Param("category_seq") int category_seq);

    @Query(value = "update post SET postView = postView+1 WHERE  post_seq = :code", nativeQuery = true)
    List<Post> increaseCount(int code); // 게시물을 볼때마다 게시물 조회수가 1씩 올라가는 쿼리문

    // SQL 쿼리문을 사용해서 BOARD를 POST 테이블에서 검색해서 결과를 반환함 :code는 이 쿼리문을 호출할때 POST_SEQ값으로 대체됨
    @Query(value = "SELECT * FROM post WHERE board_seq = :code ORDER BY post_date DESC", nativeQuery = true)
    List<Post>findByBoardCode(int code);

    @Query(value = "SELECT * FROM post WHERE USER_ID = :userId AND BOARD_SEQ = 1 AND POST_DELETE = 'N'", nativeQuery = true)
    List<Post> findPostByUserId(@Param("userId") String userId);

    @Query(value = "SELECT * FROM post WHERE USER_ID = :userId AND BOARD_SEQ = 1 AND POST_DELETE = 'N' AND MATCHED = 'N'" , nativeQuery = true)
    List<Post> findNotMatchedPostByUserId(@Param("userId") String userId);


    @Query(value = "SELECT * FROM post WHERE BOARD_SEQ = 2 AND POST_DELETE = 'N'" , nativeQuery = true)
    List<Post> getAllReview();

    @Query(value = "UPDATE POST SET MATCHED = 'Y' WHERE POST_SEQ =:postSEQ", nativeQuery = true)
    int MatchedPost(@Param("postSEQ") int postSEQ);

}
