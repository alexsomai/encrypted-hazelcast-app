package com.alexsomai.blog;

import com.alexsomai.blog.cache.EncryptedHazelcastCacheManager;
import com.alexsomai.blog.crypto.CipherService;
import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
@EnableCaching
public class Application {

    public static final String USERS_CACHE = "users";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance, CipherService cipherService) {
        return new EncryptedHazelcastCacheManager(hazelcastInstance, cipherService);
    }

    @Bean
    public HazelcastInstance hazelcastInstance(Config config) {
        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    public Config config() {
        Config config = new Config();

        JoinConfig joinConfig = config.getNetworkConfig().getJoin();

        // disable multicast config for demo
        joinConfig.getMulticastConfig()
            .setEnabled(false);

        // enable tcp/ip config for demo
        joinConfig.getTcpIpConfig()
            .setMembers(Collections.singletonList("localhost"))
            .setEnabled(true);

        MapConfig usersMapConfig = new MapConfig();

        usersMapConfig.setName(USERS_CACHE)
            .setTimeToLiveSeconds(600)
            .setEvictionPolicy(EvictionPolicy.LFU);

        config.addMapConfig(usersMapConfig);

        return config;
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            System.out.println(("Using cache manager: " + ctx.getBean("cacheManager").getClass().getName()));

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }

        };
    }
}
