package Objects;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by makoto on 2/6/17.
 */

public class Question {
    private String question;
    private LinkedList<Choice> choices;
    private Calendar start;
    private Calendar end;
    private Duration duration;
    private String username;
    private int questionId;
    private Status status;
    private int num_access;

    public enum Status {
        CLOSED, NEW, TRENDING
    }

    public Question(HashMap<String, String> map) {

    }

    public Question(String question, LinkedList<Choice> choices, Calendar start, Calendar end, String username) {
        this.question = question;
        this.choices = choices;
        this.start = start;
        this.end = end;
        this.username = username;
        this.status = Status.NEW;
    }

    public String getQuestion() {
        return question;
    }

    public LinkedList<Choice> getChoices() {
        return choices;
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
}
