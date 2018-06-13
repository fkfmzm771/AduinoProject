package naveropenapi.example.com.aduinoproject.DB;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonElement;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import naveropenapi.example.com.aduinoproject.DeveloperKey;

import static com.google.android.gms.internal.zzahf.runOnUiThread;

/**
 * Created by hyunungLim on 2018-05-14.
 */

public class DialogFlow implements AIListener {


    public AIService aiService;
    private Context mContext;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseUser user;

    public static TextToSpeech tts;
    public final AIConfiguration config;
    private String email;


    public DialogFlow(Context context) {
        mContext = context;

        //tts 객체 생성
        tts=new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        config = new AIConfiguration(DeveloperKey.DIAL_LOGFLOW_KEY,
                AIConfiguration.SupportedLanguages.Korean,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(context, config);
        aiService.setListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

    }

    public void button_Clicked() {
        aiService.startListening();

    }


    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {
        Toast.makeText(mContext, "리스닝 시작", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onListeningCanceled() {
        Toast.makeText(mContext, "리스닝 캔슬", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListeningFinished() {
        Toast.makeText(mContext, "리스닝 피니쉬", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResult(final AIResponse response) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Result result = response.getResult();

                // Get parameters
                String parameterString = "";
                if (result.getParameters() != null && !result.getParameters().isEmpty()) {
                    for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                        parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
                    }
                }

                System.out.println(parameterString);

                //캘랜더에서 날짜 정보를 가져옴
                Calendar c = Calendar.getInstance();
                //데이터 포멧을 설정
                SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat sd2 = new SimpleDateFormat("HH:mm");
                //데이터 포멧을 설정한 현재 시간을 가져옴
                String formatDate1 = sd1.format(c.getTime());
                String formatDate2 = sd2.format(c.getTime());

                //데이터베이스에 넣을 헤쉬테이블값을 생성
                Hashtable<String, String> chat = new Hashtable<>();


                if (user != null) {
                    email = user.getEmail();
                }

                //데이터 베이스 레퍼런스를 설정
                //chat란 레퍼런스 공간에 자식 값으로 '현재 시간'을 지정
                myRef = database.getReference("MemberToken").child(user.getUid()).child("ChatBot").child(formatDate1);

                //미사키 값 추가
                //테이블에 데이터를 추가
                //email은 사용자 정보에서 가져온 값이다.
                chat.put("id", email);
                chat.put("email", email);
                //에디트 텍스트에 입력한 값
                chat.put("comment", result.getResolvedQuery());
                chat.put("time", formatDate2);

                //데이터베이스 레퍼런스에 값을 set
                myRef.setValue(chat);


                try {
                    Thread.sleep(1000l);

                    c = Calendar.getInstance();
                    formatDate1 = sd1.format(c.getTime());
                    formatDate2 = sd2.format(c.getTime());
                    //데이터 베이스 레퍼런스를 설정
                    //chat란 레퍼런스 공간에 자식 값으로 '현재 시간'을 지정
                    myRef = database.getReference("MemberToken").child(user.getUid()).child("ChatBot").child(formatDate1);

                    //미사키 값 추가
                    //테이블에 데이터를 추가
                    //email은 사용자 정보에서 가져온 값이다.
                    chat.put("id", "MISAKI");
                    chat.put("email", "MISAKI");
                    //에디트 텍스트에 입력한 값
                    chat.put("comment", result.getFulfillment().getSpeech());
                    chat.put("time", formatDate2);

                    //데이터베이스 레퍼런스에 값을 set
                    myRef.setValue(chat);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //tts 실행
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ttsGreater21(result.getFulfillment().getSpeech());
                } else {
                    ttsUnder20(result.getFulfillment().getSpeech());
                }

            }
        });



    }



    //tts 처리
    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

}


