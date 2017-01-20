package com.the_great_amoeba.mobster;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by makoto on 1/19/17.
 */

public class User {
    private static AtomicInteger ID_GENERATOR = new AtomicInteger(1);
    private int userID;
    private String username;
    private String password;

    public User(String username, String password) {
        this.userID = ID_GENERATOR.getAndIncrement();
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getUserID() {
        return userID;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
