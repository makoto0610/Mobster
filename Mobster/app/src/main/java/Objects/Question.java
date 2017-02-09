package Objects;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.LinkedList;

/**
 * Created by makoto on 2/6/17.
 */

public class Question {
    private String question;
    private LinkedList<Choice> choices;
    private DateTime start;
    private DateTime end;
    private Duration duration;
    private String username;

    public Question(String question, LinkedList<String> choices, DateTime start, DateTime end) {
        this.question = question;
        this.choices = new LinkedList<>();
        for (String s : choices) {
            this.choices.add(new Choice(s));

        }
        this.start = start;
        this.end = end;
    }

    public String getQuestion() {
        return question;
    }

    public LinkedList<Choice> getChoices() {
        return choices;
    }

    public DateTime getStart() {
        return start;
    }

    public DateTime getEnd() {
        return end;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
