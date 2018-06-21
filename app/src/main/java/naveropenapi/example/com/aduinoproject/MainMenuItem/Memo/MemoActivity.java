package naveropenapi.example.com.aduinoproject.MainMenuItem.Memo;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import naveropenapi.example.com.aduinoproject.DB.ChatModel;
import naveropenapi.example.com.aduinoproject.R;
import naveropenapi.example.com.aduinoproject.VoiceApi.GoogleVoice;

public class MemoActivity extends AppCompatActivity {
    TextView memotxt;


    private AudioManager am;
    SpeechRecognizer mSpeechRecognizer;
    GoogleVoice mGoogleVoice;

    LinearLayout addLayout;
    MemoAdapter mMemoAdapter;
    ListView mListView;
    TextView memoTxt;

    private FirebaseDatabase database = null;
    private DatabaseReference myRef = null;
    private FirebaseUser user;

    String email;

    private List<MemoData> mMemoData;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo_layout);

        mMemoData = new ArrayList<>();
        database = FirebaseDatabase.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            email = user.getEmail();

        }

        am = (AudioManager) this.getSystemService(getApplicationContext().AUDIO_SERVICE);

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(mRecognitionListener);
        mGoogleVoice = new GoogleVoice(getApplicationContext());
        memotxt = findViewById(R.id.memoText);


        // 메모장 어댑터
        memotxt = findViewById(R.id.memoText);

        mMemoAdapter = new MemoAdapter();
        mListView = findViewById(R.id.memoListView);
        mListView.setAdapter(mMemoAdapter);

        addLayout = findViewById(R.id.memo_add_layout);


        findViewById(R.id.memoAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout.setVisibility(View.VISIBLE);
                memotxt.setText("");

            }
        });


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

        findViewById(R.id.memoSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hashtable<String, String> memo = new Hashtable<>();

                //캘랜더에서 날짜 정보를 가져옴
                Calendar c = Calendar.getInstance();
                //데이터 포멧을 설정
                SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //데이터 포멧을 설정한 현재 시간을 가져옴
                String formatDate1 = sd1.format(c.getTime());
                String txt = memotxt.getText().toString();

                myRef = database.getReference("MemberToken").child(user.getUid()).child("Memo").child(formatDate1);

                //테이블에 데이터를 추가
                //email은 사용자 정보에서 가져온 값이다.
                //에디트 텍스트에 입력한 값
                memo.put("time", formatDate1);
                memo.put("memo", txt);

                //데이터베이스 레퍼런스에 값을 set
                myRef.setValue(memo);


                mMemoAdapter.addMemoItem(formatDate1, txt);
                mMemoAdapter.notifyDataSetChanged();
                addLayout.setVisibility(View.INVISIBLE);

            }
        });

        if (myRef == null) {
            myRef = database.getReference("MemberToken").child(user.getUid()).child("Memo");
        }

        myRef.addChildEventListener(new ChildEventListener() {
            //자식값이 추가될때 실행되는 메소드
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MemoData memo = dataSnapshot.getValue(MemoData.class);
                mMemoData.add(memo);

                mMemoAdapter.notifyDataSetChanged();
//                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);


            }

            //addChildEventListener 인터페이스들
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSpeechRecognizer.stopListening();
        addLayout.setVisibility(View.INVISIBLE);

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
