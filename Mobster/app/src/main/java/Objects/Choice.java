package Objects;

/**
 * Choice object for firebase database storage.
 *
 * @author makoto
 * @version 1.0
 */
public class Choice {
    private String option;
    private int vote;

    /**
     * Constructor for the option objects
     *
     * @param option option in string
     */
    public Choice(String option) {
        this.option = option;
    }

    /**
     * Getter method for the option for firebase usage
     * (implicit call from database)
     *
     * @return option in string form
     */
    public String getOption() {
        return option;
    }

    /**
     * Set number of votes for the option
     *
     * @param value value to set the field to
     */
    public void setVote(int value) {
        vote = value;
    }

    /**
     * Getter method for votes for firebase usage
     *
     * @return number of votes
     */
    public int getVote() {
        return vote;
    }

    /**
     * Increment number of vote by one
     */
    public void incrementVote() {
        this.vote++;
    }
}
