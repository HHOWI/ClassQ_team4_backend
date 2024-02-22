package com.kh.elephant.service;


import com.kh.elephant.domain.*;
import com.kh.elephant.repo.UserInfoDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class UserInfoService {

    @Autowired
    private UserInfoDAO dao;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserCategoryInfoService userCategoryInfoService;


    public List<UserInfo> showAll (){
        return dao.findAll();
    }

    public UserInfo show(String id){
        return dao.findById(id).orElse(null);
    }

    public UserInfo findByNickname(String nickname)
    {
        return dao.findByNickname(nickname);
    }



    public UserInfo create(UserInfo vo){
        return dao.save(vo);
    }

    public UserInfo update(UserInfo vo) {
        UserInfo target = dao.findById(vo.getUserId()).orElse(null);
        if (target != null) {
            return dao.save(vo);
        }
        return null;
    }

    public UserInfo delete(String id){
        UserInfo userInfo = dao.findById(id).orElse(null);
        dao.delete(userInfo);
        return userInfo;
    }

    public UserInfo getByCredentials(String id, String password, PasswordEncoder encoder) {
        UserInfo userInfo = dao.findById(id).orElse(null);
        if (userInfo != null && encoder.matches(password, userInfo.getUserPwd())) {
            return userInfo;
        }
        return null;
    }


    public UserInfoDTO buildUserInfoDTO(UserInfo userInfo, String token) {
        return UserInfoDTO.builder()
                .id(userInfo.getUserId())
                .name(userInfo.getUserName())
                .nickname(userInfo.getUserNickname())
                .age(userInfo.getAge())
                .gender(userInfo.getGender())
                .placeType(userInfo.getPlaceType())
                .phone(userInfo.getPhone())
                .email(userInfo.getEmail())
                .statusMessage(userInfo.getStatusMessage())
                .hasPartner(userInfo.getHasPartner())
                .bloodType(userInfo.getBloodType())
                .mbti(userInfo.getMbti())
                .birthday(userInfo.getBirthday())
                .profileImg(userInfo.getProfileImg())
                .token(token)
                .build();
    }

    public UserInfo findIdByEmail(String email) {
        UserInfo user = dao.findByEmail(email);
        if (user != null) {
            return user;
        } else {
            return null;
        }
    }
}
