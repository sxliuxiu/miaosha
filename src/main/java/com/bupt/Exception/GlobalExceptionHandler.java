package com.bupt.Exception;

import com.bupt.result.CodeMsg;
import com.bupt.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 定义一个全局异常拦截器
 * advice 类似一个xml
 * */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    //后边的value表示拦截那种异常。现在是获取所有异常
    @ExceptionHandler(value = Exception.class)
    //里边的参数自己进行定义
    public Result<String> exceptionHandler(HttpServletRequest request,Exception e){
        if (e instanceof GlobalException){//对业务系统中抛出来的异常进行处理
            GlobalException ex = (GlobalException)e;
            return Result.error(ex.getCm());
        } else if (e instanceof BindException){//如果是绑定异常
            BindException ex = (BindException)e;
            List<ObjectError> errors = ex.getAllErrors();
            //为了简单只获取错误中的第一个，也可以设计获取所有的
            ObjectError error = errors.get(0);

            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        }else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }

    }
}
