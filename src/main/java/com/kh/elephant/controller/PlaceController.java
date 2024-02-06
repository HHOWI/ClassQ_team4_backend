package com.kh.elephant.controller;

import com.kh.elephant.domain.Place;
import com.kh.elephant.domain.QPlace;
import com.kh.elephant.service.PlaceService;
import com.querydsl.core.BooleanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/qiri/*")
@CrossOrigin(origins = {"*"}, maxAge = 6000)
@Slf4j
public class PlaceController {

    @Autowired
    private PlaceService service;

    @GetMapping("/public/place")
    public ResponseEntity<List<Place>> showAll() {

        try {
            // 가나다 순으로 정렬
            return ResponseEntity.status(HttpStatus.OK).body(service.showAll().stream()
                    .sorted(Comparator.comparing(Place::getPlaceName))
                    .collect(Collectors.toList()));
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/public/place/{id}")
    public ResponseEntity<Place> show(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.show(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

//    @GetMapping("/public/place/{id}")
//    public ResponseEntity<List<Place>> findByPostSeq(@PathVariable int id){
//        return ResponseEntity.status(HttpStatus.OK).body(service.findByPostSEQ(id));
//    }

    @PostMapping("/place")
    public ResponseEntity<Place> create(@RequestBody Place vo) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.create(vo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/place")
    public ResponseEntity<Place> update(@RequestBody Place vo) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.update(vo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/place/{id}")
    public ResponseEntity<Place> delete(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.delete(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
