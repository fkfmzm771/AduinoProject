package naveropenapi.example.com.aduinoproject.MainMenuItem.Memo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.util.ArrayList;

import naveropenapi.example.com.aduinoproject.R;


public class MemoAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private ArrayList<MemoData> memoList;

    public MemoAdapter(Context mContext, ArrayList<MemoData> memoList) {
        this.mContext = mContext;
        this.memoList = memoList;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {

//        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.memo_item_layout, parent, false);

        View view = LayoutInflater.from(mContext).inflate(R.layout.memo_item_layout, null);
        SwipeLayout swipeLayout = view.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {   //스와이프 설정
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));
            }
        });
        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {   //스와이프 더블 클릭
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {      //스와이프 딜리트 설정
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "click delete", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void fillValues(int position, View convertView) {    //기본 뷰 처리

            TextView date = convertView.findViewById(R.id.text_day);
            date.setText(memoList.get(position).getTime());
        System.out.println("출력체크"+ memoList.get(position).getTime());
            TextView text = convertView.findViewById(R.id.text_data);
            text.setText(memoList.get(position).getMemo());
    }

    @Override
    public int getCount() {
        return memoList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
