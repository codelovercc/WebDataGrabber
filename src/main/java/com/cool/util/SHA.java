package com.cool.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by codelover on 17/3/23.
 * SHA签名类
 */
public class SHA {

    /**
     * 对字符串加密,加密算法使用MD5,SHA-1,SHA-256,默认使用SHA-256
     *
     * @param strSrc
     *            要加密的字符串
     * @param encName
     *            加密类型 为空默认使用SHA-256
     * @return
     */
    public static String SHA(String strSrc, String encName) {
        MessageDigest md = null;
        String strDes = null;

        byte[] bt = strSrc.getBytes();
        return SHA(bt,encName);
    }
    public static String SHA256(String str){
        return SHA(str,"SHA-256");
    }

    public static String SHA(byte[] bytes, String encName){
        MessageDigest md = null;
        String strDes = null;
        try {
            if (encName == null || encName.equals("")) {
                encName = "SHA-256";
            }
            md = MessageDigest.getInstance(encName);
            md.update(bytes);
            strDes = StringUtil.bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }
}
