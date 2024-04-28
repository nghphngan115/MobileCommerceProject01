package com.group01.plantique.java;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.group01.plantique.R;
import com.group01.plantique.adapter.AddressAdapter;
import com.group01.plantique.model.Address;

import java.util.ArrayList;

public class SettingAddressActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private EditText edtFullName, edtAddress1, edtDistrict, edtProvince, edtPhoneNumber;
    private ListView lvAddress;
    private ImageButton imgbtnAddAddress;
    private ArrayList<Address> addressList;
    private AddressAdapter adapter;
    private ConstraintLayout btnSave;
    private boolean isInputFieldVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_address);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
                .child(currentUser.getUid())
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

        // Khởi tạo danh sách địa chỉ và adapter
        addressList = new ArrayList<>();
        adapter = new AddressAdapter(this, R.layout.layout_address_item, addressList);
        lvAddress.setAdapter(adapter);

        // Xử lý sự kiện khi nhấn nút "Lưu"
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrUpdateAddress();
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
                Address selectedAddress = addressList.get(position);
                populateAddressFields(selectedAddress);
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

        // Load danh sách địa chỉ từ Firebase
        loadAddresses();
    }

    // Phương thức để load danh sách địa chỉ từ Firebase
    private void loadAddresses() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addressList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Address address = dataSnapshot.getValue(Address.class);
                    addressList.add(address);
                }
                adapter.notifyDataSetChanged(); // Cập nhật danh sách trong adapter
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

        if (fullName.isEmpty() || address1.isEmpty() || district.isEmpty() || province.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        String userID = sharedPreferences.getString("userID", "");

        DatabaseReference userAddressRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userID)
                .child("shipping_addresses");

        String addressId = userAddressRef.push().getKey(); // Tạo một addressId mới
        Address address = new Address(addressId, fullName, address1, district, province, phoneNumber);

        if (addressId != null) {
            userAddressRef.child(addressId).setValue(address);
            Toast.makeText(this, getString(R.string.address_saved_success), Toast.LENGTH_SHORT).show();
        }

        clearFields();
    }



    // Phương thức để hiển thị thông tin của một địa chỉ lên các input fields
    private void populateAddressFields(Address address) {
        edtFullName.setText(address.getFullName());
        edtAddress1.setText(address.getAddress1());
        edtDistrict.setText(address.getDistrict());
        edtProvince.setText(address.getProvince());
        edtPhoneNumber.setText(address.getPhoneNumber());
    }

    // Phương thức để xóa một địa chỉ
    private void deleteAddress(int position) {
        Address address = addressList.get(position);
        databaseReference.child(address.getAddressId()).removeValue();
        Toast.makeText(this, getString(R.string.address_deleted_success), Toast.LENGTH_SHORT).show();
    }

    // Phương thức để hiển thị dialog xác nhận xóa địa chỉ
    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_confirmation_message))
                .setPositiveButton(getString(R.string.delete_button_text), (dialog, which) -> deleteAddress(position))
                .setNegativeButton(getString(R.string.cancel_button_text), (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // Phương thức để xóa dữ liệu trên các input fields
    private void clearFields() {
        edtFullName.setText("");
        edtAddress1.setText("");
        edtDistrict.setText("");
        edtProvince.setText("");
        edtPhoneNumber.setText("");
    }
}
