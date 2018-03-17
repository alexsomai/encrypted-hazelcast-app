package com.alexsomai.blog;

import com.alexsomai.blog.cache.EncryptedHazelcastCacheManager;
import com.alexsomai.blog.crypto.CipherService;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
@EnableCaching
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance, CipherService cipherService) {
        return new EncryptedHazelcastCacheManager(hazelcastInstance, cipherService);
    }

    @Bean
    public Config hazelcastConfig() {
        return new Config();
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
