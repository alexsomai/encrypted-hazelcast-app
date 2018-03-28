package com.alexsomai.blog.controller;


import com.alexsomai.blog.model.User;
import com.alexsomai.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationContext context;

    @RequestMapping(value = "/{accountId}", method = RequestMethod.GET)
    public User getUser(@PathVariable long accountId) {
        return userService.getUser(accountId);
    }

    /**
     * Hackish solution to view the cached items using reflection.
     * Only for demo purposes! Do not use at home :)
     * @return the items cached in Hazelcast
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/cached", method = RequestMethod.GET)
    public Set<Map.Entry> getCachedUsers() {
        CacheManager cacheManager = (CacheManager) context.getBean("cacheManager");
        Cache cache = cacheManager.getCache("users");
        Field field = ReflectionUtils.findField(cache.getClass(), "map");
        ReflectionUtils.makeAccessible(field);
        Map map = (Map) ReflectionUtils.getField(field, cache);
        return map.entrySet();
    }
}
