package naveropenapi.example.com.aduinoproject;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedSave {
    private int colorData;
    private Context mContext;

    private String MISAKI_CALL;
    private String SET_MEMO_CALL;
    private String SET_MEMOSTART_CALL;
    private String SET_MEMOSAVE_CALL;


    public SharedSave() {
    }

    public SharedSave(Context context) {
        this.mContext = context;
    }

    public void setColorData(int color) {

        SharedPreferences pref = mContext.getSharedPreferences("COLOR", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("SET_COLOR", color); //키값, 저장값
        colorData = color;
        editor.commit();

    }

    public void getColor() {
        //값읽기
        SharedPreferences prefs = mContext.getSharedPreferences("COLOR", MODE_PRIVATE);
        int result = prefs.getInt("SET_COLOR", 0); //키값, 디폴트값
        colorData = result;
    }

    public int getColorData() {
        return colorData;
    }


    public void saveVoiceCall(String c1, String c2, String c3, String c4) {
        SharedPreferences pref = mContext.getSharedPreferences("VOICE_CALL", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if (c1.trim().equals("")) {
            editor.putString("SET_MISAKI_CALL", "미사키"); //키값, 저장값
        }else{
            editor.putString("SET_MISAKI_CALL", c1.trim()); //키값, 저장값
        }

        if (c2.trim().equals("")) {
            editor.putString("SET_MEMO_CALL", "메모장 열어"); //키값, 저장값
        }else{
            editor.putString("SET_MEMO_CALL", c2.trim()); //키값, 저장값
        }

        if (c3.trim().equals("")) {
            editor.putString("SET_MEMOSTART_CALL", "메모 시작"); //키값, 저장값
        }else{
            editor.putString("SET_MEMOSTART_CALL", c3.trim()); //키값, 저장값
        }

        if (c4.trim().equals("")) {
            editor.putString("SET_MEMOSAVE_CALL", "메모 저장"); //키값, 저장값
        }else{
            editor.putString("SET_MEMOSAVE_CALL", c4.trim()); //키값, 저장값
        }

        editor.commit();

    }


    public void getVoiceCall() {
        //값읽기
        SharedPreferences prefs = mContext.getSharedPreferences("COLOR", MODE_PRIVATE);
        int result = prefs.getInt("SET_COLOR", 0); //키값, 디폴트값
        colorData = result;
    }


}
