package naveropenapi.example.com.aduinoproject.DB;

/**
 * Created by hyunungLim on 2017-12-13.
 */

public class ChatModel {
    private String id;
    private String email;
    private String comment;
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ChatModel() {
    }

    public ChatModel(String id, String email, String comment, String time) {
        this.id = id;
        this.email = email;
        this.comment = comment;
        this.time = time;

    }
}
