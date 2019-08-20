package fun.annotation.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pengweichao
 * @date 2019/7/26
 */
public abstract class CryptoHandler {
    private static final Logger log = LoggerFactory.getLogger(CryptoHandler.class);
    /**
     * 加密方法
     * @param input
     * @return
     */
   public abstract String encrypt(String input,String secretKey);

    /**
     * 解密方法
     * @param input
     * @return
     */
    public abstract String decrypt(String input,String secretKey);

    public String crypto(boolean doEncrypt,boolean doDecrypt,String input,boolean strict,String secretKey){
        try {
            if (doEncrypt && !doDecrypt) {
                return encrypt(input,secretKey);
            }
            if (!doEncrypt && doDecrypt) {
                return decrypt(input,secretKey);
            }
            log.error("配置错误无法执行加解密,p=doEncrypt:{},doDecrypt:{},strict:{},input:{}",doEncrypt,doDecrypt,strict,input);
            if(strict) {
                throw new RuntimeException("配置错误无法执行");
            }
        }catch (Exception e){
            log.error("执行出现错误,p=doEncrypt:{},doDecrypt:{},strict:{},input:{},e=",doEncrypt,doDecrypt,strict,input,e);
            if(strict){
                throw new RuntimeException(e);
            }
        }
        return input;
    }
}
