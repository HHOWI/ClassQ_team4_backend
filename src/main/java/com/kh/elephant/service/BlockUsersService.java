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

    //전체 차단 정보
    public List<BlockUsers> showAll() {
        return dao.findAll();
    }

    public BlockUsers show(int code) {
        return dao.findById(code).orElse(null);
    }

    public List<BlockUsers> showBlockUser(String id) {return dao.findByUserId(id);}

    @Transactional
    public void updateBlockUser(String id) {
        dao.updateByUnblock(id);
    }

    @Transactional
    public void deleteBlockUser(String id) {
        dao.deleteByUnblock(id);
    }


    public BlockUsers create(String id, String blockId) {
        if (id.equals(blockId)) {
            throw new IllegalArgumentException("자신을 차단할 수 없습니다.");
        }
        BlockUsers blockUsers = new BlockUsers();

        // UserInfo 및 BlockInfo 초기화
        blockUsers.setUserInfo(new UserInfo());
        blockUsers.setBlockInfo(new UserInfo());

        blockUsers.getUserInfo().setUserId(id);
        blockUsers.getBlockInfo().setUserId(blockId);

        // 기타 필요한 값을 설정

        return dao.save(blockUsers);
    }

}
