package naveropenapi.example.com.aduinoproject.Ui.MainViewPager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import naveropenapi.example.com.aduinoproject.R;

public class VPager01 extends Fragment{

    public VPager01() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_viewpager01,container,false);


        view.findViewById(R.id.frag_Ar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"아직 준비중이에요",Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
