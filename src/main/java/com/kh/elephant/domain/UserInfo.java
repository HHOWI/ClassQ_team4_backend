package com.kh.elephant.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "user_info")
public class UserInfo {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_pwd")
    private String userPwd;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "USER_NICKNAME")
    private String userNickname;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender")
    private String gender;

    @ManyToOne
    @JoinColumn(name = "place_type_seq")
    private PlaceType placeType;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "profile_img")
    private String profileImg;

    @Column(name = "status_message")
    private String statusMessage;

    @Column(name = "HAS_PARTNER")
    private String hasPartner;

    @Column(name = "blood_type")
    private String bloodType;

    @Column(name = "mbti")
    private String mbti;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "JOIN_DATE")
    private Date joinDate;

    @Column(name = "POPULARITY")
    private int popularity;

    @Column(name = "IS_DELETED")
    @ColumnDefault("N")
    private String isDeleted;

}
