package com.kh.elephant.service;


import com.kh.elephant.domain.PostAttachments;
import com.kh.elephant.repo.PostAttachmentsDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PostAttachmentsService {

    @Autowired
    private PostAttachmentsDAO dao;
    public List<PostAttachments> findByPostSEQ(int id) {return dao.findByPostSeq(id);}
    public void deleteByPostSeq (int id){
        dao.deleteByPostSeq(id);;
    }
    public List<PostAttachments> showAll() { return dao.findAll(); }

    public PostAttachments show(int code) { return dao.findById(code).orElse(null); }

    public PostAttachments create(PostAttachments postAttachments) {
        return dao.save(postAttachments);
    }


    public PostAttachments update(PostAttachments postAttachments) { return dao.save(postAttachments); }

    public PostAttachments delete(int code) {

        PostAttachments data = dao.findById(code).orElse(null);
        dao.delete(data);
        return data;
    }

}
