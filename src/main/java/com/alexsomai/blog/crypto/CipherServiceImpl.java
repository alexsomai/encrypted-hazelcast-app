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

@Component
public class CipherServiceImpl implements CipherService {

    @Value("${cipher.service.secret-key}")
    private String secretKey;

    @Override
    public String hash(Object object) {
        Digest digest = new SHA256Digest();

        byte[] clearBytes = SerializationUtil.serialize(object);
        digest.update(clearBytes, 0, clearBytes.length);
        byte[] hashedBytes = new byte[digest.getDigestSize()];
        digest.doFinal(hashedBytes, 0);

        return new String(Hex.encode(hashedBytes));
    }

    @Override
    public String encrypt(Object object) {
        if (object == null) {
            throw new NullPointerException("Cannot encrypt a null value");
        }

        byte[] bytesToEncrypt = SerializationUtil.serialize(object);
        byte[] encryptedTextBytes = applyAlgorithm(bytesToEncrypt, true);

        return new String(Hex.encode(encryptedTextBytes));
    }

    @Override
    public Object decrypt(String data) {
        if (data == null) {
            throw new NullPointerException("Cannot decrypt a null value");
        }

        byte[] bytesToDecrypt = Hex.decode(data);
        byte[] originalTextBytes = applyAlgorithm(bytesToDecrypt, false);

        return SerializationUtil.deserialize(originalTextBytes);
    }

    private byte[] applyAlgorithm(byte[] inputBytes, boolean forEncryption) {
        PaddedBufferedBlockCipher paddedBufferedBlockCipher = initPaddedBufferedBlockCipher(forEncryption);

        byte[] outputBytes = new byte[paddedBufferedBlockCipher.getOutputSize(inputBytes.length)];
        int outputLength = paddedBufferedBlockCipher.processBytes(inputBytes, 0, inputBytes.length, outputBytes, 0);

        try {
            paddedBufferedBlockCipher.doFinal(outputBytes, outputLength);
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException("Exception occurred while encrypting/decrypting the object");
        }

        return outputBytes;
    }

    private PaddedBufferedBlockCipher initPaddedBufferedBlockCipher(boolean forEncryption) {
        PaddedBufferedBlockCipher bufferedBlockCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));

        KeyParameter keyParameter = new KeyParameter(secretKey.getBytes(StandardCharsets.UTF_8));
        bufferedBlockCipher.init(forEncryption, keyParameter);

        return bufferedBlockCipher;
    }
}
