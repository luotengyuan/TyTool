package com.lois.tytool.base.exception;

/**
 * @Description 加解密异常
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class EncryptionAndDecryptionException extends RuntimeException {

    public EncryptionAndDecryptionException() { }
    public EncryptionAndDecryptionException(String message) {
        super(message);
    }

    public EncryptionAndDecryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public EncryptionAndDecryptionException(Throwable cause) {
        super(cause);
    }

}
