package blog.alexsomai.com.service;

import blog.alexsomai.com.model.UserInfo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceDummyImpl implements UserInfoService {

    @Override
    @Cacheable("users")
    public UserInfo getUserInfo(long accountId) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new UserInfo(1, "abc", "abc", "a", "b", "c");
    }
}
