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
    public ResponseEntity<Boolean> uploadFiles(@RequestParam(required = false)List<MultipartFile> files,
            @RequestParam(required = false) List<String> urls, @RequestParam int postId) throws IOException {
        try{


            List<PostAttachments> deleteList = service.findByPostSEQ(postId);
            if(!deleteList.isEmpty())
            {

                for (PostAttachments postAttachments : deleteList) {

                    service.delete(postAttachments.getPostAttachmentSEQ());
                }
            }

            // 기존 새롭게 덮지 않은 이미지 인경우.. 즉, urls에 뭔가 데이터가 들어 있는 경우에..
            if(urls == null)
            {
                // 기존 url 이 달려있는 기존 데이터가 넘어오지 않고 새롭게 첨부된 무언가가 넘어올떄..

                if(files == null)
                {
                    return ResponseEntity.status(HttpStatus.OK).body(false);
                }

                for (MultipartFile file : files) { // 첨부파일이 여러개 일수 있으니 for문 사용
                    String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename(); // 파일 랜덤 이름 부여랑 원래 이름
                    String uploadPath = "D:\\ClassQ_team4_frontend\\qoqiri\\public\\upload"; // 저장 경로

                    InputStream inputStream = file.getInputStream(); // 파일 데이터를 읽기
                    Path filePath = Paths.get(uploadPath,fileName); //Paths.get를 사용하여 파일 경로를 생성
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING); // 파일 중복으로 올라올 시 덮어쓰기

                    log.info(fileName);



                    PostAttachments postAttachments = PostAttachments.builder()
                            .postCode(postId)
                            .attachmentURL(fileName)
                            .build();
                    service.create(postAttachments);
                }

            }
            else{
                // 하나라도 기존의 데이터가 넘어 왔을떄..
                for (String path : urls) { // 첨부파일이 여러개 일수 있으니 for문 사용
                    PostAttachments postAttachments = PostAttachments.builder()
                            .postCode(postId)
                            .attachmentURL(path)
                            .build();
                    service.create(postAttachments);
                }

            }

            return ResponseEntity.status(HttpStatus.OK).body(true);

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

    @PostMapping("/deleteFiles")
    public ResponseEntity<Boolean> deleteFiles(@RequestParam List<String> files)
    {
        try{
            String path = "C:\\ClassQ_team4_frontend\\qoqiri\\public\\upload"; // 저장 경로
            log.info("폴더에 존재 하는 기존 파일 삭제 로직 들어오는지 체킹.");
            log.info(files.toString());

            for(int i = 0 ; i < files.size(); i++)
            {
                File file = new File(path + "\\" + files.get(i));
                if(file.exists())
                {
                    if(file.delete())
                    {
                        log.info(i + "번째파일삭제");
                    }
                    else {
                        log.info(i + "번째파일삭제 실패요...@@@@@@@@@@@@@@@@@@@@@@@");
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }




}
