package com.bupt.vo;

import com.bupt.validator.IsMobile;

import javax.validation.constraints.NotNull;

/**
 * 用来接收前端登录页面传过来的参数
 * */
public class LoginVo {

    /**
     * 使用标签对参数进行校验
     * */
    @NotNull
    @IsMobile
    private String mobile;
    @NotNull
    private String password;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginVo{" +
                "mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
