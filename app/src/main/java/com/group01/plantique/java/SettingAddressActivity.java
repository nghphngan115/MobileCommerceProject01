package com.group01.plantique.java;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.group01.plantique.R;
import com.group01.plantique.adapter.ShippingAddressAdapter;
import com.group01.plantique.databinding.ActivityAboutUsBinding;
import com.group01.plantique.databinding.ActivityPolicyBinding;
import com.group01.plantique.databinding.ActivitySettingAddressBinding;
import com.group01.plantique.model.ShippingAddress;

import java.util.ArrayList;

public class SettingAddressActivity  extends DrawerBaseActivity {
    ActivitySettingAddressBinding activitySettingAddressBinding;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private EditText edtFullName, edtAddress1, edtDistrict, edtProvince, edtPhoneNumber, edtWard;
    private ListView lvAddress;
    private ImageButton imgbtnAddAddress, imgbtnBack;
    private ArrayList<ShippingAddress> shippingAddressList;
    private ShippingAddressAdapter adapter;
    private ConstraintLayout btnSave;
    private Switch switchSaveDefault;
    private String defaultAddressId;
    private boolean isInputFieldVisible = false;
    private String userID; // Lưu userID tạm thời ở đây


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingAddressBinding= ActivitySettingAddressBinding.inflate(getLayoutInflater());
        setContentView(activitySettingAddressBinding.getRoot());
        allocateActivityTitle(getString(R.string.nav_shipping_address));
        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        userID = sharedPreferences.getString("userID", "");
        // Khởi tạo defaultAddressId từ SharedPreferences (nếu có)
        defaultAddressId = sharedPreferences.getString("defaultAddressId", "");

        // Kiểm tra xem userID có tồn tại không
        if (userID.isEmpty()) {
            Toast.makeText(this, getString(R.string.user_id_not_found), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SettingAddressActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
                .child(userID)
                .child("shipping_addresses");

        // Khai báo các view
        edtFullName = findViewById(R.id.edtFullName);
        edtAddress1 = findViewById(R.id.edtAddress1);
        edtDistrict = findViewById(R.id.edtDistrict);
        edtProvince = findViewById(R.id.edtProvince);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        lvAddress = findViewById(R.id.lvAddress);
        btnSave = findViewById(R.id.btnSave);
        imgbtnAddAddress = findViewById(R.id.imgbtnAddAddress);
        edtWard = findViewById(R.id.edtWard);
        switchSaveDefault = findViewById(R.id.switchSaveDefault);
        imgbtnBack = findViewById(R.id.imgBack);

        // Khởi tạo danh sách địa chỉ và adapter
        shippingAddressList = new ArrayList<>();
        adapter = new ShippingAddressAdapter(this, R.layout.layout_address_item, shippingAddressList);
        lvAddress.setAdapter(adapter);

        // Xử lý sự kiện khi nhấn nút "Lưu"
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrUpdateAddress();
            }
        });
        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // Xử lý sự kiện khi nhấn nút "Thêm địa chỉ"
        imgbtnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layoutInputFields = findViewById(R.id.layoutInputFields);
                layoutInputFields.setVisibility(isInputFieldVisible ? View.GONE : View.VISIBLE);
                isInputFieldVisible = !isInputFieldVisible;
            }
        });

        // Xử lý sự kiện khi chọn một địa chỉ từ danh sách
        lvAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShippingAddress selectedAddress = shippingAddressList.get(position);
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedAddress", new Gson().toJson(selectedAddress));
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });


        // Xử lý sự kiện khi giữ lâu trên một địa chỉ trong danh sách
        lvAddress.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteConfirmationDialog(position);
                return true;
            }
        });



        // Xử lý sự kiện khi thay đổi switchSaveDefault
        switchSaveDefault.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                int defaultPosition = adapter.getDefaultAddressPosition();
                if (defaultPosition >= 0 && defaultPosition < shippingAddressList.size()) {
                    defaultAddressId = shippingAddressList.get(defaultPosition).getAddressId();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("defaultAddressId", defaultAddressId);
                    editor.apply();
                } else {
                    Toast.makeText(this, getString(R.string.invalid_default_address), Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadAddresses();
    }

    private void loadAddresses() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shippingAddressList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ShippingAddress shippingAddress = dataSnapshot.getValue(ShippingAddress.class);
                    shippingAddressList.add(shippingAddress);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingAddressActivity.this, getString(R.string.failed_to_load_addresses), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addOrUpdateAddress() {
        String fullName = edtFullName.getText().toString().trim();
        String address1 = edtAddress1.getText().toString().trim();
        String district = edtDistrict.getText().toString().trim();
        String province = edtProvince.getText().toString().trim();
        String phoneNumber = edtPhoneNumber.getText().toString().trim();
        String ward = edtWard.getText().toString().trim();

        if (fullName.isEmpty() || address1.isEmpty() || district.isEmpty() || province.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userAddressRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userID)
                .child("shipping_addresses");

        String addressId = userAddressRef.push().getKey(); // Tạo một addressId mới
        ShippingAddress address = new ShippingAddress(addressId, ward, fullName, address1, district, province, phoneNumber);

        if (addressId != null) {
            userAddressRef.child(addressId).setValue(address);
            Toast.makeText(this, getString(R.string.address_saved_success), Toast.LENGTH_SHORT).show();
        }
        if (switchSaveDefault.isChecked()) {
            // Kiểm tra nếu đã tồn tại địa chỉ mặc định khác
            if (!defaultAddressId.isEmpty() && !defaultAddressId.equals(address.getAddressId())) {
                // Báo lỗi vì chỉ được lưu một địa chỉ mặc định
                Toast.makeText(this, getString(R.string.only_choose_one_default), Toast.LENGTH_SHORT).show();
                return;
            }
            // Lưu địa chỉ mặc định vào SharedPreferences và cập nhật defaultAddressId
            defaultAddressId = address.getAddressId();
            SharedPreferences.Editor editor = getSharedPreferences("userData", MODE_PRIVATE).edit();
            editor.putString("defaultAddressId", defaultAddressId);
            editor.apply();
        }

        clearFields();
    }

    private void populateAddressFields(ShippingAddress shippingAddress) {
        edtFullName.setText(shippingAddress.getFullName());
        edtAddress1.setText(shippingAddress.getAddress1());
        edtWard.setText(shippingAddress.getWard());
        edtDistrict.setText(shippingAddress.getDistrict());
        edtProvince.setText(shippingAddress.getProvince());
        edtPhoneNumber.setText(shippingAddress.getPhoneNumber());
    }

    private void deleteAddress(int position) {
        ShippingAddress shippingAddress = shippingAddressList.get(position);
        databaseReference.child(shippingAddress.getAddressId()).removeValue();
        Toast.makeText(this, getString(R.string.address_deleted_success), Toast.LENGTH_SHORT).show();
    }

    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_confirmation_message))
                .setPositiveButton(getString(R.string.delete_button_text), (dialog, which) -> deleteAddress(position))
                .setNegativeButton(getString(R.string.cancel_button_text), (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void clearFields() {
        edtFullName.setText("");
        edtAddress1.setText("");
        edtDistrict.setText("");
        edtProvince.setText("");
        edtPhoneNumber.setText("");
        edtWard.setText("");
    }
}
