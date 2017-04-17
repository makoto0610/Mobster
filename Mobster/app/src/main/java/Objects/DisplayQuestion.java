package Objects;

import org.joda.time.Duration;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Display question object for displaying questions in list view.
 *
 * @author Ani
 * @version 1.0
 */
public class DisplayQuestion {
    private String question;
    private Duration duration;
    private long rating;
    private String questionId; //same as key
    private long num_access;
    private String username;
    private LinkedList<String> votedUsers;
    private LinkedList<String> favoritedUsers;

    /**
     * Constructor for display questions
     *
     * @param question the content of the question
     * @param questionKey question key in the database storage
     * @param duration duration of the question
     * @param rating rating of the question
     * @param questionId question identification number
     * @param num_access number of access for the question
     * @param username username of the person posted the question
     * @param votedUsers list of users who voted for the question
     * @param favoritedUsers list of users who favorited the question
     */
    public DisplayQuestion(String question, String questionKey, Duration duration, long rating, String questionId,
                           long num_access, String username, HashMap votedUsers, HashMap favoritedUsers) {
        this.question = question;
        this.duration = duration;
        this.rating = rating;
        this.questionId = questionId;
        this.num_access = num_access;
        this.username = username;
        this.votedUsers = new LinkedList<>();
        this.favoritedUsers = new LinkedList<>();
        if (votedUsers != null) {
            for (Object key : votedUsers.keySet()) {
                this.votedUsers.add((String) votedUsers.get(key));
            }
        }
        if (favoritedUsers != null) {
            for (Object key: favoritedUsers.keySet()) {
                this.favoritedUsers.add((String) favoritedUsers.get(key));
            }
        }
    }

    /**
     * Get list of users who favorited the question
     *
     * @return list of users
     */
    public LinkedList<String> getFavoritedUsers() {
        return favoritedUsers;
    }

    /**
     * Get question identification number
     *
     * @return question identification number
     */
    public String getQuestionId() {
        return questionId;
    }

    /**
     * Get the content of the question
     *
     * @return content of the question
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Get the duration for the question
     *
     * @return duration for the question
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Get the rating for the question
     *
     * @return rating for the question
     */
    public long getRating() {
        return rating;
    }

    /**
     * Set the content of the question
     *
     * @param question content of the question
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * Set the rating for the question
     *
     * @param rating rating to set to
     */
    public void setRating(long rating) {
        this.rating = rating;
    }

    /**
     * Set the duration for the question
     *
     * @param duration duration to set to
     */
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    /**
     * Getter method for number of accesses
     *
     * @return number of accesses
     */
    public long getNum_access() {
        return num_access;
    }

    /**
     * Getter method for username
     *
     * @return username associated with question
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter method for username
     *
     * @param username new username associated with question
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter method for list of voted users
     *
     * @return list of voted users
     */
    public LinkedList<String> getVotedUsers() {
        return this.votedUsers;
    }
}
