package com.bupt.service;

import com.bupt.Exception.GlobalException;
import com.bupt.Util.MD5Util;
import com.bupt.dao.MiaoShaUserDao;
import com.bupt.domain.MiaoShaUser;
import com.bupt.result.CodeMsg;
import com.bupt.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiaoShaUserService {

    @Autowired
    MiaoShaUserDao miaoShaUserDao;

    public MiaoShaUser getById(long id){
        return miaoShaUserDao.getById(id);
    }

    /**
     * 对于正常的业务系统，方法中不应该返回CodeMsg
     * 而是应该讲这些异常直接抛出，由异常处理器进行处理
     * 业务系统中返回true或者false
     * */
    public boolean login(LoginVo loginVo) {
        if (loginVo == null)
            throw new GlobalException(CodeMsg.SERVER_ERROR);

        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在，即用户是否存在
        MiaoShaUser user = getById(Long.parseLong(mobile));

        if (user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass,saltDB);
        if(!calcPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        return true;
    }
}
