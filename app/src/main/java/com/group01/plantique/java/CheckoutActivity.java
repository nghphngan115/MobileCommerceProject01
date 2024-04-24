package com.group01.plantique.java;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.group01.plantique.R;
import com.group01.plantique.adapter.CartListAdapter;
import com.group01.plantique.model.Product;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity implements CartListAdapter.OnQuantityChangeListener {
    private ConstraintLayout btnConfirm;
    private RadioButton radTransfer, radCOD;
    private TextView txtSubTotal;
    private ListView lvProduct;
    private ArrayList<Product> cartProducts;
    private CartListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        btnConfirm = findViewById(R.id.btnConfirm);
        txtSubTotal = findViewById(R.id.txtSubTotal);
        radTransfer = findViewById(R.id.radTransfer);
        radCOD = findViewById(R.id.radCOD);
        lvProduct = findViewById(R.id.lvProduct);

        cartProducts = getProductListFromIntent();
        adapter = new CartListAdapter(this, cartProducts);
        adapter.setOnQuantityChangeListener(this);
        lvProduct.setAdapter(adapter);

        // Update total based on passed total amount from Intent
        updateTotal();

        btnConfirm.setOnClickListener(v -> confirmOrder());
    }
    private ArrayList<Product> getProductListFromIntent() {
        Gson gson = new Gson();
        String json = getIntent().getStringExtra("productListJson");
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void confirmOrder() {
        EditText edtFullName = findViewById(R.id.edtFullname);
        EditText edtAddress = findViewById(R.id.edtAddress);
        EditText edtWard = findViewById(R.id.edtWard);
        EditText edtDistrict = findViewById(R.id.edtDistrict);
        EditText edtCity = findViewById(R.id.edtCity);
        EditText edtPhone = findViewById(R.id.edtVoucher);
        EditText edtEmail = findViewById(R.id.edtEmail);
        RadioGroup radGroupPayment = findViewById(R.id.radGroupPayment);

        int selectedId = radGroupPayment.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Please select a payment method.", Toast.LENGTH_LONG).show();
            return;
        }

        RadioButton radioButton = findViewById(selectedId);
        String paymentMethod = radioButton.getText().toString();

        if (!areFieldsValid(edtFullName, edtAddress, edtWard, edtDistrict, edtCity, edtPhone, edtEmail)) {
            return; // Stop if validation fails
        }

        String fullAddress = String.format("%s, %s, %s, %s", edtAddress.getText().toString(), edtWard.getText().toString(),
                edtDistrict.getText().toString(), edtCity.getText().toString());

        Gson gson = new Gson();
        String productListJson = gson.toJson(cartProducts);

        Intent intent = new Intent(CheckoutActivity.this, OrderConfirmActivity.class);
        intent.putExtra("FULL_NAME", edtFullName.getText().toString());
        intent.putExtra("ADDRESS", fullAddress);
        intent.putExtra("PHONE", edtPhone.getText().toString());
        intent.putExtra("EMAIL", edtEmail.getText().toString());
        intent.putExtra("PAYMENT_METHOD", paymentMethod);
        intent.putExtra("productListJson", productListJson);
        intent.putExtra("totalAmount", txtSubTotal.getText().toString());

        startActivity(intent);
    }



    @Override
    public void onQuantityChange() {
        updateTotal();
    }

    private void updateTotal() {
        int total = 0;
        for (Product product : cartProducts) {
            total += product.getPrice() * product.getStock(); // Assumes getStock returns current cart quantity
        }
        txtSubTotal.setText(String.format("%d đ", total));
    }

    private boolean areFieldsValid(EditText... fields) {
        for (EditText field : fields) {
            if (field.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "All fields are required.", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return isValidPhone(fields[fields.length - 2].getText().toString().trim()) &&
                isValidEmail(fields[fields.length - 1].getText().toString().trim());
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }
}
