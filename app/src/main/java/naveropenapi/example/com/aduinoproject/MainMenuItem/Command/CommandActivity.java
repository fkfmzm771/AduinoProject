package naveropenapi.example.com.aduinoproject.MainMenuItem.Command;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import naveropenapi.example.com.aduinoproject.MainActivity;
import naveropenapi.example.com.aduinoproject.R;

/**
 * Created by hyunungLim on 2018-06-21.
 */

public class CommandActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_command_layout);

        mToolbar = findViewById(R.id.command_toolbar);
        mTabLayout = findViewById(R.id.command_tabLay);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        setColor();
        super.onStart();
    }

    @Override
    protected void onPostResume() {
        setColor();
        super.onPostResume();
    }

    public void setColor() {
        MainActivity.sSharedSave.getColor();
        if (MainActivity.sSharedSave.getColorData() != 0) {
            mToolbar.setBackgroundColor(MainActivity.sSharedSave.getColorData());
            mTabLayout.setBackgroundColor(MainActivity.sSharedSave.getColorData());
            if (Build.VERSION.SDK_INT >= 21) {
                getWindow().setStatusBarColor(MainActivity.sSharedSave.getColorData());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
