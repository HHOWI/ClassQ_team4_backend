package com.kh.elephant.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "comments")
public class Comments {

    @Id
    @Column(name = "COMMENTS_SEQ")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "commentSequence")
    @SequenceGenerator(name = "commentSequence", sequenceName = "SEQ_COMMENTS", allocationSize = 1)
    private int commentsSEQ;

    @ManyToOne
    @JoinColumn(name = "POST_SEQ")
    private Post post;

    @Column(name = "COMMENTS_PARENT_SEQ")
    private Integer commentsParentSeq;

    @JsonIgnore
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="COMMENTS_PARENT_SEQ", referencedColumnName = "comments_seq", insertable = false, updatable = false)
    private Comments parent;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private UserInfo userInfo;

    @Column(name = "COMMENTS_DESC")
    private String commentDesc;

    @Column(name = "COMMENTS_DATE")
    private Date commentDate;

    @Column(name = "SECRET_COMMENTS")
    private String secretComment;

    @Column(name = "COMMENTS_DELETE")
    private String commentDelete;

}
