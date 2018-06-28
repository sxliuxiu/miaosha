package com.bupt.redis;

public class MiaoShaUserKey extends BasePrefix {

    //设置过期时间是2天
    public static final int TOKEN_EXPIRE = 3600*24*2;

    public MiaoShaUserKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }
    public static MiaoShaUserKey token = new MiaoShaUserKey(TOKEN_EXPIRE,"tk");
}