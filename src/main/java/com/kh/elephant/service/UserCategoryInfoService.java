package com.kh.elephant.service;

        import com.kh.elephant.domain.UserCategoryInfo;
        import com.kh.elephant.repo.UserCategoryInfoDAO;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;

        import java.util.List;

@Service
public class UserCategoryInfoService {

    @Autowired
    private UserCategoryInfoDAO dao;

    public List<UserCategoryInfo> showAll() { return dao.findAll(); }

    public UserCategoryInfo show(int code) { return dao.findById(Integer.valueOf(String.valueOf(code))).orElse(null); }

    public UserCategoryInfo create(UserCategoryInfo userCategoryInfo) { return dao.save(userCategoryInfo); }

    public List<UserCategoryInfo> createAll(List<UserCategoryInfo> userCategoryInfo) {
        return dao.saveAll(userCategoryInfo);
    }

    @Transactional
    public UserCategoryInfo update(UserCategoryInfo userCategoryInfo) { return dao.save(userCategoryInfo); }

    public UserCategoryInfo delete(int code) {
        UserCategoryInfo data = dao.findById(code).orElse(null);
        dao.delete(data);
        return data;
    }

    public List<UserCategoryInfo> findByUserId(String id) { return dao.findByUserId(id); }


    @Transactional
    public List<UserCategoryInfo> updateAll(List<UserCategoryInfo> userCategoryInfo) {
        return dao.saveAll(userCategoryInfo);
    }


}
