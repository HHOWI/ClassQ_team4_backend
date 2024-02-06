package com.kh.elephant.repo;

import com.kh.elephant.domain.MatchingCategoryInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MatchingCategoryInfoDAO extends JpaRepository<MatchingCategoryInfo, Integer> {


    @Query(value = "SELECT * FROM MATCHING_CATEGORY_INFO JOIN CATEGORY USING(CATEGORY_SEQ) WHERE CT_SEQ = :code", nativeQuery = true)
    List<MatchingCategoryInfo> findByCTSEQ(@Param("code") int code);

    // SQL 쿼리문을 사용해서 post_seq를 MatchingCategoryInfo 테이블에서 검색해서 결과를 반환함 :id 이 쿼리문을 호출할때 POST_SEQ값으로 대체됨
    @Query(value = "SELECT * FROM matching_category_info WHERE post_seq = :id",nativeQuery = true)
    List<MatchingCategoryInfo> findByPostSeq(@Param("id") int id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM matching_category_info WHERE post_seq = :id",nativeQuery = true)
    int deleteByPostSeq(@Param("id") int id);

    @Query(value = "UPDATE matching_category_info WHERE post_seq = :id", nativeQuery = true)
    int updateByPostSeq(@Param("id")int id);
}
