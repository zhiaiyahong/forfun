package fun.annotation.enums;

import fun.annotation.handler.Aes256CryptoHandler;
import fun.annotation.handler.CryptoHandler;
import fun.annotation.handler.TripleDesCryptoHandler;

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
