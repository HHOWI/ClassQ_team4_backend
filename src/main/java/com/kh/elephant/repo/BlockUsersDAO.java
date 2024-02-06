package com.kh.elephant.repo;

import com.kh.elephant.domain.BlockUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlockUsersDAO extends JpaRepository<BlockUsers, Integer> {
    @Query(value = "SELECT * FROM BLOCK_USERS WHERE USER_ID = :id", nativeQuery = true)
    List<BlockUsers> findByUserId(String id);

    @Modifying
    @Query(value = "UPDATE BLOCK_USERS SET UNBLOCK = 'Y', BLOCK_DATE = default WHERE BLOCK_ID = :id", nativeQuery = true)
    void updateByUnblock(@Param("id") String id);

    @Modifying
    @Query(value = "UPDATE BLOCK_USERS SET UNBLOCK = 'N', BLOCK_DATE = default WHERE BLOCK_ID = :id", nativeQuery = true)
    void deleteByUnblock(@Param("id") String id);

}
