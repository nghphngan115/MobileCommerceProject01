package com.group01.plantique.java;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.group01.plantique.adapter.PromotionAdapter;
import com.group01.plantique.model.Promotion;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.rxjava3.annotations.NonNull;

public class AdminPromotionActivity extends AppCompatActivity {

    private ImageButton btnBack, btnAddPromotion, searchBtn, filterBtn;
    private EditText searchEt;
    private TextView filterTv;
    private RecyclerView recyclerViewPromotions;
    private ArrayList<Promotion> promotionArrayList;
    private PromotionAdapter promotionAdapter;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_promotion);

        // Ánh xạ các thành phần từ layout XML vào activity
        filterTv = findViewById(R.id.filterTv);
        btnBack = findViewById(R.id.btnBack);
        btnAddPromotion = findViewById(R.id.btnAddPromotion);
        searchBtn = findViewById(R.id.searchBtn);
        filterBtn = findViewById(R.id.filterBtn);
        searchEt = findViewById(R.id.searchEt);


        recyclerViewPromotions = findViewById(R.id.recyclerViewPromotions);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewPromotions.setLayoutManager(layoutManager);

        firebaseAuth = FirebaseAuth.getInstance();
        loadAllPromoCodes();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnAddPromotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminPromotionActivity.this, AddPromotionCodeActivity.class));
            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog();
            }
        });
    }

    private void filterDialog() {
        String[] options = {"All", "Expired", "Not Expired"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Promotion Codes")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (i == 0) {
                            filterTv.setText("All Promotion Codes");
                            loadAllPromoCodes();
                        } else if (i == 1) {
                            filterTv.setText("Expired Promotion Codes");
                            loadExpiredPromoCodes();
                        } else if (i == 2) {
                            filterTv.setText("No Expired Promotion Codes");
                            loadNoExpiredPromoCodes();
                        }
                    }
                })
                .show();
    }

    private void loadAllPromoCodes() {
        promotionArrayList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("promotions");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                promotionArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Promotion promotion = ds.getValue(Promotion.class);
                    if (promotion != null) {
                        promotionArrayList.add(promotion);
                    }
                }
                // Khởi tạo adapter và set adapter cho RecyclerView
                promotionAdapter = new PromotionAdapter(AdminPromotionActivity.this, promotionArrayList);
                recyclerViewPromotions.setAdapter(promotionAdapter);
                promotionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi
                Toast.makeText(AdminPromotionActivity.this, "Failed to load promotions: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadExpiredPromoCodes() {
        DecimalFormat mFormat = new DecimalFormat("00");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String todayDate = day + "/" + month + "/" + year;

        promotionArrayList = new ArrayList<>(); // Khởi tạo lại ArrayList

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("promotions");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                promotionArrayList.clear(); // Xóa dữ liệu cũ

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Promotion promotion = ds.getValue(Promotion.class);
                    if (promotion != null) {
                        String expDate = promotion.getExpireDate();

                        // Kiểm tra xem mã đã hết hạn chưa
                        if (isExpired(expDate)) {
                            promotionArrayList.add(promotion);
                        }
                    }
                }

                // Cập nhật adapter sau khi đã lọc xong
                promotionAdapter = new PromotionAdapter(AdminPromotionActivity.this, promotionArrayList);
                recyclerViewPromotions.setAdapter(promotionAdapter);
                promotionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi
                Toast.makeText(AdminPromotionActivity.this, "Failed to load promotions: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadNoExpiredPromoCodes() {
        DecimalFormat mFormat = new DecimalFormat("00");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String todayDate = day + "/" + month + "/" + year;

        promotionArrayList = new ArrayList<>(); // Khởi tạo lại ArrayList

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("promotions");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                promotionArrayList.clear(); // Xóa dữ liệu cũ

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Promotion promotion = ds.getValue(Promotion.class);
                    if (promotion != null) {
                        String expDate = promotion.getExpireDate();

                        // Kiểm tra xem mã chưa hết hạn
                        if (!isExpired(expDate)) {
                            promotionArrayList.add(promotion);
                        }
                    }
                }

                // Cập nhật adapter sau khi đã lọc xong
                promotionAdapter = new PromotionAdapter(AdminPromotionActivity.this, promotionArrayList);
                recyclerViewPromotions.setAdapter(promotionAdapter);
                promotionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi
                Toast.makeText(AdminPromotionActivity.this, "Failed to load promotions: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức kiểm tra xem mã có hết hạn không
    private boolean isExpired(String expDate) {
        try {
            SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
            Date currentDate = new Date(); // Ngày hiện tại
            Date expireDate = sdformat.parse(expDate);

            // So sánh ngày hết hạn với ngày hiện tại
            return expireDate != null && expireDate.compareTo(currentDate) < 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}