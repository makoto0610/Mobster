package Objects;

import android.location.Location;

import org.joda.time.Duration;

import java.util.Calendar;
import java.util.LinkedList;

/**
 * Created by makoto on 2/6/17.
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


    public enum Status {
        CLOSED, NEW, TRENDING
    }

    public Question() {
    }



    public Question(String question, LinkedList<Choice> choices, LinkedList<String> keywords,
                    Calendar start, Calendar end, String username, Location loc, LinkedList<String> comments) {
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

    public LinkedList<String> getComments() {

        return comments;
    }

    public void setNum_favorites(long num_favorites) {
        this.num_favorites = num_favorites;
    }

    public long getNum_favorites() {

        return num_favorites;
    }


    public String getQuestion() {
        return question;
    }

    public LinkedList<Choice> getChoices() {
        return choices;
    }

    public LinkedList<String> getKeywords() {
        return keywords;
    }

    public Calendar getStart() {
        return start;
    }

    public Calendar getEnd() {
        return end;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getUsername() {
        return this.username;
    }

    public int getQuestionId() {
        return this.questionId;
    }

    public String getStatus() {
        return this.status.toString();
    }

    public int getNum_access() {
        return this.num_access;
    }

    public void incrementAccess() {
        this.num_access++;
    }

    public void setIsFlagged(int isFlagged) {
        this.isFlagged = isFlagged;
    }

    public int getIsFlagged() {
        return this.isFlagged;
    }

    public Location getLoc() {
        return loc;
    }

    public void addVotedUsers(String username) {
        this.votedUsers.add(username);
    }

    public LinkedList<String> getVotedUsers() {
        return this.votedUsers;
    }
}
