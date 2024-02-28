package com.kh.elephant.service;

import com.kh.elephant.domain.MatchingUserInfo;
import com.kh.elephant.domain.QBlockUsers;
import com.kh.elephant.domain.QMatchingUserInfo;
import com.kh.elephant.repo.MatchingUserInfoDAO;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchingUserInfoService {

    @Autowired
    private MatchingUserInfoDAO dao;

    public List<MatchingUserInfo> showAll(Predicate predicate) {
        return (List<MatchingUserInfo>) dao.findAll(predicate);
    }

    public MatchingUserInfo show(int code) { return dao.findById(code).orElse(null); }

    public MatchingUserInfo create(MatchingUserInfo matchingUserInfo) { return dao.save(matchingUserInfo); }

    public MatchingUserInfo update(MatchingUserInfo matchingUserInfo) { return dao.save(matchingUserInfo); }

    public MatchingUserInfo delete(int code) {
        MatchingUserInfo data = dao.findById(code).orElse(null);
        dao.delete(data);
        return data;
    }

    // 수락정보 포스트 공용SEQ 체킹용
    public Optional<MatchingUserInfo> findByUserIdAndPostSEQ(String userId, int postSEQ) {
        return dao.findByUserInfo_UserIdAndPost_PostSEQ(userId, postSEQ);
    }

    // 매칭 정보 postSEQ확인
    public List<MatchingUserInfo> findMatchingByPostSEQ(String id, int postSEQ) {
        QBlockUsers qBlockUsers = QBlockUsers.blockUsers;
        QMatchingUserInfo qMatchingUserInfo = QMatchingUserInfo.matchingUserInfo;

        //차단한 사용자는 거르고 받기
        Predicate predicate = qMatchingUserInfo.post.postSEQ.eq(postSEQ)
                .and(qMatchingUserInfo.userInfo.userId.notIn(
                        JPAExpressions.select(qBlockUsers.blockInfo.userId)
                                .from(qBlockUsers)
                                .where(qBlockUsers.userInfo.userId.eq(id))
                ).and(qMatchingUserInfo.userInfo.userId.notIn(id)));
        
        return showAll(predicate).stream().sorted(Comparator.comparing(MatchingUserInfo::getApplicationDate).reversed()).collect(Collectors.toList());
    }

    public int matchingAccept(int code, String id) {
      return dao.matchingAccept(code, id);
    }


    public int hideMachingUser(int code, String id) {
        return dao.hideMachingUser(code, id);
    }


    public List<MatchingUserInfo> findAccept(int code) {
        return dao.findAccept(code);
    }

    public List<MatchingUserInfo> findByUserIdForPostReview(String id) { return dao.findByUserIdForPostReview(id);}

    @Transactional
    public int postReview(int postSEQ) {
        return dao.postReview(postSEQ);
    }

    public MatchingUserInfo findMuiByPostSEQAndUserId(int postSEQ, String userId) {
        return dao.findMuiByPostSEQAndUserId(postSEQ, userId);
    }
}
