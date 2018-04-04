package com.alexsomai.blog.cache;

import com.alexsomai.blog.crypto.CipherService;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.cache.Cache;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class EncryptedHazelcastCacheManager extends HazelcastCacheManager {

    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<>();
    private HazelcastInstance hazelcastInstance;
    private CipherService cipherService;

    public EncryptedHazelcastCacheManager(HazelcastInstance hazelcastInstance, CipherService cipherService) {
        this.hazelcastInstance = hazelcastInstance;
        this.cipherService = cipherService;
    }

    @Override
    public Cache getCache(String name) {
        Cache cache = caches.get(name);
        if (cache == null) {
            final IMap<Object, Object> map = hazelcastInstance.getMap(name);
            cache = new EncryptedHazelcastCache(map, cipherService);
            long cacheTimeout = calculateCacheReadTimeout(name);
            ((EncryptedHazelcastCache) cache).setReadTimeout(cacheTimeout);
            final Cache currentCache = caches.putIfAbsent(name, cache);
            if (currentCache != null) {
                cache = currentCache;
            }
        }
        return cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        Set<String> cacheNames = new HashSet<>();
        final Collection<DistributedObject> distributedObjects = hazelcastInstance.getDistributedObjects();
        for (DistributedObject distributedObject : distributedObjects) {
            if (distributedObject instanceof IMap) {
                final IMap<?, ?> map = (IMap) distributedObject;
                cacheNames.add(map.getName());
            }
        }
        return cacheNames;
    }

    private long calculateCacheReadTimeout(String name) {
        Long timeout = getReadTimeoutMap().get(name);
        return timeout == null ? getDefaultReadTimeout() : timeout;
    }
}
