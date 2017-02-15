package Objects;

import org.joda.time.Duration;

/**
 * Created by anireddy on 2/14/17.
 */

public class DisplayQuestion {
    private String question;
    private Duration duration;
    private long rating;
    private String questionId;

    public DisplayQuestion(String question, Duration duration, long rating, String questionId) {
        this.question = question;
        this.duration = duration;
        this.rating = rating;
        this.questionId = questionId;
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
}
