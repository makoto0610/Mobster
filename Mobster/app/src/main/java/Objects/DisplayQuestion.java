package Objects;

import org.joda.time.Duration;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by anireddy on 2/14/17.
 */

public class DisplayQuestion {
    private String question;
    private Duration duration;
    private long rating;
    private String questionId;
    private long num_access;
    private String username;
    private LinkedList<String> votedUsers;

    public DisplayQuestion(String question, Duration duration, long rating, String questionId,
                           long num_access, String username, HashMap votedUsers) {
        this.question = question;
        this.duration = duration;
        this.rating = rating;
        this.questionId = questionId;
        this.num_access = num_access;
        this.username = username;
        this.votedUsers = new LinkedList<>();
        if (votedUsers != null) {
            for (Object key : votedUsers.keySet()) {
                this.votedUsers.add((String) votedUsers.get(key));
            }
        }
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getQuestion() {
        return question;
    }

    public Duration getDuration() {
        return duration;
    }

    public long getRating() {
        return rating;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public long getNum_access() {
        return num_access;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LinkedList<String> getVotedUsers() {
        return this.votedUsers;
    }
}
