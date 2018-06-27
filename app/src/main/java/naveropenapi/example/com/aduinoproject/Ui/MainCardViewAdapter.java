package naveropenapi.example.com.aduinoproject.Ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import naveropenapi.example.com.aduinoproject.MainMenuItem.Memo.MemoActivity;
import naveropenapi.example.com.aduinoproject.R;

/**
 * Created by fkfmz on 2018-06-11.
 */

public class MainCardViewAdapter extends RecyclerView.Adapter<MainCardViewAdapter.ViewHolder> {

    Context mContext;
    List<MainCardViewItem> items = new ArrayList<>();
    int item_layout;


    public MainCardViewAdapter(Context context, List<MainCardViewItem> items, int item_layout) {
        this.mContext = context;
        this.items = items;
        this.item_layout = item_layout;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).
                inflate(R.layout.albumview_item, null);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final MainCardViewItem item = items.get(position);
        Drawable drawable = ContextCompat.getDrawable(mContext, item.getImage());
//        holder.mImageView.setBackground(drawable);
        holder.title.setText(item.getTitleStr());
        holder.mCardView.setBackground(drawable);
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, item.getTitleStr(), Toast.LENGTH_SHORT).show();

                switch (position){
                    case 3:
                        Intent intent = new Intent(mContext, MemoActivity.class);
                        mContext.startActivity(intent);
                        Log.e("리사이클", "인텐트" );
                        break;
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {


        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView title;
        CardView mCardView;

        public ViewHolder(View itemView) {
            super(itemView);

            mImageView = (ImageView) itemView.findViewById(R.id.card_imageView1);
            title = (TextView) itemView.findViewById(R.id.card_textView1);
            mCardView = (CardView) itemView.findViewById(R.id.cardview);


        }
    }
}
