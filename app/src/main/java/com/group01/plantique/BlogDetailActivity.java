package com.group01.plantique;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class BlogDetailActivity extends AppCompatActivity {

    private ImageView imgButton2;
    private ImageView imgBlog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);

        // Ánh xạ các thành phần trong layout
        TextView txtTitle = findViewById(R.id.txtTitle1);
        TextView txtContent = findViewById(R.id.txtContent);
        imgBlog = findViewById(R.id.imgBlog);
        imgButton2 = findViewById(R.id.imgButton2);

        // Tham chiếu đến nút blog trên Firebase
        DatabaseReference blogRef = FirebaseDatabase.getInstance().getReference("Blog");

        // Lắng nghe sự thay đổi dữ liệu trên Firebase
        blogRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Lấy thông tin của blog từ dataSnapshot
                String title = dataSnapshot.child("Title").getValue(String.class);
                String content = dataSnapshot.child("Content").getValue(String.class);
                String imageUrl = dataSnapshot.child("Image").getValue(String.class);

                // Hiển thị thông tin của blog trong layout
                txtTitle.setText(title);
                txtContent.setText(content);
                // Sử dụng Picasso để tải hình ảnh từ URL và hiển thị vào ImageView
                Picasso.get().load(imageUrl).into(imgBlog);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        });

        imgButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng Activity hiện tại và quay lại Activity trước đó (BlogCategoryActivity)
            }
        });
    }
}
