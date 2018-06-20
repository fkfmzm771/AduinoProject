package naveropenapi.example.com.aduinoproject.DB;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import naveropenapi.example.com.aduinoproject.R;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<ChatModel> mChat = new ArrayList<>();
    private String email;

    private final int MISAKI = 1;
    private final int USER = 2;
    private final int FOOTER = 3;

    //노멀 뷰 홀더 클래스 설정----
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView chat_comment;
        public TextView chat_time;


        public ViewHolder(View itemview) {
            super(itemview);
            chat_comment = (TextView) itemview.findViewById(R.id.chat_comment);
            chat_time = (TextView) itemview.findViewById(R.id.chat_time);
        }
    }


    //생성자
    public MyAdapter(List<ChatModel> mChat, String email) {
        this.mChat = mChat;
        this.email = email;

    }


    @Override
    public int getItemViewType(int position) {

        if (position < mChat.size()) {
            if ((mChat.get(position).getEmail()).equals(email)) {
                return USER;
            } else if ((mChat.get(position).getEmail()).equals("MISAKI")) {
                return MISAKI;
            }
        }
        return FOOTER;


    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;

        if (viewType == MISAKI) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_item_layout, parent, false);
        } else if (viewType == USER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_item_mylayout, parent, false);
        } else if (viewType == FOOTER) {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.view_item_footer, parent, false);
        }

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position < mChat.size()) {
            holder.chat_comment.setText(mChat.get(position).getComment());
            holder.chat_time.setText(mChat.get(position).getTime());
        }


    }


    @Override
    public int getItemCount() {
        return mChat.size() + 1;
    }

}
