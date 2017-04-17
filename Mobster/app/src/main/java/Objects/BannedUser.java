package Objects;

/**
 * Banned User object for firebase database storage.
 *
 * @author Esha
 * @version 1.0
 */
public class BannedUser {
    private String username;
    private String password;

    /**
     * Constructor for the BannedUser objects
     *
     * @param username username of the banned user
     */
    public BannedUser(String username) {
        this.username = username;
    }

    /**
     * Getter method for the username
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter method for the username
     *
     * @param userId username
     */
    public void setUsername(String userId) {
        username = userId;
    }
}
