package naveropenapi.example.com.aduinoproject.Ui;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import naveropenapi.example.com.aduinoproject.R;

/**
 * Created by MIT-18 on 2016-09-12.
 */
public class ListViewAdapter extends BaseAdapter {
    private ArrayList<ListViewItem> listViewItemList;

    private boolean isCheckedConfrim;
    private boolean[] checkitemPosition;


    // ListViewAdapter의 생성자
    public ListViewAdapter(ArrayList<ListViewItem> itemList) {
        if (itemList == null) {
            listViewItemList = new ArrayList<ListViewItem>();
        } else {
            listViewItemList = itemList;
        }
    }


    public void setAllChecked(boolean ischeked) {
        isCheckedConfrim = ischeked;
        for (int i = 0; i < checkitemPosition.length; i++) {
            checkitemPosition[i] = false;
        }
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {

        return listViewItemList.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        //체크리스트 등록을 기억하기 위한 배열
        if(checkitemPosition==null){
            checkitemPosition = new boolean[getCount()];

        }
        final CustomViewHolder holder;
        final int pos = position;
        final Context context = parent.getContext();
        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
//        if (convertView == null){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.albumview_item, parent, false);

        holder = new CustomViewHolder();
        holder.m_Image = (ImageView) convertView.findViewById(R.id.album_imageView1);
        holder.m_title = (TextView) convertView.findViewById(R.id.album_textView1);
        holder.m_desc = (TextView) convertView.findViewById(R.id.album_textView2);

        ListViewItem listViewItem = listViewItemList.get(position);

        holder.m_Image.setImageBitmap(listViewItem.getIcon());
        holder.m_title.setText(listViewItem.getTitle());
        holder.m_desc.setText(listViewItem.getDesc());


        convertView.setTag(holder);

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String icon, String title, String desc) {
        ListViewItem item = new ListViewItem();
        item.setIcon(icon);
        item.setTitle(title);
        item.setDesc(desc);
        listViewItemList.add(item);
    }

    //클릭 체크 리스트시 변경
    public View checked(View v, int position) {
        CheckBox check = (CheckBox) v.findViewById(R.id.album_checkbox);

        if (isCheckedConfrim == true && check.isChecked() == false) {
            check.setChecked(true);
            v.setBackgroundColor(Color.parseColor("#e2e8fa"));
            checkitemPosition[position] = true;
            for (int i = 0; i < checkitemPosition.length; i++) {
                Log.e(i + "  " + checkitemPosition[i] + "", "배열값");
            }
        } else if (check.isChecked() == true) {
            check.setChecked(false);


            checkitemPosition[position] = false;

        }
        return v;
    }


    //--홀더 생성-----
    public class CustomViewHolder {
        public ImageView m_Image;
        public TextView m_title;
        public TextView m_desc;
    }
}