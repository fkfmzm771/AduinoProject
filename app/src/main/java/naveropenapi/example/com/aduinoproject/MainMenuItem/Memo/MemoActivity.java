package naveropenapi.example.com.aduinoproject.MainMenuItem.Memo;

import android.content.Context;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import naveropenapi.example.com.aduinoproject.DB.ChatModel;
import naveropenapi.example.com.aduinoproject.DB.FragChat;
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

    private LinearLayout addLayout;
    private MemoAdapter mMemoAdapter;
    private ListView mListView;
    private FireBaseDB mFireBaseDB;
    private ArrayList<MemoData> mMemoData;
    private Context mContext;
    private String position;

    private String backupText = "";

    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;


    //스와이프레이아웃 AsyncTask
    private class Task extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... voids) {
            return new String[0];
        }
        @Override
        protected void onPostExecute(String[] result) {
            // Call setRefreshing(false) when the list has been refreshed.
            mWaveSwipeRefreshLayout.setRefreshing(false);
            super.onPostExecute(result);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.memo_layout);
        mContext = getApplicationContext();
        mFireBaseDB = new FireBaseDB();

        //스와이프 리플래시 레이아웃
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) findViewById(R.id.main_swipe);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mMemoAdapter.notifyDataSetChanged();
                new Task().execute();
            }
        });



        //시스템 사운드 제어
        am = (AudioManager) this.getSystemService(mContext.AUDIO_SERVICE);

        //스피치 초기화
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(mRecognitionListener);
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

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("ListView", "onclick:" + position);
                addLayout.setVisibility(View.VISIBLE);
                setPosition(position);
                memotxt.setText(mMemoData.get(position).getMemo());
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

               if (memotxt.getText().equals("")){
                   Toast.makeText(mContext,"내용을 입력해 주세요",Toast.LENGTH_SHORT).show();
               }
                Hashtable<String, String> memo = new Hashtable<>();
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formatDate1 = sd1.format(c.getTime());
                String txt = memotxt.getText().toString();

                if (LoginCheck.GoogleCheck) {
                    mFireBaseDB.input_Ref(mFireBaseDB.getDatabase().getReference("MemberToken")
                            .child(mFireBaseDB.getUser().getUid())
                            .child("Memo").child(formatDate1));
                } else if (LoginCheck.NaverCheck) {
                    mFireBaseDB.input_Ref(mFireBaseDB.getDatabase().getReference("MemberToken")
                            .child(NaverLogin.mOAuthLoginModule.getRefreshToken(mContext))
                            .child("Memo").child(formatDate1));
                }
//                memo.put("number", position);
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
                mMemoData.add(0,memo);
//                mMemoData.add(memo);
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

    //리스트 포지션 셋
    void setPosition(int position) {
        this.position = String.valueOf(position);
    }

    @Override
    public void onBackPressed() {
        mSpeechRecognizer.stopListening();
        if (addLayout.getVisibility() == View.VISIBLE) {
            addLayout.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }


    private void receiveResults(Bundle results) {
        String resultText = "";

        if ((results != null) && results.containsKey(SpeechRecognizer.RESULTS_RECOGNITION)) {
            List<String> heard = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            resultText = heard.get(0);
            Log.e("메모 스피치 테스트", heard.get(0));
        }
        memotxt.setText(backupText + resultText);
    }


    private RecognitionListener mRecognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.e("메모 스피치 테스트","메모 스피치 시작");
            if (!memotxt.getText().equals("")){
                backupText = memotxt.getText().toString();
            }
        }

        @Override
        public void onRmsChanged(float v) {
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
        }

        @Override
        public void onEndOfSpeech() {
            Log.e("메모 스피치 테스트","메모 스피치 종료");
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
}



