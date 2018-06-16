package naveropenapi.example.com.aduinoproject.VoiceApi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

import naveropenapi.example.com.aduinoproject.MainActivity;

public class TestReceiver extends BroadcastReceiver {

    private static final String TAG = "TestReceiver 시험중";


    @Override
    public void onReceive(Context context, Intent intent) {

        String name = intent.getAction(); // Intent SendBroadCast로 보낸 action TAG 이름으로 필요한 방송을 찾는다.
        Bundle results = null;


        if (name.equals("com.aduinoproject.example.INTENT_ACTION_VOICE_RECO")) {
            Log.d(TAG, "BroadcastReceiver :: com.dwfox.myapplication.SEND_BROAD_CAST :: " + intent.getStringExtra("sendString")); // putExtra를 이용한 String전달 } } }
            Log.e(TAG, "onReceive: " + name);
            results = intent.getExtras();
        }

        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (int i = 0; i < matches.size(); i++) {
            Log.e(TAG, "onResults text : " + matches.get(i));
            if (matches.get(i).equals("미사키 안녕")) {
                Log.e(TAG, "onResults text : " + "네 주인님");
                VoiceRecoService voice = voice.mHdrVoiceRecoState.removeMessages(0);
                MainActivity.D_FLOW.button_Clicked();
            }

        }
    }
}
