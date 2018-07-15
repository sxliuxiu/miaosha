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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoShaUserService {
    /**
     *对象级缓存
     * 首先到缓存中进行查找，如果找不到则到数据库中进行对象查找，并将查找到的数据加载到缓存中，方便下次查找。
     * */

    public static final String COOKI_NAME_TOKEN = "token";


    @Autowired
    MiaoShaUserDao miaoShaUserDao;

    @Autowired
    RedisService redisService;

    public MiaoShaUser getById(long id) {
        //取缓存
        MiaoShaUser user = redisService.get(MiaoShaUserKey.getById, ""+id, MiaoShaUser.class);
        if(user != null) {
            return user;
        }
        //取数据库
        user = miaoShaUserDao.getById(id);
        if(user != null) {
            redisService.set(MiaoShaUserKey.getById, ""+id, user);
        }
        return user;
    }
    // http://blog.csdn.net/tTU1EvLDeLFq5btqiK/article/details/78693323
    public boolean updatePassword(String token, long id, String formPass) {
        //取user
        MiaoShaUser user = getById(id);
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        /**
         * 创建一个新对象记录要修改的字段，然后在数据库中直接更新修改的字段。
         * 修改那个字段就更新那个字段，这样比直接更新全量更简单，使用较少的sql就可以实现
         * */
        //更新数据库
        MiaoShaUser toBeUpdate = new MiaoShaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
        miaoShaUserDao.update(toBeUpdate);
        /**
         * 处理缓存
         * 与id相关的user对象的所有缓存都需要进行更新
         * 更新password后，对象缓存需要更新，用户token缓存也需要更新
         * */
        redisService.del(MiaoShaUserKey.getById,""+id);
        user.setPassword(toBeUpdate.getPassword());
        /**
         * token不能直接删除，删除后不能进行登录，更好的是对token进行更新
         * 对user中的属性进行更新，然后对token进行更新
         */
        redisService.set(MiaoShaUserKey.token, token, user);
        return true;
    }


    /**
     * 首先要对参数进行校验，避免空指针异常
     * */
    public MiaoShaUser getByToken(HttpServletResponse response, String token) {
        if(StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoShaUser user = redisService.get(MiaoShaUserKey.token, token, MiaoShaUser.class);
        /**
         * 对每一次重新登录的用户延长有效期
         * 思路就是对合法用户重新写一下cookie,重置cookie的有效期
         * */
        //延长有效期
        if(user != null) {
            addCookie(response, token, user);
        }
        return user;
    }

    /**
     * 对于正常的业务系统，方法中不应该返回CodeMsg
     * 而是应该讲这些异常直接抛出，由异常处理器进行处理
     * 业务系统中返回true或者false
     * */
    public String login(HttpServletResponse response, LoginVo loginVo) {
        if(loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();
        //判断手机号是否存在
        MiaoShaUser user = getById(Long.parseLong(mobile));
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if(!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        /**
         * 登录成功之后给用户生成一个类似于sessionId的东西token来标识用户，
         写到cookie中传递给客户端，客户端在以后的访问中都在cookie中上传这个token，
         服务端拿到这个token后，根据这个token取到session信息。
         */
        /**
         * 登陆成功后要生成一个cookie，将token放在这可以避免每次都生成一个token
         * 对于已经存在的token直接使用老的token就可以
         */
        String token	 = UUIDUtil.uuid();
        addCookie(response, token, user);
        return token;
    }

    //生成cookie
    private void addCookie(HttpServletResponse response, String token, MiaoShaUser user) {
        redisService.set(MiaoShaUserKey.token, token, user);
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoShaUserKey.token.expireSeconds());
        cookie.setPath("/");
        /**
         *为了将写好的cookie放入response中，传到客户端，需要在函数中添加入参数response。
         * 原来的参数只有一个接收前端传来数据的参数。
         */
        response.addCookie(cookie);
    }
}
