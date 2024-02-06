package com.kh.elephant.repo;

import com.kh.elephant.domain.UserLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserLikeDAO extends JpaRepository<UserLike, Integer> {

    @Query(value = "SELECT * FROM USER_LIKE WHERE LIKE_UP_USER = :likeUpUser AND LIKE_UP_TARGET = :likeUpTarget", nativeQuery = true)
    UserLike duplicateCheck(@Param("likeUpUser") String likeUpUser, @Param("likeUpTarget") String likeUpTarget);

    @Query(value = "SELECT COUNT(*) FROM USER_LIKE WHERE LIKE_UP_TARGET = :likeUpTarget", nativeQuery = true)
    int findByTarget(@Param("likeUpTarget") String likeUpTarget);
}
