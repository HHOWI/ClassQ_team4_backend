package com.kh.elephant.controller;

import com.kh.elephant.domain.PostAttachments;
import com.kh.elephant.service.PostAttachmentsService;
import com.kh.elephant.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/qiri/*")
@CrossOrigin(origins = {"*"}, maxAge = 6000)
public class PostAttachmentsController {

//    @Value("${youtube.upload.path")
    @Autowired
    private PostAttachmentsService service;
    @Autowired
    private PostService postService;
    private String url;


    // 게시글 전체 조회 http://localhost:8080/qiri/post
    @GetMapping("/postAttachments")
    public ResponseEntity<List<PostAttachments>> showAll() {
        try{

            List<PostAttachments> list = service.showAll();
            log.info(list.toString());
            return ResponseEntity.status(HttpStatus.OK).body(list);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 게시글에 따른 첨부파일 조회
    // 게시글seq로 참부파일정보 가져오기
    @GetMapping("/postAttachments/{id}")
    public ResponseEntity<List<PostAttachments>> findByPostSeq(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.findByPostSEQ(id));
        } catch (Exception e) {
            return null;
        }
    }

    // 게시글 첨부 파일  추가 http://localhost:8080/qiri/post
    @PostMapping("/postAttachments")
    public ResponseEntity<List<String>> uploadFiles(@RequestParam(required = false)List<MultipartFile> files, @RequestParam int postId) throws IOException {
        try{
            List<String> ImageList = new ArrayList<>();

            log.info("일단 첨부파일 등록 확인");
            log.info(files.toString());
            log.info("postID : " + postId);

            List<PostAttachments> deleteList = service.findByPostSEQ(postId);
            log.info(deleteList.toString());

            if(!deleteList.isEmpty())
            {
                for (PostAttachments postAttachments : deleteList) {
                    service.delete(postAttachments.getPostAttachmentSEQ());
                }
            }

            if (files.isEmpty()) {
                //  클라이언트가 사진을 첨부하지 않았다면 아무 동작을 하지 않음
                return ResponseEntity.status(HttpStatus.OK).body(ImageList);
            }
            for (MultipartFile file : files) { // 첨부파일이 여러개 일수 있으니 for문 사용
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename(); // 파일 랜덤 이름 부여랑 원래 이름
                String uploadPath = "C:\\ClassQ_team4_frontend\\qoqiri\\public\\upload"; // 저장 경로

                InputStream inputStream = file.getInputStream(); // 파일 데이터를 읽기
                Path filePath = Paths.get(uploadPath,fileName); //Paths.get를 사용하여 파일 경로를 생성
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING); // 파일 중복으로 올라올 시 덮어쓰기

                log.info(fileName);
                String imageUrl =  fileName;
                ImageList.add(imageUrl); // list에 추가

                PostAttachments postAttachments = PostAttachments.builder()
                        .postCode(postId)
                        .attachmentURL(imageUrl)
                        .build();
                service.create(postAttachments);
            }
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 게시글 수정 http://localhost:8080/qiri/post
    @PutMapping("/postAttachments")
    public ResponseEntity<List<String>> updateFiles(@RequestParam(required = false) List<MultipartFile> files, @RequestParam int postId) throws IOException {
        return ResponseEntity.ok().build();
    }
    // 게시글 삭제 http://localhost:8080/qiri/post/1 <--id

    @DeleteMapping("/postAttachments/deleteAll/{id}")
    public ResponseEntity<PostAttachments> deleteFiles(@PathVariable int id) {
        try {
            log.info("id 확인" + id);
            
            List<PostAttachments> list = service.findByPostSEQ(id);
            log.info(list.toString());

            log.info("리스트 비어 있는지 체크 " + list.isEmpty());

            if(!list.isEmpty())
            {
                log.info("비 어있지 않음");
                service.deleteByPostSeq(id);
                log.info("삭제 성공!");
            }
            else {
                log.info("첨부 파일이 없음.");
            }
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e) {
            log.error("첨부 파일 정보 삭제 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
