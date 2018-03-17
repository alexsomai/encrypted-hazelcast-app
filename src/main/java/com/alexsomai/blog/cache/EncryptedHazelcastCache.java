package com.alexsomai.blog.cache;

import com.alexsomai.blog.crypto.CipherService;
import com.hazelcast.core.IMap;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.spring.cache.HazelcastCache;

import java.io.Serializable;

public class EncryptedHazelcastCache extends HazelcastCache {

    static final DataSerializable NULL = new NullDataSerializable();

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
    protected Object fromStoreValue(Object value) {
        final Object decryptedObject = cipherService.decrypt((String) value);
        if (NULL.equals(decryptedObject)) {
            return null;
        } else {
            return decryptedObject;
        }
    }

    @Override
    protected Object toStoreValue(Object value) {
        final Object encryptedObject;
        if (value == null) {
            encryptedObject = cipherService.encrypt(NULL);
        } else {
            encryptedObject = cipherService.encrypt(value);
        }
        return encryptedObject;
    }

    static final class NullDataSerializable implements DataSerializable, Serializable {

        private static final long serialVersionUID = -235268091591769944L;

        public void writeData(final ObjectDataOutput out) {
        }

        public void readData(final ObjectDataInput in) {
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && obj.getClass() == getClass();
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }
}
