package com.yhwch.annotation.handler;


import com.yhwch.util.HexUtil;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

/**
 * @author pengweichao
 * @date 2019/7/30
 * 3DES加密方式将字符串加密，并将结果转化为16进制表示的字符串
 */
public class TripleDesCryptoHandler extends CryptoHandler {

    private static final String ALGORITHM = "DESede";
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
            SecretKey desKey = new SecretKeySpec(build3DesKey(secretKey), ALGORITHM);    //生成密钥
            Cipher c1 = Cipher.getInstance(ALGORITHM);    //实例化负责加密/解密的Cipher工具类
            c1.init(Cipher.ENCRYPT_MODE, desKey);    //初始化为加密模式
            return HexUtil.byteArr2HexStr(c1.doFinal(input.getBytes()));
        } catch (java.security.NoSuchAlgorithmException e1) {
           throw new RuntimeException(e1);
        } catch (javax.crypto.NoSuchPaddingException e2) {
            throw new RuntimeException(e2);
        } catch (Exception e3) {
            throw new RuntimeException(e3);
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
            SecretKey desKey = new SecretKeySpec(build3DesKey(secretKey), ALGORITHM);
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            c1.init(Cipher.DECRYPT_MODE, desKey);    //初始化为解密模式
            return new String (c1.doFinal(HexUtil.hexStr2ByteArr(input)));
        } catch (java.security.NoSuchAlgorithmException e1) {
            throw new RuntimeException(e1);
        } catch (javax.crypto.NoSuchPaddingException e2) {
            throw new RuntimeException(e2);
        } catch (Exception e3) {
            throw new RuntimeException(e3);
        }
    }

    /*
     * 根据字符串生成密钥字节数组
     * @param keyStr 密钥字符串
     * @return
     * @throws UnsupportedEncodingException
     */
    public static byte[] build3DesKey(String keyStr) throws UnsupportedEncodingException {
        byte[] key = new byte[24];    //声明一个24位的字节数组，默认里面都是0
        byte[] temp = keyStr.getBytes("UTF-8");    //将字符串转成字节数组

         /*
          * 执行数组拷贝
          * System.arraycopy(源数组，从源数组哪里开始拷贝，目标数组，拷贝多少位)
          */
        if(key.length > temp.length){
            //如果temp不够24位，则拷贝temp数组整个长度的内容到key数组中
            System.arraycopy(temp, 0, key, 0, temp.length);
        }else{
            //如果temp大于24位，则拷贝temp数组24个长度的内容到key数组中
            System.arraycopy(temp, 0, key, 0, key.length);
        }
        return key;
    }

}
