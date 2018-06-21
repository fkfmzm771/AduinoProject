package naveropenapi.example.com.aduinoproject.DB;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import naveropenapi.example.com.aduinoproject.MainActivity;
import naveropenapi.example.com.aduinoproject.R;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class FragChat extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private EditText editText;
    private Button btn_main, btn_voice;

    private FirebaseDatabase database = null;
    private DatabaseReference myRef = null;

    private List<ChatModel> mChat;
    private Context context;


    //파이어캐스트 이메일값
    private String email;
    private FirebaseUser user;

    public static DialogFlow mDialogFlow;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDialogFlow = new DialogFlow(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        context = getContext();


        //파이어캐스트 데이터 베이스
        database = FirebaseDatabase.getInstance();

        final View view = inflater.inflate(R.layout.frag_chat, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewChat);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            email = user.getEmail();

        }
        //chatModel 객체
        mChat = new ArrayList<>();


        //어댑터에 값 추가
        mAdapter = new MyAdapter(mChat, email);
        mRecyclerView.setAdapter(mAdapter);


        editText = (EditText) view.findViewById(R.id.edit_main);

        btn_main = (Button) view.findViewById(R.id.btn_main);
        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String et = editText.getText().toString();

                if (et.equals("") || et.isEmpty()) {

                    Toast.makeText(getActivity(), "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
                } else {
                    //데이터베이스에 넣을 헤쉬테이블값을 생성
                    Hashtable<String, String> chat = new Hashtable<>();

                    //캘랜더에서 날짜 정보를 가져옴
                    Calendar c = Calendar.getInstance();
                    //데이터 포멧을 설정
                    SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat sd2 = new SimpleDateFormat("HH:mm");
                    //데이터 포멧을 설정한 현재 시간을 가져옴
                    String formatDate1 = sd1.format(c.getTime());
                    String formatDate2 = sd2.format(c.getTime());

                    //데이터 베이스 레퍼런스를 설정
                    //chat란 레퍼런스 공간에 자식 값으로 '현재 시간'을 지정
                    myRef = database.getReference("MemberToken").child(user.getUid()).child("ChatBot").child(formatDate1);

                    //테이블에 데이터를 추가
                    //email은 사용자 정보에서 가져온 값이다.
                    chat.put("id", email);
                    chat.put("email", email);
                    //에디트 텍스트에 입력한 값
                    chat.put("comment", et);
                    chat.put("time", formatDate2);

                    //데이터베이스 레퍼런스에 값을 set
                    myRef.setValue(chat);
                    editText.setText("");

                }
            }
        });

        btn_voice = (Button) view.findViewById(R.id.btn_voice);
        btn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogFlow.button_Clicked();

            }
        });

        if (myRef == null) {
            myRef = database.getReference("MemberToken").child(user.getUid()).child("ChatBot");
        }
        myRef.addChildEventListener(new ChildEventListener() {
            //자식값이 추가될때 실행되는 메소드
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatModel chat = dataSnapshot.getValue(ChatModel.class);
                mChat.add(chat);

                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);


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

        view.findViewById(R.id.frag_chat_view)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideKey();
                    }
                });
        return view;
    }

    //키보드 hide 설정
    public void hideKey() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (DialogFlow.tts != null) {
            DialogFlow.tts.stop();
            DialogFlow.tts.shutdown();
        }
    }
}
