package com.kh.elephant.service;

import com.kh.elephant.domain.UserLike;
import com.kh.elephant.repo.UserLikeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserLikeService {

    @Autowired
    private UserLikeDAO userLikeDAO;

    public List<UserLike> showAll() {
        return userLikeDAO.findAll();
    }
    public UserLike show(int code) {
        return userLikeDAO.findById(code).orElse(null);
    }

    public UserLike create(UserLike userLike){
        return userLikeDAO.save(userLike);
    }
    public UserLike update(UserLike userLike) {
        UserLike target = userLikeDAO.findById(userLike.getLikeUpSeq()).orElse(null);
        if(target!=null){
            return userLikeDAO.save(userLike);
        }
        return null;
    }

    public UserLike delete(int id) {
        UserLike target = userLikeDAO.findById(id).orElse(null);
        userLikeDAO.delete(target);
        return target;
    }

    public UserLike duplicateCheck(String likeUpUser, String likeUpTarget) {
        return userLikeDAO.duplicateCheck(likeUpUser, likeUpTarget);
    }

    public int findByTarget(String likeUpTarget) {
        return userLikeDAO.findByTarget(likeUpTarget);
    }
}
