package com.group01.plantique.java;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.group01.plantique.R;
import com.group01.plantique.adapter.CartListAdapter;
import com.group01.plantique.model.Product;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class OrderConfirmActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ArrayList<Product> productList;
    private TextView txtPromoDescription, txtFullName, txtAddress, txtPhone, txtEmail, txtPaymentMethod, txtTotal, txtSubTotal, txtShipFee, txtNote;
    private ConstraintLayout btnConfirm;
    private ListView lvProduct;
    private CartListAdapter cartListAdapter;
    public EditText edtVoucher;
    private static final int SHIPPING_FEE = 30000; // Shipping fee constant

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        initializeFirebase();
        initializeViews();
        productList = getCartFromSharedPreferences();
        setupListAdapter();
        updateTotal();
        populateDataFromIntent();
    }

    private void initializeFirebase() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("allorders");
    }

    private void initializeViews() {
        txtFullName = findViewById(R.id.txtFullname);
        txtAddress = findViewById(R.id.txtAddress);
        txtPhone = findViewById(R.id.txtPhone);
        txtEmail = findViewById(R.id.txtEmail);
        txtPaymentMethod = findViewById(R.id.txtPaymentMethod);
        txtSubTotal = findViewById(R.id.txtSubTotal);
        txtShipFee = findViewById(R.id.txtShipFee);
        txtNote=findViewById(R.id.txtNote);
        txtTotal = findViewById(R.id.txtTotal);
        lvProduct = findViewById(R.id.lvProduct);
        btnConfirm = findViewById(R.id.btnConfirm);
        txtPromoDescription = findViewById(R.id.txtPromoDescription);
        btnConfirm.setOnClickListener(v -> finalizeOrder());
        txtShipFee.setText(String.format("%d đ", SHIPPING_FEE));
        edtVoucher = findViewById(R.id.edtVoucher);
    }

    private void setupListAdapter() {
        cartListAdapter = new CartListAdapter(this, productList);
        lvProduct.setAdapter(cartListAdapter);
        // Thiết lập trình lắng nghe sự thay đổi số lượng
        cartListAdapter.setOnQuantityChangeListener(() -> updateTotal());
    }

    private void updateTotal() {
        int subTotal = 0;
        for (Product product : productList) {
            subTotal += product.getPrice() * product.getCartQuantity();
        }
        txtSubTotal.setText(String.format("%d đ", subTotal));
        int total = subTotal + SHIPPING_FEE;
        txtTotal.setText(String.format("%d đ", total));
        saveCartToSharedPreferences();
    }
    private void saveCartToSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(productList);
        sharedPreferences.edit().putString("cart", json).apply();
    }
    private ArrayList<Product> getCartFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("CartPrefs", MODE_PRIVATE);
        String json = sharedPreferences.getString("cart", null);
        if (json == null) {
            return new ArrayList<>(); // Return an empty list if nothing is found
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        return gson.fromJson(json, type); // Convert the JSON string back to a List of Product
    }
    private void finalizeOrder() {
        String userId = getUserIdFromSharedPreferences();
        if (userId == null) {
            Toast.makeText(this, "User ID is not available, please sign in again.", Toast.LENGTH_LONG).show();
            return;
        }

        // Assuming you have already populated productList with the items in the cart
        // Update stock for each product in the order
        for (Product product : productList) {
            updateProductStock(product.getProductId(), product.getCartQuantity());
        }

        // After updating stock, push order details to Firebase
        pushOrderToFirebase();
    }
    private void pushOrderToFirebase() {
        String userId = getUserIdFromSharedPreferences();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String orderId = timestamp;
        String subTotal = txtSubTotal.getText().toString().replace("đ", "").trim();
        String totalCost = txtTotal.getText().toString().replace("đ", "").trim();

        HashMap<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("orderId", orderId);
        orderInfo.put("orderDate", timestamp);
        orderInfo.put("orderStatus", "Processing");
        orderInfo.put("orderNote", txtNote.getText().toString());
        orderInfo.put("fullName", txtFullName.getText().toString());
        orderInfo.put("subTotal", subTotal);
        orderInfo.put("orderBy", userId);
        orderInfo.put("shippingFee", SHIPPING_FEE);
        orderInfo.put("totalCost", totalCost);
        orderInfo.put("address", txtAddress.getText().toString());
        orderInfo.put("email", txtEmail.getText().toString());
        orderInfo.put("phone", txtPhone.getText().toString());
        orderInfo.put("paymentMethod", txtPaymentMethod.getText().toString());

        DatabaseReference orderRef = databaseReference.child(userId).child("Orders").child(timestamp);
        orderRef.setValue(orderInfo)
                .addOnSuccessListener(aVoid -> {
                    processOrderItems(orderRef);
                    showOrderConfirmationDialog(orderId);
                    sendOrderConfirmationNotification(timestamp, new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
                    // After successfully writing order data, update stock
                    for (Product product : productList) {
                        updateProductStock(product.getProductId(), product.getCartQuantity());
                    }
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to push order data", e));
    }

        private void processOrderItems(DatabaseReference orderRef) {
        for (Product product : productList) {
            HashMap<String, Object> itemMap = new HashMap<>();
            itemMap.put("productId", product.getProductId());
            itemMap.put("productName", product.getProductName());
            itemMap.put("price", product.getPrice());
            itemMap.put("cartQuantity", product.getCartQuantity());
            itemMap.put("imageurl",product.getImageurl());
            itemMap.put("discount_price", product.getDiscount_price());

            orderRef.child("Items").child(product.getProductId()).setValue(itemMap);
            updateProductStock(product.getProductId(), product.getCartQuantity());
        }
    }

    private void updateProductStock(String productId, int quantityOrdered) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("products").child(productId).child("stock");

        productRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer stock = currentData.getValue(Integer.class);
                if (stock == null || stock < quantityOrdered) {
                    return Transaction.abort();
                }
                currentData.setValue(stock - quantityOrdered);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                if (committed) {
                    Log.d("Firebase", "Stock successfully updated for product ID: " + productId);
                } else {
                    Log.e("Firebase", "Failed to update stock for product ID: " + productId);
                    if (databaseError != null) {
                        Log.e("Firebase", "Error updating stock: " + databaseError.getMessage(), databaseError.toException());
                    }
                }
            }
        });
    }



    private void showOrderConfirmationDialog(String orderId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmation, null);
        builder.setView(dialogView);

        TextView txtOrderIdConfirm = dialogView.findViewById(R.id.txtOrderIdConfirm);
        txtOrderIdConfirm.setText("Mã đơn hàng: " + orderId);

        ConstraintLayout btnBack = dialogView.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finishHomeScreen());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void finishHomeScreen() {
        Intent intent = new Intent(this, HomeScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private String getUserIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        return sharedPreferences.getString("userID", null);
    }
    private void populateDataFromIntent() {
        Intent intent = getIntent();
        String fullname = intent.getStringExtra("FULL_NAME");
        String address = intent.getStringExtra("ADDRESS");
        String email = intent.getStringExtra("EMAIL");
        String phone = intent.getStringExtra("PHONE");
        String paymentMethod = intent.getStringExtra("PAYMENT_METHOD");
        String note=intent.getStringExtra("NOTE");
        txtFullName.setText(fullname != null ? fullname : "N/A");
        txtAddress.setText(address != null ? address : "N/A");
        txtEmail.setText(email != null ? email : "N/A");
        txtPhone.setText(phone != null ? phone : "N/A");
        txtPaymentMethod.setText(paymentMethod != null ? paymentMethod : "N/A");
        if (note != null) {
            txtNote.setText(note);
        } else {
            txtNote.setVisibility(View.GONE);
        }

    }
    private void sendOrderConfirmationNotification(String orderId, String orderDate) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "order_confirmation_channel";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Order Confirmation",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Notifications for order confirmations");
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, OrderHistoryActivity.class);
       // Pass order ID to handle in the destination activity

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_IMMUTABLE; // Adding FLAG_IMMUTABLE for Android 12 and above
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, flags);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification) // replace ic_notification with your notification icon
                .setContentTitle("Order Placed Successfully")
                .setContentText("You have successfully placed order " + orderId + " on " + orderDate)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }






}

