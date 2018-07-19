package com.jike.myhouse.common.utils;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

public class HashUtils {
    private static final HashFunction FUNCTION = Hashing.md5();

    //设置盐,防止用户密码被暴力破解
    private static final String SALT = "house";

    public static String encryPasswor(String password){
        HashCode hashCode = FUNCTION.hashString(password+SALT,Charset.forName("UTF-8"));
        return hashCode.toString();
    }

    public static void main(String[] args) {
        String password = null;
        String md5 = HashUtils.encryPasswor(password);
        System.out.println(md5);
    }
}
