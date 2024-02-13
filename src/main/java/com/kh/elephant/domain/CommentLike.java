package com.kh.elephant.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@Builder
public class CommentLike {

    @Id
    @Column(name="cl_seq")
    @GeneratedValue(strategy= GenerationType.IDENTITY, generator = "clSeq")
    @SequenceGenerator(name = "clSeq", sequenceName = "SEQ_COMMENT_LIKE", allocationSize = 1)
    private int clSEQ;

    @Column(name = "cl_date")
    private Date clDate;

    @ManyToOne
    @JoinColumn(name = "comments_seq")
    private Comments comments;

    @ManyToOne
    @JoinColumn(name="user_id")
    private UserInfo userInfo;
}
