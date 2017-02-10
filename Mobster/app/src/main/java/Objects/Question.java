package Objects;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.Calendar;
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

    public Question(String question, LinkedList<Choice> choices, Calendar start, Calendar end, String username) {
        this.question = question;
        this.choices = choices;
        this.start = start;
        this.end = end;
        this.username = username;
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
}
