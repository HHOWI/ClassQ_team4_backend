package com.kh.elephant.repo;

import com.kh.elephant.domain.MatchingUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface MatchingUserInfoDAO extends JpaRepository<MatchingUserInfo, Integer>, QuerydslPredicateExecutor<MatchingUserInfo> {


    Optional<MatchingUserInfo> findByUserInfo_UserIdAndPost_PostSEQ(String userId, int postSEQ);

    @Transactional
    @Modifying
    @Query(value = "UPDATE MATCHING_USER_INFO SET MATCHING_ACCEPT = 'Y' WHERE POST_SEQ = :code AND USER_ID = :id", nativeQuery = true)
    int matchingAccept(@Param("code") int code, @Param("id") String id);
    @Transactional
    @Modifying
    @Query(value = "UPDATE MATCHING_USER_INFO SET MATCHING_ACCEPT = 'H' WHERE POST_SEQ = :code AND USER_ID = :id", nativeQuery = true)
    int hideMachingUser(@Param("code") int code, @Param("id") String id);

    @Query(value = "SELECT * FROM MATCHING_USER_INFO WHERE POST_SEQ = :code AND MATCHING_ACCEPT = 'Y'", nativeQuery = true)
    List<MatchingUserInfo> findAccept (@Param("code") int code);

    @Query(value = "SELECT MATCHING_USER_INFO.* FROM MATCHING_USER_INFO JOIN POST ON MATCHING_USER_INFO.POST_SEQ = POST.POST_SEQ WHERE MATCHING_USER_INFO.USER_ID = :id AND MATCHING_ACCEPT = 'Y' AND POST_REVIEW = 'N' AND MATCHED = 'Y'", nativeQuery = true)
    List<MatchingUserInfo> findByUserIdForPostReview(@Param("id") String id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE MATCHING_USER_INFO SET POST_REVIEW = 'Y' WHERE POST_SEQ = :postSEQ", nativeQuery = true)
    int postReview(@Param("postSEQ") int postSEQ);

    @Query(value = "SELECT * FROM MATCHING_USER_INFO WHERE POST_SEQ = :code AND USER_ID = :id", nativeQuery = true)
    MatchingUserInfo findMuiByPostSEQAndUserId(@Param("code") int code, @Param("id") String id);
}
