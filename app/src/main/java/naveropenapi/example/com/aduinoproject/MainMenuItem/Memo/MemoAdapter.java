package naveropenapi.example.com.aduinoproject.MainMenuItem.Memo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import naveropenapi.example.com.aduinoproject.R;


/**
 * Created by hyunungLim on 2018-06-21.
 */

public class MemoAdapter extends BaseAdapter{

    private ArrayList<MemoData> mMemoData = new ArrayList<>();



    @Override
    public int getCount() {
        return mMemoData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.memo_item_layout, parent, false);

        }

        TextView memoDate = convertView.findViewById(R.id.memodate);
        TextView memo_in= convertView.findViewById(R.id.memo_in);

        MemoData memoData = mMemoData.get(position);

        memoDate.setText(memoData.getDate());
        memo_in.setText(memoData.getMemo_in());


        return convertView;

    }

    public void addMemoItem(String date, String memoIn){
        MemoData memoData = new MemoData(date, memoIn);
        mMemoData.add(memoData);


    }
}
