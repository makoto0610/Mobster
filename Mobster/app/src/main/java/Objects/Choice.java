package Objects;

/**
 * Created by makoto on 2/6/17.
 */

public class Choice {
    private String option;
    private int vote;

    public Choice(String option) {
        this.option = option;
    }

    public String getOption() {
        return option;
    }

    public int getVote() {
        return vote;
    }

    public void incrementVote() {
        this.vote++;
    }

}
