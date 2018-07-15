package com.bupt.config;

import com.bupt.acces.UserContext;
import com.bupt.domain.MiaoShaUser;
import com.bupt.service.MiaoShaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 实现相应的UserArgumentResolver
 * 因为下边需要注入service，所以本身也需要框架管理起来
 * 用service标签进行标记
 * */
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver{

    @Autowired
    MiaoShaUserService miaoShaUserService;
    /**
     * 获取参数的类型，表明支持的参数类型
     * 判断参数类型是否是MiaoShaUser，只对这种参数进行相关的处理
     * */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz == MiaoShaUser.class;
    }

    /**
     * 这里边存放的就是对相应类型进行的相关处理
     * */
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
       /* HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);

        String paramToken = request.getParameter(MiaoShaUserService.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request,MiaoShaUserService.COOKIE_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken)){
            return null;
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;

        return miaoShaUserService.getByToken(response,token);*/
        /**
         * 因为现在将当前用户存在了ThreadLocal中，
         * 所以可以直接从ThreadLocal中获取当前用户
         * 因为前边的拦截器先执行，而这个参数解析后执行
         * 所以可以这样进行操作从后边的参数解析中获取用户
         */
        return UserContext.getUser();
    }
    /**
     * 因为需要对cookie中的值进行判断，所以单独写一个方法进行处理
     * */
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
