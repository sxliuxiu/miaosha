package com.bupt.controller;

import com.bupt.redis.RedisService;
import com.bupt.result.Result;
import com.bupt.service.MiaoShaUserService;
import com.bupt.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    MiaoShaUserService miaoShaUserService;
    @Autowired
    RedisService redisService;

    @RequestMapping("/to_login")
    public String toLogin(Model model){

        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    /**
     * 使用jsr进行参数校验，在要进行校验的参数前面加上Valid标签
     * 然后在相应的属性上边加上合适的校验项即可
     * 这样下边的参数校验就不需要
     *
     * 经过改造后的代码就是业务中出现异常都向外抛出，在业务代码中不必出现对异常处理的代码
     * 最后的结果直接返回true或者false
     * */
    public Result<Boolean> doLogin(HttpServletResponse response,@Valid LoginVo loginVo){
        log.info(loginVo.toString());

        //参数校验
        /*String passInput = loginVo.getPassword();
        String mobile = loginVo.getMobile();
        if (StringUtils.isEmpty(passInput)){
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        }
        if (StringUtils.isEmpty(mobile)){
            return Result.error(CodeMsg.MOBILE_EMPTY);
        }
        //判断手机号的格式
        if (!ValidatorUtil.isMobile(mobile)){
            return Result.error(CodeMsg.MOBILE_ERROR);
        }*/
        //参数检验成功之后进行登录
        miaoShaUserService.login(response,loginVo);
        //因为现在所有的异常都进行抛出单独处理，所以下边的判断已经没用
        /*//对结果中的代码进行判断
        if (cm.getCode() == 0){
            return Result.success(true);
        }else {
            return Result.error(cm);
        }*/
        return Result.success(true);
    }
}
