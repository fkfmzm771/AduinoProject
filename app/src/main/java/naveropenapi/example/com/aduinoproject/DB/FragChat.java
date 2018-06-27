package naveropenapi.example.com.aduinoproject.DB;

import android.content.Context;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import naveropenapi.example.com.aduinoproject.FireBase.FireBaseDB;
import naveropenapi.example.com.aduinoproject.Login.LoginCheck;
import naveropenapi.example.com.aduinoproject.Login.NaverLogin;
import naveropenapi.example.com.aduinoproject.R;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class FragChat extends Fragment {
    private final String TAG = "쳇봇 프래그먼트";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private EditText editText;
    private Button btn_main, btn_voice;
    private Context context;
    private FireBaseDB fireBaseDB;

    public static DialogFlow mDialogFlow;
    private View view;
    private List<ChatModel> chatList;




    void setView() {
        mRecyclerView = view.findViewById(R.id.recyclerViewChat);
        editText = view.findViewById(R.id.edit_main);
        btn_voice = view.findViewById(R.id.btn_voice);
        btn_main = view.findViewById(R.id.btn_main);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        context = getContext();
        view = inflater.inflate(R.layout.frag_chat, container, false);
        setView();

        mDialogFlow = new DialogFlow(context);
        fireBaseDB = new FireBaseDB();




        //리사이클뷰 셋
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
//        mRecyclerView.setLayoutManager(new GridLayoutManager(context, 4));
        mRecyclerView.setLayoutManager(mLayoutManager);

        chatList = new ArrayList<>();
        mAdapter = new ChatAdapter(chatList);
        mRecyclerView.setAdapter(mAdapter);

        //음성 다이얼로그 버튼
        btn_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogFlow.voice_stop();
                mDialogFlow.voice_start();
            }
        });

        //텍스트 다이얼로그 버튼
        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String et = editText.getText().toString();

                if (et.equals("") || et.isEmpty()) {
                    Toast.makeText(getActivity(), "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
                } else {
                    mDialogFlow.text_start(et);
                    editText.setText("");
                }
            }
        });

        if (LoginCheck.GoogleCheck || LoginCheck.FaceCheck) {
            fireBaseDB.input_Ref(fireBaseDB.getDatabase().getReference("MemberToken")
                    .child(fireBaseDB.getUser().getUid())
                    .child("ChatBot"));
        } else if (LoginCheck.NaverCheck) {
            fireBaseDB.input_Ref(fireBaseDB.getDatabase().getReference("MemberToken")
                    .child(NaverLogin.mOAuthLoginModule.getRefreshToken(context))
                    .child("ChatBot"));
        }
        fireBaseDB.getMyRef().addChildEventListener(new ChildEventListener() {
            //자식값이 추가될때 실행되는 메소드
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatModel chat = dataSnapshot.getValue(ChatModel.class);
                chatList.add(chat);
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

    }



}

