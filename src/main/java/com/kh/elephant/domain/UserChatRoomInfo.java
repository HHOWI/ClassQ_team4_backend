package com.kh.elephant.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicInsert
@Table(name = "USER_CHATROOM_INFO")
public class UserChatRoomInfo {
    @Id
    @Column(name = "USER_CHATROOM_INFO_SEQ")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "userChatRoomInfoSequence")
    @SequenceGenerator(name = "userChatRoomInfoSequence", sequenceName = "SEQ_USER_CHATROOM_INFO", allocationSize = 1)
    private int userChatRoomInfoSeq;

    @ManyToOne
    @JoinColumn(name = "CHATROOM_SEQ")
    private ChatRoom chatRoom;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private UserInfo userInfo;

    @Column(name = "LEAVE")
    private String leave;

    @Column(name = "join_date")
    private Date joinDate;

    @Column(name = "JOIN_MESSAGE_SENT")
    private String joinMessageSent;
}
