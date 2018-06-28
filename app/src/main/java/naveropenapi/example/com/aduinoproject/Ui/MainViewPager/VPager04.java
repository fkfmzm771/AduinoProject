package naveropenapi.example.com.aduinoproject.Ui.MainViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import naveropenapi.example.com.aduinoproject.MainMenuItem.Color.ColorActivity;
import naveropenapi.example.com.aduinoproject.MainMenuItem.Command.CommandActivity;
import naveropenapi.example.com.aduinoproject.R;

public class VPager04 extends Fragment {
    LinearLayout memoLay;

    public VPager04() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_viewpager04, container, false);
        memoLay = view.findViewById(R.id.pager_command);
        memoLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("커맨드", "인텐트 시작");
                Intent intent = new Intent(getActivity(), CommandActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }
}
