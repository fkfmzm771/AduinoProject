package naveropenapi.example.com.aduinoproject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.google.firebase.auth.FirebaseAuth;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import naveropenapi.example.com.aduinoproject.Login.LoginActivity;
import naveropenapi.example.com.aduinoproject.NetWork.C_BlueTooth;
import naveropenapi.example.com.aduinoproject.Ui.BackPressCloseHandler;
import naveropenapi.example.com.aduinoproject.Ui.MainViewPager.PagerAdapter;
import naveropenapi.example.com.aduinoproject.VoiceApi.VoiceRecoService;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    public static SharedSave sSharedSave;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;
    private C_BlueTooth blueTooth;

    private boolean VOICEON = false;

    final int ITEM_SIZE = 4;

    //백 프레스
    private BackPressCloseHandler backPressCloseHandler;
    private FloatingActionButton fab;


    private void setColor() {
        sSharedSave.getColor();
        if (sSharedSave.getColorData() != 0) {
            mTabLayout.setBackgroundColor(sSharedSave.getColorData());
            mToolbar.setBackgroundColor(sSharedSave.getColorData());
            if (Build.VERSION.SDK_INT >= 21) {
                getWindow().setStatusBarColor(MainActivity.sSharedSave.getColorData());
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sSharedSave = new SharedSave(getApplicationContext());

        mToolbar = findViewById(R.id.main_toolbar);

        mTabLayout = findViewById(R.id.main_tabLay);
        backPressCloseHandler = new BackPressCloseHandler(MainActivity.this);
        mViewPager = findViewById(R.id.menu_list);
        mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        mViewPager.setPageTransformer(true, new CubeOutTransformer());
//        mViewPager.setCurrentItem(50);
        mTabLayout.setupWithViewPager(mViewPager);

        setColor();


        //다이얼로그 플로어 객체 생성

//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.menu_list);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(layoutManager);

//        List<MainCardViewItem> items = new ArrayList<>();
//        MainCardViewItem[] item = new MainCardViewItem[ITEM_SIZE];
//        item[0] = new MainCardViewItem(R.drawable.menu_color, "");
//        item[1] = new MainCardViewItem(R.drawable.menu_sound, "");
//        item[2] = new MainCardViewItem(R.drawable.menu_command, "");
//        item[3] = new MainCardViewItem(R.drawable.menu_memo, "");
//
//        for (int i = 0; i < ITEM_SIZE; i++) {
//            items.add(item[i]);
//        }

//        recyclerView.setAdapter(new MainCardViewAdapter(MainActivity.this, items, R.layout.activity_main));


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


        //네비게이션 드로어 구현
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        //플로팅버튼 호출
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SlidingUpPanelLayout slidingUpPanelLayout = findViewById(R.id.sliding_layout);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    protected void onDestroy() {
        try {
            blueTooth.mWorkerThread.interrupt(); // 데이터 수신 쓰레드 종료
            blueTooth.mInputStream.close();
            blueTooth.mSocket.close();
        } catch (Exception e) {
        }

        Intent intent = new Intent(getApplicationContext(), VoiceRecoService.class);
        stopService(intent);

        super.onDestroy();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // startActivityForResult 를 여러번 사용할 땐 이런 식으로 switch 문을 사용하여 어떤 요청인지 구분하여 사용함.
        //블루투스
        if (requestCode == C_BlueTooth.REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) { // 블루투스 활성화 상태
                blueTooth.selectDevice();
            } else if (resultCode == RESULT_CANCELED) { // 블루투스 비활성화 상태 (종료)
                Toast.makeText(getApplicationContext(), "블루투스를 사용할 수 없어 프로그램을 종료합니다", Toast.LENGTH_LONG).show();
                finish();
            }

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
            if (VOICEON == false) {
                startService(new Intent(MainActivity.this, VoiceRecoService.class));
                VOICEON = true;
            }else if (VOICEON == true){
                stopService(new Intent(MainActivity.this, VoiceRecoService.class));
                VOICEON = false;
            }

            //보이스 상시대기

        } else if (id == R.id.btn_logout) {  //로그아웃 버튼
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
            backPressCloseHandler.onBackPressed();

        }
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

    @Override
    protected void onStart() {
        setColor();


        Intent in = getIntent();
        boolean voice_call = in.getBooleanExtra("VOICE_CALL", false);

        if (voice_call) {
            SlidingUpPanelLayout slidingUpPanelLayout = findViewById(R.id.sliding_layout);
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
        super.onStart();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onResume() {
        setColor();
        super.onResume();

    }


}

