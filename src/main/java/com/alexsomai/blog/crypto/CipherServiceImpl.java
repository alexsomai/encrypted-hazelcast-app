package com.alexsomai.blog.crypto;

import com.alexsomai.blog.util.SerializationUtil;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

@Component
public class CipherServiceImpl implements CipherService {

    @Value("${cipher.service.secret-key}")
    private String secretKey;

    @Override
    public String hash(Object object) {
        Digest digest = new SHA256Digest();

        final byte[] clearBytes = SerializationUtil.serialize(object);
        digest.update(clearBytes, 0, clearBytes.length);
        final byte[] hashedBytes = new byte[digest.getDigestSize()];
        digest.doFinal(hashedBytes, 0);

        return new String(Hex.encode(hashedBytes));
    }

    @Override
    public String encrypt(Object object) {
        if (object == null) {
            throw new NullPointerException("Cannot encrypt a null value");
        }

        PaddedBufferedBlockCipher bufferedBlockCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));

        KeyParameter keyParameter;
        keyParameter = new KeyParameter(secretKey.getBytes(StandardCharsets.UTF_8));
        bufferedBlockCipher.init(true, keyParameter);

        byte[] bytesToEncrypt = SerializationUtil.serialize(object);
        byte[] encryptedTextBytes = new byte[bufferedBlockCipher.getOutputSize(bytesToEncrypt.length)];

        int outputLength = bufferedBlockCipher.processBytes(bytesToEncrypt, 0, bytesToEncrypt.length, encryptedTextBytes, 0);

        try {
            bufferedBlockCipher.doFinal(encryptedTextBytes, outputLength);
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException("Exception occurred while encrypting the object");
        }

        return new String(Hex.encode(encryptedTextBytes));
    }

    @Override
    public Object decrypt(String data) {
        if (data == null) {
            throw new NullPointerException("Cannot decrypt a null value");
        }

        PaddedBufferedBlockCipher bufferedBlockCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));

        byte[] objectToDecrypt = Hex.decode(data);
        if (objectToDecrypt.length % bufferedBlockCipher.getUnderlyingCipher().getBlockSize() > 0) {
            String message = MessageFormat.format("After decoding the encrypted object from Base64, " +
                "the object should contain a number of bytes multiple of {0} and the size of the object in bytes" +
                " is {1}. Most probably the object has not been encrypted with this library, " +
                "or maybe it has been tampered", bufferedBlockCipher.getUnderlyingCipher().getBlockSize(), objectToDecrypt.length);
            throw new IllegalArgumentException(message);
        }

        KeyParameter keyParameter = new KeyParameter(secretKey.getBytes());
        bufferedBlockCipher.init(false, keyParameter);
        final byte[] originalTextBytes = new byte[bufferedBlockCipher.getOutputSize(objectToDecrypt.length)];
        final int outputLength = bufferedBlockCipher.processBytes(objectToDecrypt, 0, objectToDecrypt.length, originalTextBytes, 0);

        try {
            bufferedBlockCipher.doFinal(originalTextBytes, outputLength);
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException("Exception occurred while decrypting the object");
        }

        return SerializationUtil.deserialize(originalTextBytes);
    }
}
