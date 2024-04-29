package com.group01.plantique.java;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.group01.plantique.model.NotificationApp;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class OrderConfirmActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private ArrayList<Product> productList;
    private TextView txtDiscount, txtPromoDescription, txtFullName, txtAddress, txtPhone, txtEmail, txtPaymentMethod, txtTotal, txtSubTotal, txtShipFee, txtNote;
    private ConstraintLayout btnConfirm, btnApply;
    private ListView lvProduct;
    private CartListAdapter cartListAdapter;
    public EditText edtVoucher;
    private FloatingActionButton validateBtn;
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

        validateBtn.setOnClickListener(v -> {
            String promotionCode = edtVoucher.getText().toString();
            checkCodeAvailability(promotionCode);
        });

        btnConfirm.setOnClickListener(v -> finalizeOrder());

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
        validateBtn = findViewById(R.id.validateBtn);
        btnApply = findViewById(R.id.btnApply);
        txtDiscount = findViewById(R.id.txtDiscount);
    }
    private boolean isPromoCodeApplied =false;
    public String promoId, promoCode, promoDescription, promoMinimumOrderPrice, promoPrice, promoExpDate;
    private void checkCodeAvailability(String promotionCode) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Checking Promo Code...");
        progressDialog.setCanceledOnTouchOutside(false);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("promotions");
        ref.orderByChild("promoCode").equalTo(promotionCode)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        boolean isPromoCodeFound = false;
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                promoId = "" + ds.child("id").getValue();
                                promoCode = "" + ds.child("promoCode").getValue();
                                promoDescription = "" + ds.child("description").getValue();
                                promoExpDate = "" + ds.child("expireDate").getValue();
                                promoMinimumOrderPrice = "" + ds.child("minimumOrderPrice").getValue();
                                promoPrice = "" + ds.child("promoPrice").getValue();

                                // Kiểm tra tính khả dụng của mã
                                boolean isAvailable = checkCodeExpireDate() && checkMinimumOrderPrice();

                                if (isAvailable) {
                                    isPromoCodeFound = true;
                                    applyPromoCode();
                                    break;
                                } else {
                                    // Nếu không tìm thấy mã hợp lệ hoặc không thỏa mãn điều kiện
                                    isPromoCodeApplied = false;
                                    // Ẩn nút Apply và mô tả khuyến mãi
                                    btnApply.setVisibility(View.GONE);
                                    txtPromoDescription.setVisibility(View.GONE);
                                    txtPromoDescription.setText("");
                                    priceWithoutDiscount(); // Gọi khi không áp dụng giảm giá
                                }
                            }
                        } if (!isPromoCodeFound) {
                            Toast.makeText(OrderConfirmActivity.this, "Promo code has not existed or has expired", Toast.LENGTH_SHORT).show();
                            isPromoCodeApplied = false;
                            // Ẩn nút Apply và mô tả khuyến mãi
                            btnApply.setVisibility(View.GONE);
                            txtPromoDescription.setVisibility(View.GONE);
                            txtPromoDescription.setText("");
                            priceWithoutDiscount(); // Gọi khi không áp dụng giảm giá
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressDialog.dismiss();
                        Toast.makeText(OrderConfirmActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void applyPromoCode() {
        if (!isPromoCodeApplied) {
            isPromoCodeApplied = true;
            btnApply.setVisibility(View.GONE);
            txtPromoDescription.setVisibility(View.VISIBLE);
            txtPromoDescription.setText(promoDescription);
            priceWithDiscount();
        } else {
            Toast.makeText(this, "Promo code has already been applied", Toast.LENGTH_SHORT).show();
        }
    }



    private void priceWithDiscount() {
        try {
            double currentPrice = Double.parseDouble(txtTotal.getText().toString().replaceAll("[^\\d.]+", ""));
            double promoPriceValue = 0; // Giá trị mặc định
            if (promoPrice != null && !promoPrice.equals("null")) {
                promoPriceValue = Double.parseDouble(promoPrice);
            }
            double finalPrice = currentPrice - promoPriceValue;
            txtDiscount.setText(String.format("%d đ", (int)promoPriceValue));
            txtTotal.setText(String.format("%d đ", (int)finalPrice));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // Xử lý nếu không thể chuyển đổi thành số
        }
    }


    private void priceWithoutDiscount() {
        try {
            double currentPrice = Double.parseDouble(txtTotal.getText().toString().replaceAll("[^\\d.]+", ""));
            double finalPrice = currentPrice;
            txtDiscount.setText(String.format("%d đ", 0));
            txtTotal.setText(String.format("%d đ", (int)finalPrice));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // Xử lý nếu không thể chuyển đổi thành số
        }
    }



    private boolean checkCodeExpireDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String todayDate = day + "/" + month + "/" + year;
        SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        try {
            Date currentDate = sdformat.parse(todayDate);
            Date expireDate = sdformat.parse(promoExpDate != null ? promoExpDate : "01/01/1900");

            return expireDate.after(currentDate) || expireDate.equals(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error checking promo code expiration", Toast.LENGTH_SHORT).show();
            return false; // Mặc định trả về false nếu có lỗi xảy ra
        }
    }


    private boolean checkMinimumOrderPrice() {
        double currentPrice = Double.parseDouble(txtTotal.getText().toString().replaceAll("[^\\d.]+", ""));
        if (promoMinimumOrderPrice != null && !promoMinimumOrderPrice.equals("null")) {
            double minimumOrderPriceValue = Double.parseDouble(promoMinimumOrderPrice);
            return currentPrice >= minimumOrderPriceValue;
        } else {
            // Xử lý khi promoMinimumOrderPrice là null
            // Ví dụ: Báo lỗi hoặc không áp dụng điều kiện
            return false;
        }
    }


    private void setupListAdapter() {
        cartListAdapter = new CartListAdapter(this, productList);
        lvProduct.setAdapter(cartListAdapter);
        cartListAdapter.setOnQuantityChangeListener(this::updateTotal);
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
        String orderId = generateOrderId();
        String paymentMethod = txtPaymentMethod.getText().toString();
        String transferMethod = getString(R.string.strTransfer); // Get the transfer method from resources

        if (transferMethod.equals(paymentMethod)) {
            showPaymentConfirmationDialog(orderId);
        } else {
            // For COD or any other methods, finalize the order immediately


            // After updating stock, push order details to Firebase
            pushOrderToFirebase(orderId);
            isPromoCodeApplied = false;}
    }
    private String generateOrderId() {
        return String.valueOf(System.currentTimeMillis());
    }
    private void showPaymentConfirmationDialog(String orderId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_bank_transfer, null);
        builder.setView(dialogView);

        // Get the total amount from the TextView in your activity
        String totalAmount = txtTotal.getText().toString();


        // Set the total amount on the dialog's TextView
        TextView txtDialogTotal = dialogView.findViewById(R.id.txtTotal);
        txtDialogTotal.setText(totalAmount);
        TextView txtDialogOrderId = dialogView.findViewById(R.id.txtOrderId);
        txtDialogOrderId.setText(orderId);

        // Setup the button to confirm payment
        ConstraintLayout btnCompleted = dialogView.findViewById(R.id.btnCompleted);
        btnCompleted.setOnClickListener(v -> {
            // Logic to confirm payment, perhaps update some status or push data to Firebase
            pushOrderToFirebase(orderId);
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false); // This will make it so the dialog cannot be cancelled
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        // Set the size of the dialog to be 90% of the screen width
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);
    }


    private void pushOrderToFirebase(String orderId) {
        String userId = getUserIdFromSharedPreferences();
        if (userId == null) {
            Log.e("FirebasePush", "User ID is null, cannot push order.");
            return;
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
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
        orderInfo.put("discount", txtDiscount.getText().toString());

        DatabaseReference orderRef = databaseReference.child(userId).child("Orders").child(orderId);
        orderRef.setValue(orderInfo)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebasePush", "Order data successfully pushed to Firebase.");
                    processOrderItems(orderRef);
                    showOrderConfirmationDialog(orderId);
                    sendOrderConfirmationNotification(timestamp, new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
                    for (Product product : productList) {
                        updateProductStock(product.getProductId(), product.getCartQuantity());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebasePush", "Failed to push order data: " + e.getMessage(), e);
                    Toast.makeText(OrderConfirmActivity.this, "Failed to save order: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
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



    private AlertDialog confirmationDialog; // Biến để lưu trữ tham chiếu đến dialog

    private void showOrderConfirmationDialog(String orderId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmation, null);
        builder.setView(dialogView);

        TextView txtOrderIdConfirm = dialogView.findViewById(R.id.txtOrderIdConfirm);

        txtOrderIdConfirm.setText(getString(R.string.strOrdID) + orderId);


        ConstraintLayout btnBack = dialogView.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finishHomeScreen());

        confirmationDialog = builder.create();
        confirmationDialog.setCancelable(false); // This will make it so the dialog cannot be cancelled
        confirmationDialog.setCanceledOnTouchOutside(false); // Lưu tham chiếu đến dialog vào biến confirmationDialog
        confirmationDialog.show();
    }

    private void finishHomeScreen() {
        if (confirmationDialog != null && confirmationDialog.isShowing()) {
            confirmationDialog.dismiss(); // Đảm bảo rằng dialog được đóng trước khi kết thúc Activity
        }
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
                .setContentTitle(getString(R.string.strOrderSuccess))
                .setContentText(getString(R.string.strNotiOrder) + orderId + getString(R.string.strOn) + orderDate)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
        saveNotificationToPreferences(getString(R.string.strOrderSuccess), getString(R.string.strNotiOrder) + orderId + getString(R.string.strOn) + orderDate);

    }
    private void saveNotificationToPreferences(String title, String content) {
        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("notifications", null);
        Type type = new TypeToken<ArrayList<NotificationApp>>() {}.getType();
        List<NotificationApp> notifications = gson.fromJson(json, type);

        if (notifications == null) notifications = new ArrayList<>();
        notifications.add(new NotificationApp(title, content, R.drawable.ic_notification)); // Assuming an icon

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("notifications", gson.toJson(notifications));
        editor.apply();
    }

    private List<NotificationApp> loadNotifications() {
        SharedPreferences sharedPreferences = getSharedPreferences("NotificationPrefs", MODE_PRIVATE);
        String json = sharedPreferences.getString("notifications", null);
        Type type = new TypeToken<ArrayList<NotificationApp>>() {}.getType();
        List<NotificationApp> notifications = new Gson().fromJson(json, type);
        return notifications != null ? notifications : new ArrayList<>();
    }








}

