package com.bupt.acces;

import com.bupt.domain.MiaoShaUser;

/**
 * 将当前用户进行保存
 * */
public class UserContext {
    private static ThreadLocal<MiaoShaUser> userHolder = new ThreadLocal<>();

    public static MiaoShaUser getUser() {
        return userHolder.get();
    }

    public static void setUser(MiaoShaUser user) {
        userHolder.set(user);
    }
}
