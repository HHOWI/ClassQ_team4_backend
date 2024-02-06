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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@Table(name = "MATCHING_USER_INFO")
public class MatchingUserInfo {

    @Id
    @Column(name = "MATCHING_USER_INFO_SEQ")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "matchingUserInfoSequence")
    @SequenceGenerator(name = "matchingUserInfoSequence", sequenceName = "SEQ_MATCHING_USER_INFO", allocationSize = 1)
    private int matchingUserInfoSeq;

    @ManyToOne
    @JoinColumn(name="post_seq")
    private Post post;

    @ManyToOne
    @JoinColumn(name="user_id")
    private UserInfo userInfo;

    @Column(name = "MATCHING_ACCEPT")
    private String matchingAccept;

    @Column(name = "POST_REVIEW")
    private String postReview;

    @Column(name = "APPLICATION_DATE")
    private Date applicationDate;

}
