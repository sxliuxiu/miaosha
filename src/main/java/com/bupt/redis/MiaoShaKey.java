package com.bupt.redis;

public class MiaoShaKey extends BasePrefix{

    public MiaoShaKey(int expireSeconds, String prefix) {

        super(expireSeconds,prefix);
    }

    public static MiaoShaKey isGoodsOver = new MiaoShaKey(0,"go");
    public static MiaoShaKey getMiaoShaPath = new MiaoShaKey(60,"mp");
    public static MiaoShaKey getMiaoshaVerifyCode = new MiaoShaKey(300,"vc");

}
