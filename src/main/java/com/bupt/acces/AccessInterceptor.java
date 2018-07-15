package com.bupt.acces;

import com.alibaba.fastjson.JSON;
import com.bupt.domain.MiaoShaUser;
import com.bupt.redis.AccessKey;
import com.bupt.redis.RedisService;
import com.bupt.result.CodeMsg;
import com.bupt.result.Result;
import com.bupt.service.MiaoShaUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * 定义一个处理AccessLimit标签的拦截器
 * */
@Service
public class AccessInterceptor extends HandlerInterceptorAdapter{

    @Autowired
    MiaoShaUserService miaoShaUserService;

    @Autowired
    RedisService redisService;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod){
            MiaoShaUser user = getUser(request,response);
            //将获取到的用户进行保存，方便后边的参数解析中使用
            UserContext.setUser(user);

            HandlerMethod hm = (HandlerMethod)handler;
            //获取到相应的标签
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null){
                return true;
            }

            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();

            if (needLogin){
                if (user == null){
                    //但是为了将错误信息传递出去，所以创建方法
                    render(response, CodeMsg.SESSION_ERROR);
                    //拦截器中只能使用true或false
                    return false;
                 }
                //如果是需要登录
                key += "_"+user.getId();
            }else {
                //do nothing
            }

            //过期时间是可以变化的，随着标签上需要的时间进行变化。
            AccessKey ak = AccessKey.withExpire(seconds);
            Integer count = redisService.get(ak,key,Integer.class);
            if (count == null){
                //如果没有访问次数，则将访问次数置为1
                redisService.set(ak,key,1);
            }else if (count < maxCount){
                redisService.incr(ak,key);
            }else {
                render(response,CodeMsg.ACCESS_LIMIT_REACHED);
                return false;
            }


        }
        return true;
    }

    private void render(HttpServletResponse response, CodeMsg cm) throws Exception{
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(cm));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    private MiaoShaUser getUser(HttpServletRequest request,HttpServletResponse response){
        String paramToken = request.getParameter(MiaoShaUserService.COOKI_NAME_TOKEN);
        String cookieToken = getCookieValue(request,MiaoShaUserService.COOKI_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken)){
            return null;
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;

        return miaoShaUserService.getByToken(response,token);
    }
    private String getCookieValue(HttpServletRequest request, String cookieNameToken) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieNameToken)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
