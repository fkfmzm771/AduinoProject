package naveropenapi.example.com.aduinoproject.MainMenuItem.Memo;

/**
 * Created by hyunungLim on 2018-06-21.
 */

public class MemoData {

    private String time;
    private String memo;

    public MemoData() {
    }

    public MemoData(String time, String memo) {
        this.time = time;
        this.memo = memo;
    }

    public String getTime() {
        return time;
    }

    public String getMemo() {
        return memo;
    }
}
