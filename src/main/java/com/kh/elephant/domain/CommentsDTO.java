package com.kh.elephant.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentsDTO {
    private int post;
    private int commentsSEQ;
    private Date commentDate;
    private UserInfo userInfo;
    private String commentDesc;
    private String commentDelete;
    private Integer commentsParentSeq;
    private String userId;
    @Builder.Default
    private List<Comments> replies = new ArrayList<>();
}
