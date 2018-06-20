package naveropenapi.example.com.aduinoproject.Memo;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import naveropenapi.example.com.aduinoproject.R;
import naveropenapi.example.com.aduinoproject.VoiceApi.GoogleVoice;

public class MemoActivity extends AppCompatActivity {
    TextView memotxt;


    private AudioManager am;
    SpeechRecognizer mSpeechRecognizer;
    GoogleVoice mGoogleVoice;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo_layout);

        am = (AudioManager)this.getSystemService(getApplicationContext().AUDIO_SERVICE);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(mRecognitionListener);
        mGoogleVoice = new GoogleVoice(getApplicationContext());
        memotxt = findViewById(R.id.memoText);

        findViewById(R.id.memoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                am.setStreamMute(AudioManager.STREAM_SYSTEM, true);


                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        am.setStreamMute(AudioManager.STREAM_SYSTEM, false);
                    }
                }, 1000);
                mSpeechRecognizer.startListening(mGoogleVoice.voiceBtn());

                Log.e("Memo 버튼 실행", "보이스인텐트");
            }


        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSpeechRecognizer.stopListening();
        finish();
    }

    private RecognitionListener mRecognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int i) {

        }

        @Override
        public void onResults(Bundle bundle) {


        }

        @Override
        public void onPartialResults(Bundle bundle) {
            receiveResults(bundle);
        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

    private void receiveResults(Bundle results) {

        String resultText;

        if ((results != null) && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION)) {
            List<String> heard = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            resultText = heard.get(0);
            memotxt.append(resultText);

        }
    }

}
