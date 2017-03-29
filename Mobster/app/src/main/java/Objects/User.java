package Objects;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by makoto on 1/19/17.
 */

public class User {

    public static final String DUMMY_EMAIL = "@mobster.com";

    private String username;
    private String password;
    private String email;
    private int asked;
    private int answered;
    private List<String> votedQuestions;
    private List<String> viewedQuestions;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;

        //email is not entered, so add a dummy email
        if (email.trim().equals("")) {
            this.email = username + DUMMY_EMAIL;
        } else {
            this.email = email;
        }
        this.votedQuestions = new LinkedList<>();
        this.viewedQuestions = new LinkedList<>();
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void incrementAsked() {
        this.asked++;
    }

    public void incrementAnswered() {
        this.answered++;
    }

    public int getAsked() {
        return this.asked;
    }

    public int getAnswered() {
        return this.answered;
    }

    public void addVoted(String id) {
        this.votedQuestions.add(id);
    }

    public List<String> getVotedQuestions() {
        return this.votedQuestions;
    }

    public void addViewedQuestions(String id) {
        this.viewedQuestions.add(id);
    }

    public List<String> getViewedQuestions() {
        return this.viewedQuestions;
    }
}

