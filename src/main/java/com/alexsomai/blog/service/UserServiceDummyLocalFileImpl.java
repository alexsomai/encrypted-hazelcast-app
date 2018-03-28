package com.alexsomai.blog.service;

import com.alexsomai.blog.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Optional;

@Service
public class UserServiceDummyLocalFileImpl implements UserService {

    private static final String USERS_FILE = "users.json";
    private static final Gson GSON = new GsonBuilder().create();

    @Override
    @Cacheable("users")
    public User getUser(long accountId) {

        // Exaggerate delay, to simulate making a slow query to DB
        // DO NOT TRY THIS AT HOME! (to be read production) :)
        sleep();

        InputStream fileURI = this.getClass().getClassLoader().getResourceAsStream(USERS_FILE);

        JsonReader reader = new JsonReader(new InputStreamReader(fileURI));
        User[] users = GSON.fromJson(reader, User[].class);
        Optional<User> user = Arrays.stream(users).filter(u -> u.accountId == accountId).findFirst();

        return user.orElse(null);
    }

    private static void sleep() {
        try {
            Thread.sleep((long) 3000);
        } catch (InterruptedException ignore) {
        }
    }
}
