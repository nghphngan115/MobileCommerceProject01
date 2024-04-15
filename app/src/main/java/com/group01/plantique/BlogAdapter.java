package com.group01.plantique;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class BlogAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private ArrayList<String> mBlogTitles;
    private OnBlogClickListener mClickListener;

    public BlogAdapter(Context context, ArrayList<String> blogTitles) {
        super(context, 0, blogTitles);
        mContext = context;
        mBlogTitles = blogTitles;
    }

    public void setOnBlogClickListener(OnBlogClickListener listener) {
        this.mClickListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.blog_show, parent, false);
        }

        String currentTitle = mBlogTitles.get(position);

        TextView titleTextView = listItem.findViewById(R.id.txtTitle1);
        titleTextView.setText(currentTitle);

        // Thiết lập sự kiện click cho mỗi mục trong ListView
        listItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    // Lấy dữ liệu của blog được click
                    String title = mBlogTitles.get(position);
                    // Gửi sự kiện click đến Activity
                    mClickListener.onBlogClick(title);
                }
            }
        });

        return listItem;
    }

    // Interface cho sự kiện click
    public interface OnBlogClickListener {
        void onBlogClick(String title);
    }
}
