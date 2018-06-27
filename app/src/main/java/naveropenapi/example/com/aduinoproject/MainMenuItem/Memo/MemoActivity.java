package naveropenapi.example.com.aduinoproject.MainMenuItem.Memo;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;
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
import naveropenapi.example.com.aduinoproject.FireBase.FireBaseDB;
import naveropenapi.example.com.aduinoproject.Login.LoginCheck;
import naveropenapi.example.com.aduinoproject.Login.NaverLogin;
import naveropenapi.example.com.aduinoproject.R;
import naveropenapi.example.com.aduinoproject.VoiceApi.GoogleRecognition;
import naveropenapi.example.com.aduinoproject.VoiceApi.GoogleVoice;

public class MemoActivity extends AppCompatActivity {
    private TextView memotxt;


    private AudioManager am;
    private SpeechRecognizer mSpeechRecognizer;
    private GoogleVoice mGoogleVoice;
    private GoogleRecognition mGoogleRecognition;

    private LinearLayout addLayout;
    private MemoAdapter mMemoAdapter;
    private ListView mListView;

    private FireBaseDB mFireBaseDB;

    private ArrayList<MemoData> mMemoData;

    private Context mContext;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo_layout);
        mContext = getApplicationContext();
        mFireBaseDB = new FireBaseDB();


        //시스템 사운드 제어
        am = (AudioManager) this.getSystemService(mContext.AUDIO_SERVICE);

        //스피치 초기화
        mGoogleRecognition = new GoogleRecognition();
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(mGoogleRecognition);
        mGoogleVoice = new GoogleVoice(mContext);
        memotxt = findViewById(R.id.memoText);

        //메모 어댑터 설정 시작
        // 메모장 어댑터
        mMemoData = new ArrayList<>();
        mMemoAdapter = new MemoAdapter(mContext, mMemoData);
        mListView = findViewById(R.id.memoListView);
        mListView.setAdapter(mMemoAdapter);
        mMemoAdapter.setMode(Attributes.Mode.Single);


        //리스트뷰 리스너 시작
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("ListView", "OnTouch");
                return false;
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, "OnItemLongClickListener", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.e("ListView", "onScrollStateChanged");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("ListView", "onItemSelected:" + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("ListView", "onNothingSelected:");
            }
        });
        //메모 어댑터 설정 끝

        //메모 추가 레이아웃
        addLayout = findViewById(R.id.memo_add_layout);
        //메모 추가 버튼
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

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formatDate1 = sd1.format(c.getTime());
                String txt = memotxt.getText().toString();

                if (LoginCheck.GoogleCheck || LoginCheck.FaceCheck) {
                    mFireBaseDB.input_Ref(mFireBaseDB.getDatabase().getReference("MemberToken")
                            .child(mFireBaseDB.getUser().getUid())
                            .child("Memo").child(formatDate1));
                } else if (LoginCheck.NaverCheck) {
                    mFireBaseDB.input_Ref(mFireBaseDB.getDatabase().getReference("MemberToken")
                            .child(NaverLogin.mOAuthLoginModule.getRefreshToken(mContext))
                            .child("Memo").child(formatDate1));
                }
                memo.put("time", formatDate1);
                memo.put("memo", txt);
                mFireBaseDB.getMyRef().setValue(memo);
                addLayout.setVisibility(View.INVISIBLE);

            }
        });

        //로그인 아이디 구분
        if (LoginCheck.GoogleCheck || LoginCheck.FaceCheck) {
            mFireBaseDB.input_Ref(mFireBaseDB.getDatabase().getReference("MemberToken")
                    .child(mFireBaseDB.getUser().getUid())
                    .child("Memo"));
        } else if (LoginCheck.NaverCheck) {
            mFireBaseDB.input_Ref(mFireBaseDB.getDatabase().getReference("MemberToken")
                    .child(NaverLogin.mOAuthLoginModule.getRefreshToken(mContext))
                    .child("Memo"));
        }

        //자식값이 추가될때 실행되는 메소드
        mFireBaseDB.getMyRef().addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                MemoData memo = dataSnapshot.getValue(MemoData.class);
                mMemoData.add(memo);
                mMemoAdapter.notifyDataSetChanged();

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
        if (addLayout.getVisibility() == View.VISIBLE) {
            addLayout.setVisibility(View.INVISIBLE);
        } else {
            finish();
        }
    }


    private void receiveResults(Bundle results) {
        String resultText;

        if ((results != null) && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION)) {
            List<String> heard = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            resultText = heard.get(0);
            memotxt.append(resultText);

        }
    }

}
