package com.alexsomai.blog.crypto;

public interface CipherService {

    String encrypt(Object objectToEncrypt);

    Object decrypt(String objectToDecrypt);

    String hash(Object object);
}
