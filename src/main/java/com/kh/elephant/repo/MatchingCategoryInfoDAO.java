package com.kh.elephant.repo;

import com.kh.elephant.domain.MatchingCategoryInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MatchingCategoryInfoDAO extends JpaRepository<MatchingCategoryInfo, Integer> {

//    @Transactional
//    @Modifying // post_seq와
//    @Query(value = "INSERT INTO matching_category_info (post_seq, category_seq) VALUES (:post_seq, :category_seq)", nativeQuery = true)
//    void createPostAndCategorySeq(@Param("post_seq") int post_seq, @Param("category_seq") int category_seq);

    @Query(value = "SELECT * FROM matching_category_info WHERE post_seq = :id",nativeQuery = true)
    List<MatchingCategoryInfo> findByPostSeq(@Param("id") int id);
}
