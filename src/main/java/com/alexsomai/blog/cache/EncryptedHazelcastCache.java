package com.alexsomai.blog.cache;

import com.alexsomai.blog.crypto.CipherService;
import com.hazelcast.core.IMap;
import com.hazelcast.spring.cache.HazelcastCache;

public class EncryptedHazelcastCache extends HazelcastCache {

    private final CipherService cipherService;

    public EncryptedHazelcastCache(IMap<Object, Object> map, CipherService cipherService) {
        super(map);
        this.cipherService = cipherService;
    }

    @Override
    public ValueWrapper get(Object key) {
        final String hashedKey = cipherService.hash(key);
        return super.get(hashedKey);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        final String hashedKey = cipherService.hash(key);
        return super.get(hashedKey, type);
    }

    @Override
    public void put(Object key, Object value) {
        final String hashedKey = cipherService.hash(key);
        super.put(hashedKey, value);
    }

    @Override
    public void evict(Object key) {
        final String hashedKey = cipherService.hash(key);
        super.evict(hashedKey);
    }

    @Override
    protected Object toStoreValue(Object value) {
        final Object valueToEncrypt = super.toStoreValue(value);
        return cipherService.encrypt(valueToEncrypt);
    }

    @Override
    protected Object fromStoreValue(Object value) {
        final Object decryptedObject = cipherService.decrypt((String) value);
        return super.fromStoreValue(decryptedObject);
    }
}
