package com.alexsomai.blog.crypto;

public interface CipherService {

    String hash(Object object);

    String encrypt(Object object);

    Object decrypt(String data);
}
