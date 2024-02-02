package com.kh.elephant.repo;

import com.kh.elephant.domain.Post;
import com.kh.elephant.domain.PostAttachments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostAttachmentsDAO extends JpaRepository<PostAttachments, Integer>{

    // 게시글에 따른 첨부파일 조회
    @Query(value = "SELECT * FROM post_attachments WHERE post_seq = :id ", nativeQuery = true)
    List<PostAttachments> findByPostSeq(@Param("id") int id);

    @Query(value = "DELETE FROM post_attachments  WHERE post_seq = :id",nativeQuery = true)
    void deleteByPostSeq(int id);
}
