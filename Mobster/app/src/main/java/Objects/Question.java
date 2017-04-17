package Objects;

import android.location.Location;

import org.joda.time.Duration;

import java.util.Calendar;
import java.util.LinkedList;

/**
 * Question objects for firebase storage
 *
 * @author makoto
 * @version 1.0
 */
public class Question {
    private String question;
    private LinkedList<Choice> choices;
    private LinkedList<String> keywords;
    private Calendar start;
    private Calendar end;
    private Duration duration;
    private String username;
    private int questionId;
    private int isFlagged;
    private Status status;
    private int num_access;
    private long num_favorites;

    private LinkedList<String> votedUsers;

    private Location loc;

    private LinkedList<String> comments;

    /**
     * public enum representing Status of questions
     */
    public enum Status {
        CLOSED, NEW, TRENDING
    }

    /**
     * Default non-arg constructor for question object
     */
    public Question() {
    }

    /**
     * Constructor for question object
     *
     * @param question content of the question
     * @param choices list of choices associated with question
     * @param keywords keywords associated with question
     * @param start start time of the question
     * @param end end time of the question
     * @param username username associated with the question (person posted the question)
     * @param loc location where the question is posted
     * @param comments comments made for the question
     */
    public Question(String question, LinkedList<Choice> choices, LinkedList<String> keywords,
                    Calendar start, Calendar end, String username, Location loc,
                    LinkedList<String> comments) {
        this.question = question;
        this.choices = choices;
        this.keywords = keywords;
        this.start = start;
        this.end = end;
        this.username = username;
        this.status = Status.NEW;
        this.loc = loc;
        isFlagged = 0;
        this.num_favorites = 0;

        this.comments = comments;
        this.votedUsers = new LinkedList<>();
    }

    /**
     * Getter method for list of comments
     *
     * @return list of comments
     */
    public LinkedList<String> getComments() {
        return comments;
    }

    /**
     * Setter method for number of people who favorited the question
     *
     * @param num_favorites number of people who favorited the question
     */
    public void setNum_favorites(long num_favorites) {
        this.num_favorites = num_favorites;
    }

    /**
     * Getter method for number of people who favorited the question
     *
     * @return number of people who favorited the question
     */
    public long getNum_favorites() {
        return num_favorites;
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
     * Get list of choices
     *
     * @return list of choices
     */
    public LinkedList<Choice> getChoices() {
        return choices;
    }

    /**
     * Get list of keywords
     *
     * @return list of keywords
     */
    public LinkedList<String> getKeywords() {
        return keywords;
    }

    /**
     * Get start time of the question
     *
     * @return start time of the question
     */
    public Calendar getStart() {
        return start;
    }

    /**
     * Get end time of the question
     *
     * @return end time of the question
     */
    public Calendar getEnd() {
        return end;
    }

    /**
     * Get the duration of the question
     *
     * @return duration of the question
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * Set the duration of the question
     *
     * @param duration duration of the question
     */
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    /**
     * Get the username of the person who posted the question
     *
     * @return username of the person who posted the question
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Get the question identification number
     *
     * @return question identification number
     */
    public int getQuestionId() {
        return this.questionId;
    }

    /**
     * Get the status of the question
     *
     * @return status of the question
     */
    public String getStatus() {
        return this.status.toString();
    }

    /**
     * Get the number of access for the question
     *
     * @return number of access for the question
     */
    public int getNum_access() {
        return this.num_access;
    }

    /**
     * Increment the number of accesses by one
     */
    public void incrementAccess() {
        this.num_access++;
    }

    /**
     * Set the flagged variable
     *
     * @param isFlagged boolean value for isFlagged
     */
    public void setIsFlagged(int isFlagged) {
        this.isFlagged = isFlagged;
    }

    /**
     * Get whether the question is flagged
     *
     * @return whether the question is flagged or not
     */
    public int getIsFlagged() {
        return this.isFlagged;
    }

    /**
     * Increment the number of flags by one
     */
    public void incrementFlagged() {
        this.isFlagged++;
    }

    /**
     * Get location where the question is posted
     *
     * @return location where the question is posted
     */
    public Location getLoc() {
        return loc;
    }

    /**
     * Add the voted user
     *
     * @param username username of the person who voted
     */
    public void addVotedUsers(String username) {
        this.votedUsers.add(username);
    }

    /**
     * Get list of voted users
     *
     * @return list of voted users
     */
    public LinkedList<String> getVotedUsers() {
        return this.votedUsers;
    }
}
