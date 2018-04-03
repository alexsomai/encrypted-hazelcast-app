package com.alexsomai.blog.crypto;

public interface CipherService {

    String encrypt(Object object);

    Object decrypt(String data);

    String hash(Object object);
}
