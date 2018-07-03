package com.bupt.service;

import com.bupt.dao.UserDao;
import com.bupt.domain.MiaoShaUser;
import com.bupt.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public User getById(int id){

        return userDao.getById(id);
    }

    @Transactional//添加事物标签
    public boolean tx() {
        User u1 = new User();
        u1.setId(2);
        u1.setName("2222");
        userDao.insert(u1);

        return true;
    }


}
