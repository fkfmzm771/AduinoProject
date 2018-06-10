package naveropenapi.example.com.aduinoproject;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import naveropenapi.example.com.aduinoproject.DB.DialogFlow;
import naveropenapi.example.com.aduinoproject.Login.LoginActivity;
import naveropenapi.example.com.aduinoproject.NetWork.C_BlueTooth;
import naveropenapi.example.com.aduinoproject.Ui.ListViewAdapter;
import naveropenapi.example.com.aduinoproject.Ui.ListViewItem;
import naveropenapi.example.com.aduinoproject.VoiceApi.GoogleVoice;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    //다이얼로그 플로어
    public static DialogFlow D_FLOW;

    //리스트뷰 객체 구현
    private ListViewAdapter adapter;
    private ListView menuListView;
    private ArrayList<ListViewItem> itemList = new ArrayList<ListViewItem>();


    //view 객체 생성
    private TextView mEditReceive;
    private EditText mEditSend;
    private Button mButtonSend;
    private ArrayList<String> arDump = new ArrayList<String>();

    private C_BlueTooth blueTooth;
    private GoogleVoice googleVoice;

    private static String voice_result = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //다이얼로그 플로어 객체 생성
        D_FLOW = new DialogFlow(getApplicationContext());

        adapter = new ListViewAdapter(itemList);
        menuListView = (ListView) findViewById(R.id.menu_list);

        adapter.addItem(null, "사용자 명령어", "예시1");
        adapter.addItem(null, "LED 제어", "예시2");
        adapter.addItem(null, "전원 제어", "예시3");
        adapter.addItem(null, "기기 연결", "예시4");


        menuListView.setAdapter(adapter);

//        mEditReceive = (TextView) findViewById(R.id.receiveString);
//        mEditSend = (EditText) findViewById(R.id.sendString);
//        mButtonSend = (Button) findViewById(R.id.sendButton);


        //아두이노 문자 전송
//        mButtonSend.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 문자열 전송하는 함수(쓰레드 사용 x)
//                blueTooth.sendData(mEditSend.getText().toString());
//                sendData(BtStr);
//                mEditSend.setText("");
//            }
//        });

        // 블루투스 활성화 시키는 메소드
        if (blueTooth != null) {
            blueTooth = new C_BlueTooth(this);
        }

        //구글 음성인식 시작
//
//        if (googleVoice == null) {
//            googleVoice = new GoogleVoice(this);
//        }
//
//        findViewById(R.id.voice_in).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                arDump.clear();
//                mEditSend.setText("");
//                startActivityForResult(googleVoice.VoiceBtn(), GoogleVoice.RESULT_SPEECH);
//            }
//        });


        //네비게이션 드로어 구현
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //플로팅버튼 호출
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SlidingUpPanelLayout slidingUpPanelLayout = findViewById(R.id.sliding_layout);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                D_FLOW.button_Clicked();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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


    //네비게이션 드로어 메뉴
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {


        } else if (id == R.id.btn_logout) {  //로그아웃 버튼
//            LoginActivity.mAuth.signOut();
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        SlidingUpPanelLayout slidingUpPanelLayout = findViewById(R.id.sliding_layout);

        if ((drawer.isDrawerOpen(GravityCompat.START) || slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        } else {
            super.onBackPressed();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drower, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

