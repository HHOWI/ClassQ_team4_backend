package com.kh.elephant.service;

import com.kh.elephant.domain.UserInfo;
import com.kh.elephant.domain.UserLike;
import com.kh.elephant.domain.UserLikeDTO;
import com.kh.elephant.repo.UserLikeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserLikeService {

    @Autowired
    private UserLikeDAO userLikeDAO;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    NotificationMessageService nmService;

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

    @Transactional
    public void userLikeProcess(UserLikeDTO dto) {
        // 좋아요db에 중복된 데이터가 없는지(좋아요 중복 방지), 누르는 유저와 타겟유저가 다른지(셀프 좋아요 방지)
        if (this.duplicateCheck(dto.getLikeUpUser(), dto.getLikeUpTarget()) == null && !dto.getLikeUpUser().equals(dto.getLikeUpTarget())) {
            UserInfo targetUser = userInfoService.show(dto.getLikeUpTarget());
            UserLike userLike = UserLike.builder()
                    .likeUpUser(userInfoService.show(dto.getLikeUpUser()))
                    .likeUpTarget(targetUser)
                    .build();
            // 좋아요 정보 DB에 추가
            this.create(userLike);
            // 유저 좋아요 정보 DB에서 ID 검색후 카운트만큼 유저DB의 좋아요 수치 업데이트
            targetUser.setPopularity(this.findByTarget(dto.getLikeUpTarget()));
            userInfoService.update(targetUser);
            // 좋아요 받았다는 알림 발송, 알림정보 db저장
            nmService.notifyProcessing(targetUser, "좋아요를 받았습니다.", null, null);

        }

    }

    public UserLike duplicateCheck(String likeUpUser, String likeUpTarget) {
        return userLikeDAO.duplicateCheck(likeUpUser, likeUpTarget);
    }

    public int findByTarget(String likeUpTarget) {
        return userLikeDAO.findByTarget(likeUpTarget);
    }
}
