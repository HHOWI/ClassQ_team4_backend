package com.kh.elephant.controller;


import com.kh.elephant.domain.PlaceType;
import com.kh.elephant.service.PlaceTypeService;
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
public class PlaceTypeController {

    @Autowired
    private PlaceTypeService service;

    @GetMapping("/public/placeType")
    public ResponseEntity<List<PlaceType>> showAll() {
        try {
            // 가나다 순으로 정렬하여 반환
            List<PlaceType> placeTypes = service.showAll().stream()
                    .filter(pt -> !pt.getPlaceTypeName().equals("없음"))
                    .sorted((pt1, pt2) -> {
                        if (pt1.getPlaceTypeName().equals("기타")) return 1;
                        if (pt2.getPlaceTypeName().equals("기타")) return -1;
                        return pt1.getPlaceTypeName().compareTo(pt2.getPlaceTypeName());
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.OK).body(placeTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/placeType/{id}")
    public ResponseEntity<PlaceType> show(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.show(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/placeType")
    public ResponseEntity<PlaceType> create(@RequestBody PlaceType placeType) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.create(placeType));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<PlaceType> update(@RequestBody PlaceType placeType) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.update(placeType));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<PlaceType> delete(@PathVariable int id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(service.delete(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
