package naveropenapi.example.com.aduinoproject.DB;

/**
 * Created by hyunungLim on 2017-12-13.
 */

public class ChatModel {
    private String comment;
    private String time;
    private String type;

    public String getComment() {
        return comment;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }


    public ChatModel(){

    }
    public ChatModel(String comment, String time, String type) {
        this.comment = comment;
        this.time = time;
        this.type = type;
    }
}
