package com.kh.elephant.repo;

import com.kh.elephant.domain.UserCategoryInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCategoryInfoDAO extends JpaRepository<UserCategoryInfo, Integer> {
    @Query(value = "SELECT * FROM USER_CATEGORY_INFO WHERE USER_ID = :userId", nativeQuery = true)
    List<UserCategoryInfo> findByUserId(@Param("userId") String userId);

}
