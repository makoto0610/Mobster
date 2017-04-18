package Objects;

/**
 * Comment object for firebase database storage.
 *
 * @author Ani
 * @version 1.0
 */
public class Comment {
    private String comment;

    /**
     * Constructor for the comment
     *
     * @param comment content of the comment
     */
    public Comment(String comment) {
        this.comment = comment;
    }

    /**
     * Standard getter method for comment
     *
     * @return the comment in string
     */
    public String getComment() {
        return comment;
    }
}