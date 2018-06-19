package naveropenapi.example.com.aduinoproject.VoiceApi;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.util.Log;


public class VoiceRecoService extends Service {
    boolean mBoolVoiceRecoStarted = false;

    private SpeechRecognizer mSrRecognizer;

    private AudioManager mAudioManager;


    final private int MSG_VOICE_RECO_READY = 1001;
    final private int MSG_VOICE_RECO_END = 1002;
    final private int MSG_VOICE_RECO_RESTART = 1003;

    private Context mContext;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("핸들러 시험", "서비스 실행");
        mContext = getApplicationContext();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
//        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,  AudioManager.FLAG_SHOW_UI);
//        mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, true);
//        amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
//        amanager.setStreamMute(AudioManager.STREAM_ALARM, false);
//        amanager.setStreamMute(AudioManager.STREAM_RING, false);
//        amanager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        startListening();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public Handler mHdrVoiceRecoState = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_VOICE_RECO_READY:
                    break;
                case MSG_VOICE_RECO_END: {
                    stopListening();
                    sendEmptyMessageDelayed(MSG_VOICE_RECO_RESTART, 500);
                    break;
                }
                case MSG_VOICE_RECO_RESTART:
                    startListening();
                    Log.e("핸들러 시험", "리스타트");

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };


    public void startListening() {

        if (mBoolVoiceRecoStarted == false) {
            if (mSrRecognizer == null) {
                mSrRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
                mSrRecognizer.setRecognitionListener(mClsRecoListener);
            }
            if (mSrRecognizer.isRecognitionAvailable(mContext)) {
                Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ko-KR"); //언어지정입니다.
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mContext.getPackageName());
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);   //검색을 말한 결과를 보여주는 갯수
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 7000);   //검색을 말한 결과를 보여주는 갯수

//                mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                mSrRecognizer.startListening(recognizerIntent);
//                mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            }
        }
        mBoolVoiceRecoStarted = true;
    }

    public void stopListening() {
        try {
            if (mSrRecognizer != null && mBoolVoiceRecoStarted == true) {
                mSrRecognizer.stopListening();
            }
        } catch (Exception ex) {
//            Logger.e("Stop 예외:" + StrUtil.trace(ex));
        }
        mBoolVoiceRecoStarted = false;
    }


    private RecognitionListener mClsRecoListener = new RecognitionListener() {
        @Override
        public void onRmsChanged(float rmsdB) {
        }


        @Override
        public void onResults(Bundle results) {

            mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_END);

            Intent itBroadcast = new Intent();
            itBroadcast.setAction("com.aduinoproject.example.INTENT_ACTION_VOICE_RECO");
            itBroadcast.putExtras(results);
            mContext.sendBroadcast(itBroadcast);
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d("SpeechRecognizer", "Ready");

        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int intError) {
            mHdrVoiceRecoState.sendEmptyMessage(MSG_VOICE_RECO_END);
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }
    };


}