package com.group01.plantique.java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class AddPromotionCodeActivity extends AppCompatActivity {

    private EditText promoCodeEt, promoDescriptionEt, promoPriceEt, minimumOrderPriceEt;
    private TextView expireDateTv, titleTv;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    private String promoId;
    private boolean isUpdating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_promotion_code);

        // Ánh xạ các view
        promoCodeEt = findViewById(R.id.promoCodeEt);
        promoDescriptionEt = findViewById(R.id.promoDescriptionEt);
        promoPriceEt = findViewById(R.id.promoPriceEt);
        minimumOrderPriceEt = findViewById(R.id.minimumOrderPriceEt);
        expireDateTv = findViewById(R.id.expireDateTv);
        titleTv = findViewById(R.id.titleTv);
        Button addBtn = findViewById(R.id.addBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);

        Intent intent = getIntent();
        if (intent.getStringExtra("promoId") != null) {
            promoId = intent.getStringExtra("promoId");
            titleTv.setText(getString(R.string.update_promotion_title));
            addBtn.setText(getString(R.string.update_button));

            isUpdating = true;

            loadPromoInfo();
        } else {
            titleTv.setText(getString(R.string.add_promotion_title));
            addBtn.setText(getString(R.string.add_button));

            isUpdating = false;
        }


        // Xử lý sự kiện khi nút "Add Promotion Code" được click
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });

        // Xử lý sự kiện khi nút "Back" được click
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Đóng activity hiện tại khi nút "Back" được click
            }
        });
        expireDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                datePickDialog();
            }
        });
    }

    private void loadPromoInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("promotions");
        reference.child(promoId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot snapshot) {
                String id = ""+snapshot.child("id").getValue();
                String description =""+snapshot.child("description").getValue();
                String promoCode = ""+snapshot.child("promoCode").getValue();
                String promoPrice = ""+snapshot.child("promoPrice").getValue();
                String minimumOrderPrice = ""+snapshot.child("minimumOrder").getValue();
                String expireDate = ""+snapshot.child("expireDate").getValue();

                promoCodeEt.setText(promoCode);
                promoDescriptionEt.setText(description);
                promoPriceEt.setText(promoPrice);
                minimumOrderPriceEt.setText(minimumOrderPrice);
                expireDateTv.setText(expireDate);
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error) {

            }
        });

    }

    private void datePickDialog() {
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                DecimalFormat mFormat = new DecimalFormat("00");
                String pDay= mFormat.format(dayOfMonth);
                String pMonth =mFormat.format(monthOfYear+1);
                String pYear =""+year;
                String pDate = pDay + "/"+pMonth+"/"+pYear;

                expireDateTv.setText(pDate);
            }
        },mYear, mMonth, mDay);

        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
    }
    private String promoCode, promoDescription, promoPrice, minOrderPrice, expireDate;
    private void inputData() {
        // Lấy thông tin từ các EditText
        String promoCode = promoCodeEt.getText().toString().trim();
        String promoDescription = promoDescriptionEt.getText().toString().trim();
        String promoPrice = promoPriceEt.getText().toString().trim();
        String minOrderPrice = minimumOrderPriceEt.getText().toString().trim();
        String expireDate = expireDateTv.getText().toString().trim();

        if (TextUtils.isEmpty(promoCode)){
            Toast.makeText(this, getString(R.string.enter_discount_code), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(promoDescription)){
            Toast.makeText(this, getString(R.string.enter_promotion_description), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(promoPrice)){
            Toast.makeText(this, getString(R.string.enter_promotion_price), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(minOrderPrice)){
            Toast.makeText(this, getString(R.string.enter_minimum_order_price), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(expireDate)){
            Toast.makeText(this, getString(R.string.choose_expired_date), Toast.LENGTH_SHORT).show();
            return;
        }
        if (isUpdating){
            updateDataToDb();
        }
        else {
            addDatatoDb();
        }


    }

    private void updateDataToDb() {
        progressDialog.setMessage(getString(R.string.updating_promotion));
        progressDialog.show();

        // Lấy thông tin từ các EditText và TextView
        String promoCode = promoCodeEt.getText().toString().trim();
        String promoDescription = promoDescriptionEt.getText().toString().trim();
        String promoPrice = promoPriceEt.getText().toString().trim();
        String minOrderPrice = minimumOrderPriceEt.getText().toString().trim();
        String expireDate = expireDateTv.getText().toString().trim();

        // Tạo HashMap để lưu dữ liệu cập nhật
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("description", promoDescription);
        hashMap.put("promoCode", promoCode);
        hashMap.put("promoPrice", promoPrice);
        hashMap.put("minimumOrderPrice", minOrderPrice);
        hashMap.put("expireDate", expireDate);

        DatabaseReference promotionsRef = FirebaseDatabase.getInstance().getReference("promotions");
        promotionsRef.child(promoId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(AddPromotionCodeActivity.this, getString(R.string.promotion_updated), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK); // Đặt kết quả thành công
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        String errorMessage = getString(R.string.promotion_update_failed, e.getMessage());
                        Toast.makeText(AddPromotionCodeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void addDatatoDb() {
        progressDialog.setMessage(getString(R.string.adding_promotion));
        progressDialog.show();

        // Gọi phương thức để lấy số tiếp theo và truyền callback để xử lý
        getNextCodeId(new NextCodeIdCallback() {
            @Override
            public void onNextCodeId(int nextCodeId) {
                // Tiếp tục với logic thêm dữ liệu và xử lý kết quả
                addDataWithNextCodeId(nextCodeId);
                setResult(RESULT_OK);
                finish();
            }
        });

    }

    // Phương thức để thêm dữ liệu với số tiếp theo trong chuỗi ID
    private void addDataWithNextCodeId(int nextCodeId) {
        // Sử dụng số tiếp theo để thêm dữ liệu vào Firebase
        String newPromoId = "CodeId_" + nextCodeId;

        // Lấy thông tin từ các EditText và TextView
        promoCode = promoCodeEt.getText().toString().trim();
        promoDescription = promoDescriptionEt.getText().toString().trim();
        promoPrice = promoPriceEt.getText().toString().trim();
        minOrderPrice = minimumOrderPriceEt.getText().toString().trim();
        expireDate = expireDateTv.getText().toString().trim();

        // Kiểm tra xem các trường có đầy đủ thông tin không
        if (TextUtils.isEmpty(promoCode) || TextUtils.isEmpty(promoDescription) || TextUtils.isEmpty(promoPrice) ||
                TextUtils.isEmpty(minOrderPrice) || TextUtils.isEmpty(expireDate)) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        // Tạo HashMap để lưu dữ liệu
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", newPromoId);
        hashMap.put("description", promoDescription);
        hashMap.put("promoCode", promoCode);
        hashMap.put("promoPrice", promoPrice);
        hashMap.put("minimumOrderPrice", minOrderPrice);
        hashMap.put("expireDate", expireDate);

        DatabaseReference promotionsRef = FirebaseDatabase.getInstance().getReference().child("promotions");
        // Đẩy dữ liệu lên Firebase
        promotionsRef.child(newPromoId).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(AddPromotionCodeActivity.this, getString(R.string.promotion_added_success), Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK); // Đặt kết quả thành công
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddPromotionCodeActivity.this, "Failed to add Promotion Code", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Phương thức để lấy số tiếp theo trong chuỗi ID
    public interface NextCodeIdCallback {
        void onNextCodeId(int nextCodeId);
    }

    private void getNextCodeId(NextCodeIdCallback callback) {
        DatabaseReference promotionsRef = FirebaseDatabase.getInstance().getReference().child("promotions");

        Query lastQuery = promotionsRef.orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int nextCodeId = 1; // Giá trị mặc định
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String lastKey = snapshot.getKey();
                        int lastCodeId = Integer.parseInt(lastKey.substring(lastKey.indexOf("_") + 1));
                        nextCodeId = lastCodeId + 1;
                    }
                }
                // Gọi callback để truyền giá trị nextCodeId
                callback.onNextCodeId(nextCodeId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi
                Toast.makeText(AddPromotionCodeActivity.this, getString(R.string.failed_get_promo_id), Toast.LENGTH_SHORT).show();
            }
        });
    }



}
