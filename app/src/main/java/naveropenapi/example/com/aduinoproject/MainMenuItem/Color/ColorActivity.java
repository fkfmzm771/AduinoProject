package naveropenapi.example.com.aduinoproject.MainMenuItem.Color;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

import naveropenapi.example.com.aduinoproject.MainActivity;
import naveropenapi.example.com.aduinoproject.R;

/**
 * Created by hyunungLim on 2018-06-21.
 */

public class ColorActivity extends AppCompatActivity {
    private ColorPicker picker;
    private SVBar svBar;
    private OpacityBar opacityBar;
    private SaturationBar saturationBar;
    private ValueBar valueBar;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_color_layout);

        mToolbar = findViewById(R.id.color_toolbar);
        mTabLayout = findViewById(R.id.color_tabLay);

        picker = (ColorPicker) findViewById(R.id.picker);
        svBar = (SVBar) findViewById(R.id.svbar);
        opacityBar = (OpacityBar) findViewById(R.id.opacitybar);
        saturationBar = (SaturationBar) findViewById(R.id.saturationbar);
        valueBar = (ValueBar) findViewById(R.id.valuebar);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        

        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);
        picker.addSaturationBar(saturationBar);
        picker.addValueBar(valueBar);

        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
//                mToolbar.setBackgroundColor(color);
                MainActivity.sSharedSave.setColorData(color);
                setColor();
            }
        });
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
