package Objects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by makoto on 1/19/17.
 */

public class User {

    public static final String DUMMY_EMAIL = "@mobster.com";

    private String username;
    private String password;
    private String email;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;

        //email is not entered, so add a dummy email
        if (email.trim().equals("")) {
            this.email = username + DUMMY_EMAIL;
        } else {
            this.email = email;
        }
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {return email;}

    public void setPassword(String password) {
        this.password = password;
    }
}
