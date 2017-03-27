package Objects;

/**
 * Created by singh on 3/22/2017.
 */

public class BannedUser {
    private String username;
    private String password;

    public BannedUser(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String userId) {
        username = userId;
    }
}
