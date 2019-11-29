package com.yhwch.annotation.enums;


import com.yhwch.annotation.handler.Aes256CryptoHandler;
import com.yhwch.annotation.handler.CryptoHandler;
import com.yhwch.annotation.handler.TripleDesCryptoHandler;

/**
 * @author pengweichao
 * @date 2019/7/26
 */
public enum CryptoHandlerEnum {

    AES_256_CRYPTO(new Aes256CryptoHandler()),
    TRIPLE_DES_CRYPTO(new TripleDesCryptoHandler());

    private CryptoHandler cryptoHandler;

    CryptoHandlerEnum(CryptoHandler cryptoHandler) {
        this.cryptoHandler = cryptoHandler;
    }

    public CryptoHandler getCryptoHandler() {
        return cryptoHandler;
    }
}
