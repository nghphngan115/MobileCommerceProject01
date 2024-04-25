package com.group01.plantique.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.group01.plantique.java.BlogDetailActivity;
import com.group01.plantique.model.BlogItem;
import com.group01.plantique.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BlogAdapter extends ArrayAdapter<BlogItem> {

    private ArrayList<BlogItem> blogItems;
    private Context mContext;

    public BlogAdapter(Context context, ArrayList<BlogItem> blogItems) {
        super(context, 0, blogItems);
        mContext = context;
        this.blogItems = blogItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.blog_show, parent, false);
        }

        final BlogItem currentItem = blogItems.get(position);

        TextView titleTextView = convertView.findViewById(R.id.txtTitle1);
        titleTextView.setText(currentItem.getTitle());

        ImageView imageView = convertView.findViewById(R.id.imgBlog);
        Picasso.get().load(currentItem.getImage()).into(imageView);

        // Xử lý sự kiện khi người dùng nhấp vào một mục trong danh sách
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để mở BlogDetailActivity
                Intent intent = new Intent(mContext, BlogDetailActivity.class);

                // Đặt dữ liệu của mục hiện tại vào Intent
                intent.putExtra("blogId", currentItem.getBlogId());
                intent.putExtra("blogTitle", currentItem.getTitle());
                intent.putExtra("blogImage", currentItem.getImage());
                intent.putExtra("blogContent", currentItem.getContent());

                // Khởi chạy Intent
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }
}
