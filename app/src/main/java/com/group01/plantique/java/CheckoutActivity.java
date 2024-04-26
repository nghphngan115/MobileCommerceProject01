package com.group01.plantique.java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.group01.plantique.R;
import com.group01.plantique.adapter.CartListAdapter;
import com.group01.plantique.model.Product;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity {
    ConstraintLayout btnConfirm;

    RadioButton radTransfer, radCOD;
    TextView txtSubTotal;
    ListView lvProduct;
    ArrayList<Product> cartProducts;
    private CartListAdapter cartListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        initViews();
        cartProducts = getCartFromSharedPreferences();
        updateSubTotal();

        cartListAdapter = new CartListAdapter(this, cartProducts);
        lvProduct.setAdapter(cartListAdapter);
        cartListAdapter.setOnQuantityChangeListener(new CartListAdapter.OnQuantityChangeListener() {
            @Override
            public void onQuantityChanged() {
                updateSubTotal();
                saveCartToSharedPreferences();
            }
        });

        addEvents();
    }

    private void initViews() {
        btnConfirm = findViewById(R.id.btnConfirm);
        txtSubTotal = findViewById(R.id.txtSubTotal);


        radTransfer = findViewById(R.id.radTransfer);
        radCOD = findViewById(R.id.radCOD);
        lvProduct = findViewById(R.id.lvProduct);
    }


    private void updateSubTotal() {
        int totalAmount = 0;
        for (Product product : cartProducts) {
            totalAmount += product.getPrice() * product.getCartQuantity();
        }
        txtSubTotal.setText(String.format("%d đ", totalAmount));
        SharedPreferences sharedPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE);
        sharedPreferences.edit().putInt("totalAmount", totalAmount).apply();
    }

    private void saveCartToSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(cartProducts);
        sharedPreferences.edit().putString("cart", json).apply();
    }
    private ArrayList<Product> getCartFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE);
        String json = sharedPreferences.getString("cart", null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        return gson.fromJson(json, type);
    }



    private void addEvents() {
         btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    Toast.makeText(CheckoutActivity.this, "Please select a payment method.", Toast.LENGTH_LONG).show();
                    return;
                }

                RadioButton radioButton = findViewById(selectedId);
                String paymentMethod = radioButton.getText().toString();

                if (!areFieldsValid(edtFullName, edtAddress, edtWard, edtDistrict, edtCity, edtPhone, edtEmail)) {
                    return; // Stop the function if validation fails
                }

                // Combine address components into one string
                String fullAddress = edtAddress.getText().toString() + ", " +
                        edtWard.getText().toString() + ", " +
                        edtDistrict.getText().toString() + ", " +
                        edtCity.getText().toString();

                Gson gson = new Gson();
                String productListJson = gson.toJson(cartProducts);

                // Prepare intent for passing data to ConfirmationActivity
                Intent intent = new Intent(CheckoutActivity.this, OrderConfirmActivity.class);
                intent.putExtra("FULL_NAME", edtFullName.getText().toString());
                intent.putExtra("ADDRESS", fullAddress);
                intent.putExtra("PHONE", edtPhone.getText().toString());
                intent.putExtra("EMAIL", edtEmail.getText().toString());
                intent.putExtra("PAYMENT_METHOD", paymentMethod);
                intent.putExtra("productListJson", productListJson);
                intent.putExtra("totalAmount", txtSubTotal.getText().toString());

                // Start ConfirmationActivity
                startActivity(intent);
            }
        });
    }
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }

    private boolean areFieldsValid(EditText edtFullName, EditText edtAddress, EditText edtWard,
                                   EditText edtDistrict, EditText edtCity, EditText edtPhone, EditText edtEmail) {
        // Check if any field is empty
        if (edtFullName.getText().toString().trim().isEmpty() ||
                edtAddress.getText().toString().trim().isEmpty() ||
                edtWard.getText().toString().trim().isEmpty() ||
                edtDistrict.getText().toString().trim().isEmpty() ||
                edtCity.getText().toString().trim().isEmpty() ||
                edtPhone.getText().toString().trim().isEmpty() ||
                edtEmail.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check if the phone number is valid
        if (!isValidPhone(edtPhone.getText().toString().trim())) {
            Toast.makeText(this, "Phone number must be exactly 10 digits.", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check if the email is valid
        if (!isValidEmail(edtEmail.getText().toString().trim())) {
            Toast.makeText(this, "Email is not valid.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }




}


