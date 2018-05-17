package naveropenapi.example.com.aduinoproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ai.api.AIConfiguration;
import naveropenapi.example.com.aduinoproject.Login.LoginActivity;
import naveropenapi.example.com.aduinoproject.NetWork.C_BlueTooth;
import naveropenapi.example.com.aduinoproject.Ui.ListViewAdapter;
import naveropenapi.example.com.aduinoproject.Ui.ListViewItem;
import naveropenapi.example.com.aduinoproject.VoiceApi.GoogleVoice;


/**
 * 2. JAVA 에서는 배열보다는 Util 패키지의 List,Set,Map 인터페이스를 주요 사용한다.
 * 배열은 같은 타입만 저장 가능하지만, 위의 인터페이스는 서로 다른 타입을 같은 List 안에 저장할 수 있다
 */
// 3. UUID : Universally Unique IDentifier, 범용 고유 실별자.import java.util.UUID;


public class MainActivity extends Activity {

    //리스트뷰 객체 구현
    private ListViewAdapter adapter;
    private ListView menuListView;
    private ArrayList<ListViewItem> itemList = new ArrayList<ListViewItem>();


    //view 객체 생성
    private EditText mEditReceive, mEditSend;
    private Button mButtonSend, voice_in;
    private Button mButtonLogout;
    private ArrayList<String> arDump = new ArrayList<String>();

    private C_BlueTooth blueTooth;
    private GoogleVoice googleVoice;

    private static String voice_result = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        adapter = new ListViewAdapter(itemList);
        menuListView = (ListView) findViewById(R.id.menu_list);

        adapter.addItem(null, "기능 추가", "예시1");
        adapter.addItem(null, "LED 설정", "예시2");
        adapter.addItem(null, "전원제어", "예시3");
        adapter.addItem(null, "기기 연결", "예시4");


        menuListView.setAdapter(adapter);

        mEditReceive = (EditText) findViewById(R.id.receiveString);
        mEditSend = (EditText) findViewById(R.id.sendString);
        mButtonSend = (Button) findViewById(R.id.sendButton);
        mButtonLogout = (Button) findViewById(R.id.btn_logout);
        mButtonLogout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //아두이노 문자 전송
        mButtonSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 문자열 전송하는 함수(쓰레드 사용 x)
                blueTooth.sendData(mEditSend.getText().toString());
//                sendData(BtStr);
                mEditSend.setText("");
            }
        });

        // 블루투스 활성화 시키는 메소드
        if (blueTooth != null) {
            blueTooth = new C_BlueTooth(this);
        }


        //구글 음성인식 시작

        if (googleVoice == null) {
            googleVoice = new GoogleVoice(this);
        }

        findViewById(R.id.voice_in).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                arDump.clear();
                mEditSend.setText("");
                startActivityForResult(googleVoice.VoiceBtn(), GoogleVoice.RESULT_SPEECH);
            }
        });

    }


    // onDestroy() : 어플이 종료될때 호출 되는 함수.
    //               블루투스 연결이 필요하지 않는 경우 입출력 스트림 소켓을 닫아줌.
    @Override
    protected void onDestroy() {
        try {
            blueTooth.mWorkerThread.interrupt(); // 데이터 수신 쓰레드 종료
            blueTooth.mInputStream.close();
            blueTooth.mSocket.close();
        } catch (Exception e) {
        }
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // startActivityForResult 를 여러번 사용할 땐 이런 식으로 switch 문을 사용하여 어떤 요청인지 구분하여 사용함.
        switch (requestCode) {
            case C_BlueTooth.REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) { // 블루투스 활성화 상태
                    blueTooth.selectDevice();
                } else if (resultCode == RESULT_CANCELED) { // 블루투스 비활성화 상태 (종료)
                    Toast.makeText(getApplicationContext(), "블루투스를 사용할 수 없어 프로그램을 종료합니다", Toast.LENGTH_LONG).show();
                    finish();
                }
            case GoogleVoice.RESULT_SPEECH:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    for (int i = 0; i < text.size(); i++) {
                        System.out.println("입력 음성 값" + i);

                       //보이스값 전송
                        mEditSend.setText(text.get(i));
                        String BtStr = text.get(i);
//                        blueTooth.sendData(BtStr);
                        voice_result = text.get(i);

                    }



                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}

