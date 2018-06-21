package naveropenapi.example.com.aduinoproject.MainMenuItem.Memo;

/**
 * Created by hyunungLim on 2018-06-21.
 */

public class MemoData {

    private String date;
    private String memo_in;

    public MemoData() {
    }

    public MemoData(String date, String memo_in) {
        this.date = date;
        this.memo_in = memo_in;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMemo_in() {
        return memo_in;
    }

    public void setMemo_in(String memo_in) {
        this.memo_in = memo_in;
    }
}
