package com.kh.elephant.domain;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

    private String token;

    // DTO 방식으로 처리하기 위한 게시글 작성 관련 필요 데이터

    private String postTitle;

    private String postContent;

    private int postView;

    private int postSEQ;

    private int placeSEQ;

    private int placeTypeSEQ;

    private int boardSEQ;

    private Date postDate;

    private List<Integer> categoryList;

    private List<Integer> categoryTypeList;

    private List<String> attachmentList;

    private String matched;

    private String postDelete;

    private String titleDropbox;

    private Post post;
    private List<String> attList; // 첨부파일 관련
    List<Category> matchList; // 카테고리 관련

    private List<MatchingCategoryInfo> respMatchCategoryList; // 매칭카테고리 관련

}
