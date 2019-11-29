package com.yhwch.annotation;

import com.yhwch.annotation.enums.CryptoHandlerEnum;

import java.lang.annotation.*;

/**
 * @author pengweichao
 * @date 2019/7/26
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Crypto {

    String cryptList(); /*需要加密或解密的属性列表 多个用 "," 号隔开*/

    String secretKey();/* 秘钥 */

    CryptoHandlerEnum handler() default CryptoHandlerEnum.AES_256_CRYPTO; /* 加解密处理方法 默认为AES256 */

    boolean encrypt() default false; /* 是否执行加密 默认不执行加密 加密或解密只能执行一种*/

    boolean decrypt() default false; /* 是否执行解密 默认不执行解密 加密或解密只能执行一种*/

    boolean strict() default false; /* 是否使用严格模式 默认不使用 严格模式下加解密异常直接抛出，非严格模式加密失败使用原字符串，解密失败*/
}
