package naveropenapi.example.com.aduinoproject.DB;


import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import naveropenapi.example.com.aduinoproject.DeveloperKey;
import naveropenapi.example.com.aduinoproject.FireBase.FireBaseDB;
import naveropenapi.example.com.aduinoproject.Login.LoginCheck;
import naveropenapi.example.com.aduinoproject.Login.NaverLogin;

import static com.google.android.gms.internal.zzahf.runOnUiThread;

/**
 * Created by hyunungLim on 2018-05-14.
 */

public class DialogFlow extends FireBaseDB implements AIListener {

    private static final String TAG = "다이얼로그 플로어";
    private AudioManager mAudioManager;
    public AIService aiService;
    private Context mContext;
    private final AIConfiguration config;

    private Calendar c;
    private String formatDate1;
    private String formatDate2;
    private String formatDateYear;
    private String formatDateMonth;
    private String formatDateDay;
    private TTScall mTTScall;

    public DialogFlow(Context context) {
        mContext = context;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        //tts 객체 생성
        mTTScall = new TTScall(context);

        config = new AIConfiguration(DeveloperKey.DIAL_LOGFLOW_KEY,
                AIConfiguration.SupportedLanguages.Korean,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(context, config);
        aiService.setListener(this);

    }

    public void voice_stop() {
        Log.e(TAG, "보이스 플로어 멈춤");
        aiService.stopListening();
    }

    //음성입력
    public void voice_start() {
        Log.e(TAG, "보이스 플로어 시작");
        aiService.startListening();
    }

    //음성입력
    public void voice_start_Rcv() {
        Log.e(TAG, "리시버 보이스 플로어 시작");
        mTTScall.ttsStart("부르셨나요");
        aiService.startListening();
    }

    //텍스트 전송을 위해 Task 작성
    public class VoiceTextTask extends AsyncTask<String, Void, AIResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected AIResponse doInBackground(String... text) {
            AIResponse resp = null;
            AIRequest aiRequest = new AIRequest();
            aiRequest.setQuery(text[0]);
            AIDataService aiDataService = new AIDataService(config);

            try {
                resp = aiDataService.request(aiRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return resp;
        }
    }

    //텍스트 입력
    public void text_start(String request) {
        Log.e(TAG, "텍스트 플로어 시작");
        VoiceTextTask voiceTextTask = new VoiceTextTask();
        voiceTextTask.execute(request);
        try {
            final AIResponse response = voiceTextTask.get();
            onResult(response);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

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
        Log.e(TAG, "onResult: 실행");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Result result = response.getResult();

                date_set();
//                input_Ref(getMyRef().child("USER"));

                set_Refkey("type", "USER");
                set_Refkey("comment", result.getResolvedQuery());
                set_Refkey("time", formatDate2);

                //데이터베이스 레퍼런스에 값을 set
                set_ref_hash();


                try {
                    Thread.sleep(1000l);
                    date_set();
//                input_Ref(getMyRef().child("MISAKI"));

                    set_Refkey("type", "MISAKI");
                    set_Refkey("comment", result.getFulfillment().getSpeech());
                    set_Refkey("time", formatDate2);

                    //데이터베이스 레퍼런스에 값을 set
                    set_ref_hash();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //tts 실행
                mTTScall.ttsStart(result.getFulfillment().getSpeech());

            }
        });

    }

    void date_set() {
        //캘랜더에서 날짜 정보를 가져옴
        c = Calendar.getInstance();
        //데이터 포멧을 설정
        SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sd2 = new SimpleDateFormat("HH:mm");
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat month = new SimpleDateFormat("MM");
        SimpleDateFormat day = new SimpleDateFormat("dd");
        //데이터 포멧을 설정한 현재 시간을 가져옴
        formatDate1 = sd1.format(c.getTime());
        formatDate2 = sd2.format(c.getTime());
        formatDateYear = year.format(c.getTime());
        formatDateMonth = month.format(c.getTime());
        formatDateDay = day.format(c.getTime());


        if (LoginCheck.GoogleCheck || LoginCheck.FaceCheck) {
            input_Ref(getDatabase()
                    .getReference("MemberToken")
                    .child(getUser().getUid())
                    .child("ChatBot")
                    .child(formatDate1));
        }else if (LoginCheck.NaverCheck){
            input_Ref(getDatabase()
                    .getReference("MemberToken")
                    .child(NaverLogin.mOAuthLoginModule.getRefreshToken(mContext))
                    .child("ChatBot")
                    .child(formatDate1));
        }
    }

}

