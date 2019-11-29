package com.yhwch.annotation.handler;


import com.yhwch.util.HexUtil;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * @author pengweichao
 * @date 2019/7/26
 */
public class Aes256CryptoHandler extends CryptoHandler{

    private final String GENERATOR_TYPE = "AES";
    private final String CHARSET_NAME = "utf-8";
    private final String SECURE_TYPE = "SHA1PRNG";
    /**
     * 加密方法
     *
     * @param input
     * @return
     */
    @Override
    public String encrypt(String input,String secretKey) {
        try {
            if(StringUtils.isEmpty(input)){
                return input;
            }
            KeyGenerator e = KeyGenerator.getInstance(GENERATOR_TYPE);
            SecureRandom secureRandom = SecureRandom.getInstance(SECURE_TYPE);
            secureRandom.setSeed(secretKey.getBytes(CHARSET_NAME));
            e.init(256, secureRandom);
            SecretKey originalKey = e.generateKey();
            byte[] raw = originalKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(raw, GENERATOR_TYPE);
            Cipher cipher = Cipher.getInstance(GENERATOR_TYPE);
            cipher.init(1, key);
            byte[] byteEncode = input.getBytes(CHARSET_NAME);
            byte[] byteAES = cipher.doFinal(byteEncode);
            return HexUtil.byteArr2HexStr(byteAES);
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
    }

    /**
     * 解密方法
     *
     * @param input
     * @return
     */
    @Override
    public String decrypt(String input,String secretKey) {
        try {
            if(StringUtils.isEmpty(input)){
                return input;
            }
            KeyGenerator e = KeyGenerator.getInstance(GENERATOR_TYPE);
            SecureRandom secureRandom = SecureRandom.getInstance(SECURE_TYPE);
            secureRandom.setSeed(secretKey.getBytes(CHARSET_NAME));
            e.init(256, secureRandom);
            SecretKey originalKey = e.generateKey();
            byte[] raw = originalKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(raw, GENERATOR_TYPE);
            Cipher cipher = Cipher.getInstance(GENERATOR_TYPE);
            cipher.init(2, key);
            byte[] byteContent = HexUtil.hexStr2ByteArr(input);
            byte[] byteDecode = cipher.doFinal(byteContent);
            return new String(byteDecode, CHARSET_NAME);
        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
