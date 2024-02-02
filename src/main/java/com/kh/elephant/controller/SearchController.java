package com.kh.elephant.controller;

import com.kh.elephant.domain.Post;
import com.kh.elephant.domain.SearchDTO;
import com.kh.elephant.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/qiri/*")
@CrossOrigin(origins = {"*"}, maxAge = 6000)
public class SearchController {

    @Autowired
    private SearchService searchService;

    // 키워드에 따른 게시물 전체 조회
    @GetMapping("public/search")
    public ResponseEntity<List<Post>> searchPost(@RequestParam("keyword") String keyword) {
        Sort sort = Sort.by("postSEQ").descending();
        List<Post> searchResults  = searchService.searchByKeyword(keyword);
        return new ResponseEntity<>(searchResults, HttpStatus.OK);

    }


}
