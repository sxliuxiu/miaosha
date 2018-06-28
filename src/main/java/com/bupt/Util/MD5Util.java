package com.bupt.Util;


import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    //对明文字符串做MD5,调用Apache codec中提供的方法
    public static String md5(String src){
       return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";

    //将表单上的密码进行加密
    public static String inputPassFormPass(String inputPass){
        String str = ""+salt.charAt(0)+salt.charAt(2)+inputPass+
                salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    //将表单上已经加密的密码在传输到数据库的时候在进行加密
    public static String formPassToDBPass(String formPass,String salt){
        String str = ""+salt.charAt(0)+salt.charAt(2)+formPass+
                salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    //这个方法是将输入的密码直接转成数据库中要存放的密码
    public static String inputPassToDBPass(String input,String saltDB){
        String formPass = inputPassFormPass(input);
        String dbPass = formPassToDBPass(formPass,saltDB);
        return dbPass;
    }
    public static void main(String[] args){
       System.out.println(inputPassToDBPass("123456",salt));

    }
}
