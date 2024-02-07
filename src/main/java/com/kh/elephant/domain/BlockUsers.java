package com.kh.elephant.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicInsert
@Builder
public class BlockUsers {

    @Id
    @Column(name="block_user_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "blockUserSequence")
    @SequenceGenerator(name = "blockUserSequence", sequenceName = "SEQ_BLOCK_USERS", allocationSize = 1)
    private int blockUserSeq;

    @ManyToOne
    @JoinColumn(name="user_id")
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "block_id")
    private UserInfo blockInfo;

    @Column(name = "block_date")
    private Date blockDate;
    @Column(name = "block_reason")
    private String blockReason;
    @Column(name = "unblock")
    private String unblock;

}
