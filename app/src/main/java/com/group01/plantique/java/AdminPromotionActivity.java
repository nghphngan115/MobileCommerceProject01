package com.group01.plantique.java;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.google.firebase.database.Query;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class AdminPromotionActivity extends AppCompatActivity {

    private static final int ADD_PROMO_REQUEST_CODE = 100;
    private static final int EDIT_PROMO_REQUEST_CODE = 200;
    private static final int DELETE_PROMO_REQUEST_CODE = 300;
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

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý trước khi thay đổi văn bản
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Khi nội dung của searchEt thay đổi, gọi phương thức searchPromoCode()
                searchPromoCode();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần xử lý sau khi thay đổi văn bản
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnAddPromotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPromotionActivity.this, AddPromotionCodeActivity.class);
                startActivityForResult(intent, ADD_PROMO_REQUEST_CODE);
            }
        });


        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog();
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi phương thức để tìm kiếm mã khuyến mãi
                searchPromoCode();
            }
        });
    }
    private void searchPromoCode() {
        String searchQuery = searchEt.getText().toString().trim();

        // Kiểm tra xem người dùng đã nhập mã khuyến mãi cần tìm hay chưa
        if (TextUtils.isEmpty(searchQuery)) {
            // Hiển thị thông báo yêu cầu nhập mã khuyến mãi
            loadAllPromoCodes();
        }

        // Thực hiện tìm kiếm mã khuyến mãi
        if (filterTv.getText().toString().equals(getString(R.string.all_promo_codes))) {
            performSearchAll(searchQuery);
        } else {
            // Thực hiện tìm kiếm mã khuyến mãi dựa trên bộ lọc đã chọn
            performSearch(searchQuery);
        }
    }
    private void performSearchAll(String searchQuery) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("promotions");
        Query searchQueryRef = reference.orderByChild("promoCode").equalTo(searchQuery);
        searchQueryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                promotionArrayList.clear(); // Xóa dữ liệu cũ
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Promotion promotion = ds.getValue(Promotion.class);
                    if (promotion != null) {
                        promotionArrayList.add(promotion);
                    }
                }
                // Cập nhật danh sách và thông báo cho adapter
                promotionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi
                Toast.makeText(AdminPromotionActivity.this, "Failed to search promotions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch(String searchQuery) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("promotions");
        Query searchQueryRef = reference.orderByChild("promoCode");

        // Tạo query để tìm kiếm chữ thường hoặc chữ hoa
        searchQueryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                promotionArrayList.clear(); // Xóa dữ liệu cũ
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Promotion promotion = ds.getValue(Promotion.class);
                    if (promotion != null) {
                        // Lấy promoCode từ Firebase và chuyển về chữ thường để so sánh
                        String promoCode = promotion.getPromoCode().toLowerCase();
                        if (promoCode.contains(searchQuery.toLowerCase())) {
                            promotionArrayList.add(promotion);
                        }
                    }
                }
                // Cập nhật danh sách và thông báo cho adapter
                promotionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi
                Toast.makeText(AdminPromotionActivity.this, "Failed to search promotions", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void filterDialog() {
        String[] options = getResources().getStringArray(R.array.filter_promo_options);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.filter_promo_title)
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (i == 0) {
                            filterTv.setText(getString(R.string.all_promo_codes));
                            loadAllPromoCodes();
                        } else if (i == 1) {
                            filterTv.setText(getString(R.string.expired_promo_codes));
                            loadExpiredPromoCodes();
                        } else if (i == 2) {
                            filterTv.setText(getString(R.string.no_expired_promo_codes));
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
                // Cập nhật lại adapter sau khi load xong
                if (promotionAdapter != null) {
                    promotionAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi
                Toast.makeText(AdminPromotionActivity.this,
                        getString(R.string.toast_load_promotions_failed, error.getMessage()),
                        Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AdminPromotionActivity.this,
                        getString(R.string.toast_load_promotions_failed, error.getMessage()),
                        Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AdminPromotionActivity.this,
                        getString(R.string.toast_load_promotions_failed, error.getMessage()),
                        Toast.LENGTH_SHORT).show();

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_PROMO_REQUEST_CODE || requestCode == EDIT_PROMO_REQUEST_CODE || requestCode == DELETE_PROMO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Nếu nhận được kết quả thành công, load lại danh sách mã khuyến mãi
                loadAllPromoCodes(); // Hoặc phương thức tương ứng để load lại danh sách
            } else {
                // Xử lý nếu có lỗi hoặc hành động không thành công
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(promoDeletedReceiver, new IntentFilter("com.group01.plantique.promo_deleted"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(promoDeletedReceiver);
    }

    private BroadcastReceiver promoDeletedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Load lại danh sách mã khuyến mãi khi nhận được thông báo xóa thành công
            loadAllPromoCodes(); // Hoặc phương thức tương ứng để load lại danh sách
        }
    };
}