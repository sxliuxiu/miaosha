package com.bupt.service;

import com.bupt.Exception.GlobalException;
import com.bupt.Util.MD5Util;
import com.bupt.Util.UUIDUtil;
import com.bupt.dao.MiaoShaUserDao;
import com.bupt.domain.MiaoShaUser;
import com.bupt.redis.MiaoShaUserKey;
import com.bupt.redis.RedisService;
import com.bupt.result.CodeMsg;
import com.bupt.vo.LoginVo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoShaUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    MiaoShaUserDao miaoShaUserDao;

    @Autowired
    RedisService redisService;
    public MiaoShaUser getById(long id){

        return miaoShaUserDao.getById(id);
    }

    /**
     * 首先要对参数进行校验，避免空指针异常
     * */
    public MiaoShaUser getByToken(HttpServletResponse response,String token) {
        if (StringUtils.isEmpty(token)){
            return null;
        }
        MiaoShaUser user =  redisService.get(MiaoShaUserKey.token,token,MiaoShaUser.class);
        /**
         * 对每一次重新登录的用户延长有效期
         * 思路就是对合法用户重新写一下cookie,重置cookie的有效期
         * */
        if (user !=  null){
            addCookie(response,token,user);
        }
        return user;

    }

    /**
     * 对于正常的业务系统，方法中不应该返回CodeMsg
     * 而是应该讲这些异常直接抛出，由异常处理器进行处理
     * 业务系统中返回true或者false
     * */
    public boolean login(HttpServletResponse response,LoginVo loginVo) {
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
        /**
         * 登录成功之后给用户生成一个类似于sessionId的东西token来标识用户，
         写到cookie中传递给客户端，客户端在以后的访问中都在cookie中上传这个token，
         服务端拿到这个token后，根据这个token取到session信息。
         */
        //登陆成功后要生成一个cookie
        String token = UUIDUtil.uuid();
        addCookie(response,token,user);

        return true;
    }
    //生成cookie
    private void addCookie(HttpServletResponse response,String token,MiaoShaUser user){


        //将用户信息存储到redis中,使用token就可以查到用户信息
        redisService.set(MiaoShaUserKey.token,token,user);
        //将token存到cookie中
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
        //设置cookie的存活时间和redis中的存活时间一致
        cookie.setMaxAge(MiaoShaUserKey.token.expireSeconds());
        cookie.setPath("/");
        /**
         *为了将写好的cookie放入response中，传到客户端，需要在函数中添加入参数response。
         * 原来的参数只有一个接收前端传来数据的参数。
         */
        response.addCookie(cookie);
    }
}
