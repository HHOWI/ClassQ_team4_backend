package com.kh.elephant.service;

import com.kh.elephant.domain.BlockUsers;
import com.kh.elephant.domain.UserInfo;
import com.kh.elephant.repo.BlockUsersDAO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlockUsersService {

    @Autowired
    private BlockUsersDAO dao;
    @Autowired
    private UserInfoService userInfoService;

    //전체 차단 정보
    public List<BlockUsers> showAll() {
        return dao.findAll();
    }

    public BlockUsers show(int code) {
        return dao.findById(code).orElse(null);
    }

    public List<BlockUsers> showBlockUser(String id) {return dao.findByUserId(id);}

    public void deleteBlock(String userId, String blockId) {
        dao.deleteBlock(userId, blockId);
    }

    public BlockUsers create(BlockUsers blockUsers) {
        return dao.save(blockUsers);
    }

    // 유저 차단
    public BlockUsers addBlockUser(String userId, String blockId, String blockReason) {
        BlockUsers blockUsers = BlockUsers.builder()
                .userInfo(userInfoService.show(userId))
                .blockInfo(userInfoService.show(blockId))
                .blockReason(blockReason)
                .build();
        return this.create(blockUsers);
    }

}
