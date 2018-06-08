package naveropenapi.example.com.aduinoproject.VoiceApi;

import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import ai.api.AIListener;
import ai.api.model.AIError;
import ai.api.model.AIResponse;

/**
 * Created by hyunungLim on 2018-05-15.
 */

public class GoogleVoice{


    //구글 음성 API
    private SpeechRecognizer speech;
    private Intent recognizerIntent;
    private Context mContext;
    public final static int RESULT_SPEECH = 1000;


    public GoogleVoice(Context context){

        mContext = context;
        GoogleRecognition gooreco = new GoogleRecognition();

        speech = SpeechRecognizer.createSpeechRecognizer(context);
        speech.setRecognitionListener(gooreco);
        speech.startListening(voiceBtn());
    }

    public Intent voiceBtn(){
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ko-KR"); //언어지정입니다.
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mContext.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);   //검색을 말한 결과를 보여주는 갯수

        return recognizerIntent;

    }

}
