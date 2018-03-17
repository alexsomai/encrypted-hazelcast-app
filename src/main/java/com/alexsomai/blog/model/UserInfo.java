package com.alexsomai.blog.model;

import java.io.Serializable;

public class UserInfo implements Serializable {

    public final long accountId;
    public final String username;
    public final String email;
    public final String firstName;
    public final String lastName;
    public final String about;

    public UserInfo(long accountId, String username, String email, String firstName, String lastName, String about) {
        this.accountId = accountId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.about = about;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "accountId=" + accountId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", about='" + about + '\'' +
                '}';
    }
}
