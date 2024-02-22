package com.kh.elephant.controller;

import com.kh.elephant.domain.*;
import com.kh.elephant.domain.UserCategoryInfoDTO;
import com.kh.elephant.service.UserCategoryInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qiri/*")
@CrossOrigin(origins = {"*"}, maxAge = 6000)
public class UserCategoryInfoController {

    @Autowired
    private UserCategoryInfoService categoryInfoService;

    // 관심사 카테고리 전체 조회
    @GetMapping("/userCategoryInfo")
    public ResponseEntity<List<UserCategoryInfo>> showAllCategories() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(categoryInfoService.showAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 아이디에 해당하는 관심사 카테고리 상세 조회
    @GetMapping("/userCategoryInfo/{userId}")
    public ResponseEntity<List<UserCategoryInfo>> getUserCategories(@PathVariable String userId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(categoryInfoService.findByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 유저 관심사 카테고리 정보 등록
    @PostMapping("userInfo/userCategoryInfo")
    public ResponseEntity<List<UserCategoryInfo>> createCategories(@RequestBody SignUpDTO dto) {
        try {
        List<UserCategoryInfo> list = new ArrayList<>();
        for(int i=0; i<dto.getUserCategories().size(); i++) {

            UserCategoryInfo info = new UserCategoryInfo();
            UserInfo user = new UserInfo();
            user.setUserId(dto.getUserInfoDTO().getId());

            Category category = new Category();
            category.setCategorySEQ(dto.getUserCategories().get(i).getUserCategorySeq());

            info.setUserInfo(user);
            info.setCategory(category);
            list.add(info);
        }
        categoryInfoService.createAll(list);
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 관심사 카테고리 정보 수정
//    @Transactional
//    @PutMapping("/userCategoryInfo/editProfile")
//    public ResponseEntity<UserCategoryInfo> updateCategory(@RequestBody UserCategoryInfoDTO dto) {
//
//        log.info(dto.getUserInfoDTO().getUserId());
//        // log.info(dto.getUserCategories().)
//
//        // 기존 아이디 카테고리 정보 삭제
//        String userId = dto.getUserInfoDTO().getUserId();
//        categoryInfoService.deleteByUserId(userId);
//
//        List<UserCategoryInfo> list = new ArrayList<>();
//        for (int i = 0; i < dto.getUserCategories().size(); i++) {
//
//            UserCategoryInfo info = new UserCategoryInfo();
//
//            UserInfo user = new UserInfo();
//            user.setUserId(dto.getUserInfoDTO().getUserId());
//
//            Category category = new Category();
//            category.setCategorySEQ(dto.getUserCategories().get(i).getCategorySEQ());
//
//            info.setUserInfo(user);
//            info.setCategory(category);
//            list.add(info);
//        }
//
//        try {
//            categoryInfoService.createAll(list);
//            return ResponseEntity.status(HttpStatus.OK).build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
//    }


    // 관심사 카테고리 정보 삭제
    @DeleteMapping("/userCategoryInfo/{id}")
    public ResponseEntity<UserCategoryInfo> deleteCategory(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(categoryInfoService.delete(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
