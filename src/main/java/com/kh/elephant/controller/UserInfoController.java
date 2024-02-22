package com.kh.elephant.controller;

import com.kh.elephant.domain.SignUpDTO;
import com.kh.elephant.domain.UserCategoryInfo;
import com.kh.elephant.domain.UserInfo;
import com.kh.elephant.domain.UserInfoDTO;
import com.kh.elephant.security.TokenProvider;
import com.kh.elephant.service.UserCategoryInfoService;
import com.kh.elephant.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/qiri/*")
@CrossOrigin(origins = {"*"}, maxAge = 6000)
public class UserInfoController {

    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserInfoService userService;
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    // 유저 전체 조회 http://localhost:8080/qiri/userInfo
    @GetMapping("/userInfo")
    public ResponseEntity<List<UserInfo>> showAllUser() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.showAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 유저 수정 http://localhost:8080/qiri/userInfo/1 <--id
    @PutMapping("/userInfo/editProfile")
    public ResponseEntity<UserInfoDTO> updateUserProfile(@RequestBody SignUpDTO dto) {
        try {
            UserInfo loginUser = userService.show(dto.getUserInfoDTO().getId());

            if (loginUser != null) {
                loginUser.setUserPwd(passwordEncoder.encode(dto.getUserInfoDTO().getPwd()));
                loginUser.setUserName(dto.getUserInfoDTO().getName());
                loginUser.setUserNickname(dto.getUserInfoDTO().getNickname());
                loginUser.setAge(dto.getUserInfoDTO().getAge());
                loginUser.setGender(dto.getUserInfoDTO().getGender());
                loginUser.setPhone(dto.getUserInfoDTO().getPhone());
                loginUser.setEmail(dto.getUserInfoDTO().getEmail());
                loginUser.setStatusMessage(dto.getUserInfoDTO().getStatusMessage());
                loginUser.setHasPartner(dto.getUserInfoDTO().getHasPartner());
                loginUser.setBloodType(dto.getUserInfoDTO().getBloodType());
                loginUser.setMbti(dto.getUserInfoDTO().getMbti());
                loginUser.setBirthday(dto.getUserInfoDTO().getBirthday());
                loginUser.setPlaceType(dto.getUserInfoDTO().getPlaceType());
                loginUser.setProfileImg(dto.getUserInfoDTO().getProfileImg());
//                loginUser.setProfileImg(url);

                UserInfo updatedUser = userService.update(loginUser);

                if (updatedUser != null) {
                    UserInfoDTO responseDTO = UserInfoDTO.builder()
                            .id(updatedUser.getUserId())
                            .nickname(updatedUser.getUserNickname())
                            .build();
                    return ResponseEntity.ok().body(responseDTO);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    // 유저 삭제 http://localhost:8080/qiri/userInfo/1 <--id
    @DeleteMapping("/userInfo/{id}")
    public ResponseEntity<UserInfo> deleteUser(@PathVariable String id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.delete(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 회원가입
    @PostMapping("/userInfo/signup")
    public ResponseEntity<UserInfoDTO> register(@RequestBody SignUpDTO dto) {

        UserInfo user = UserInfo.builder()
                .userId(dto.getUserInfoDTO().getId())
                .userPwd(passwordEncoder.encode(dto.getUserInfoDTO().getPwd()))
                .userName(dto.getUserInfoDTO().getName())
                .userNickname(dto.getUserInfoDTO().getNickname())
                .age(dto.getUserInfoDTO().getAge())
                .gender(dto.getUserInfoDTO().getGender())
                .phone(dto.getUserInfoDTO().getPhone())
                .email(dto.getUserInfoDTO().getEmail())
                .statusMessage(dto.getUserInfoDTO().getStatusMessage())
                .hasPartner(dto.getUserInfoDTO().getHasPartner())
                .bloodType(dto.getUserInfoDTO().getBloodType())
                .mbti(dto.getUserInfoDTO().getMbti())
                .birthday(dto.getUserInfoDTO().getBirthday())
                .placeType(dto.getUserInfoDTO().getPlaceType())
                .profileImg(dto.getUserInfoDTO().getProfileImg())
                .isDeleted("N")
                .joinDate(new Date())
                .build();
        UserInfo registeredUser = userService.create(user); // 회원 정보 저장

        if (registeredUser != null) {
            UserInfoDTO responseDTO = UserInfoDTO.builder()
                    .id(registeredUser.getUserId())
                    .nickname(registeredUser.getUserNickname())
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 로그인 -> token
    @PostMapping("/userInfo/signin")
    public ResponseEntity<UserInfoDTO> authenticate(@RequestBody UserInfoDTO dto) {
        UserInfo userInfo = userService.getByCredentials(dto.getId(), dto.getPwd(), passwordEncoder);
        if (userInfo != null) { // -> 토큰 생성
            String token = tokenProvider.create(userInfo);
            UserInfoDTO responseDTO = userService.buildUserInfoDTO(userInfo, token);
            return ResponseEntity.ok().body(responseDTO);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // 사용자 정보 조회
    @GetMapping("/userInfo/{userId}")
    public ResponseEntity<UserInfo> getUserInfo(@PathVariable String userId) {
        UserInfo user = userService.show(userId);
        if (user != null) {
            return ResponseEntity.ok().body(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 프로필 사진 업로드
    @PostMapping("userInfo/uploadProfilePicture")
    public ResponseEntity<String> uploadProfilePicture(@RequestParam(required = false) MultipartFile file) {
        // MultipartFile : 업로드된 파일을 포함하는 Spring의 객체 (import 해야함)
        try {
            // 프로필 사진을 업로드할 디렉토리 경로 설정
            String uploadDir = "D:\\ClassQ_team4_frontend\\qoqiri\\public\\uploadprofile";

            // 프로필 사진 파일 이름을 생성(고유)
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            // UUID.randomUUID().toString() : 고유한 식별자 생성 -> 파일앞에 붙여서 중복 방지

            // 프로필 사진을 디렉토리에 저장
            Path filePath = Paths.get(uploadDir, fileName); // 파일 경로
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.status(HttpStatus.OK).body(fileName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // 토큰을 이용한 사용자 정보 조회
    @GetMapping("/userInfo/byToken")
    public ResponseEntity<UserInfoDTO> getUserInfoByToken(@RequestHeader("Authorization") String token) {
        try {
            // 1. 토큰에서 사용자 정보 추출
            String userId = tokenProvider.validateAndGetUserId(token);

            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // 2. 사용자 정보 조회
            UserInfo userInfo = userService.show(userId);

            if (userInfo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // 3. 응답 반환
            UserInfoDTO responseDTO = userService.buildUserInfoDTO(userInfo, token);
            return ResponseEntity.ok().body(responseDTO);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}