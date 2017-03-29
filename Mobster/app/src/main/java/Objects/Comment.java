package Objects;

/**
 * Created by anireddy on 3/29/17.
 */

public class Comment {
    private String comment;
    private String user;

    public Comment(String comment, String user) {
        this.comment = comment;
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public String getUser() {
        return user;
    }


}
