package naveropenapi.example.com.aduinoproject.Ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import naveropenapi.example.com.aduinoproject.Login.LoginActivity;
import naveropenapi.example.com.aduinoproject.R;



public class IntroActivity extends AppCompatActivity {

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(IntroActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        handler.postDelayed(runnable, 2000);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
    }
}
