package com.alexsomai.blog.service;

import com.alexsomai.blog.model.UserInfo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceDummyImpl implements UserInfoService {

    @Override
    @Cacheable("users")
    public UserInfo getUserInfo(long accountId) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new UserInfo(accountId, "abc", "abc", "a", "b", "c");
    }
}
