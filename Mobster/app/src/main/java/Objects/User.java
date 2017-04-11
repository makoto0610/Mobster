package Objects;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by makoto on 1/19/17.
 */

public class User {

    private String username;
    private String email;
    private int asked;
    private int answered;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }


    public String getUsername() {
        return username;
    }

    public String getEmail() {return email;}

    public void incrementAsked() {
        this.asked++;
    }

    public void incrementAnswered() {
        this.answered++;
    }

    public int getAsked() {
        return this.asked;
    }

    public int getAnswered() {
        return this.answered;
    }
}
