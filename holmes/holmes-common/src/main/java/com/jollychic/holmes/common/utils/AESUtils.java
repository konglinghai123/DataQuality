package com.jollychic.holmes.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Created by WIN7 on 2018/1/5.
 * AESUtils,加密处理
 */

@Slf4j
public class AESUtils {

    public final static String DEFAULT_PASSWORD="123456";

    public static String AES_CBC_Encrypt(String content, String password){
        try{
            SecretKeySpec skeySpec = getKey(password);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(content.getBytes("utf-8"));
            return Base64.getEncoder().encodeToString(encrypted);
        }catch (Exception e) {
            log.error("exception:"+e.toString());
        }
        return null;
    }

    public static String AES_CBC_Decrypt(String content, String password){
        try{
            SecretKeySpec skeySpec = getKey(password);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes("utf-8"));
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = Base64.getDecoder().decode(content);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            return originalString;
        }catch (Exception e) {
            log.error("exception:"+e.toString());
        }
        return null;
    }

    private static SecretKeySpec getKey(String strKey) throws Exception {
        byte[] arrBTmp = strKey.getBytes("utf-8");
        // 创建一个空的16位字节数组（默认值为0）
        byte[] arrB = new byte[16];
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }
        SecretKeySpec skeySpec = new SecretKeySpec(arrB, "AES");
        return skeySpec;
    }

    public static void main(String[] args) {
        String[] contents = new String[] {
                "abcdef",
        };
        String password="123456";

        for(String content : contents) {
            System.out.println("加密前：" + content);
            String encrypted = AES_CBC_Encrypt(content, password);
            System.out.println("加密后：" + encrypted);
            String decrypted = AES_CBC_Decrypt(encrypted, password);
            System.out.println("解密后：" + decrypted);
        }
        System.out.println(AES_CBC_Decrypt("jETumiKsq0CuJpc3KCBC1Tu0qVqIQApNKQV+b7e12NbZ/q9HIs2vETSortw+b68ZMWYQLcvOugeOkSfVGpYYvDqSGgEKAlfWlalJfourd7s=", password));
    }
}
