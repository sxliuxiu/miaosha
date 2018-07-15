package com.bupt.redis;

public class MiaoShaUserKey extends BasePrefix {

    //设置过期时间是2天
    public static final int TOKEN_EXPIRE = 3600*24*2;

    public MiaoShaUserKey(int expireSeconds,String prefix) {

        super(expireSeconds,prefix);
    }
    public static MiaoShaUserKey token = new MiaoShaUserKey(TOKEN_EXPIRE,"tk");

    //希望对象缓存是永久有效的
    public static MiaoShaUserKey getById = new MiaoShaUserKey(0,"id");


}
