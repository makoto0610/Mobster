package Objects;

/**
 * User objects for firebase storage
 *
 * @author makoto
 * @version 1.0
 */
public class User {

    private String username;
    private String email;
    private int asked;
    private int answered;

    /**
     * Constructor for user objects
     *
     * @param username username of the user
     * @param email email of the user
     */
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    /**
     * Get the username of the user
     *
     * @return username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get the email of the user
     *
     * @return email of the user
     */
    public String getEmail() {return email;}

    /**
     * Increment the number of questions asked by the user
     */
    public void incrementAsked() {
        this.asked++;
    }

    /**
     * Increment the number of questions answered by the user
     */
    public void incrementAnswered() {
        this.answered++;
    }

    /**
     * Get number of asked questions
     *
     * @return number of asked questions
     */
    public int getAsked() {
        return this.asked;
    }

    /**
     * Get number of answered questions
     *
     * @return number of answered questions
     */
    public int getAnswered() {
        return this.answered;
    }
}
