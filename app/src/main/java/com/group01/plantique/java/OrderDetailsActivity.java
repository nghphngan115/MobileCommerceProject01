package com.group01.plantique.java;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.R;
import com.group01.plantique.adapter.ProductOrderAdapter;
import com.group01.plantique.model.Order;
import com.group01.plantique.model.Product;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderDetailsActivity extends AppCompatActivity {
    private TextView txtStatus, txtFullname, txtAddress, txtOrderId, txtUserId, txtEmail, txtPhone,txtNote, txtSubTotal, txtShipFee, txtPaymentMethod, txtDiscount, txtTotal;
    private ListView lvProduct;
    private Button btnAction;
    private Order order;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        initializeViews();
        populateOrderDetails();

    }

    private void initializeViews() {
        txtStatus = findViewById(R.id.txtStatus);
        txtFullname = findViewById(R.id.txtFullname);
        txtAddress = findViewById(R.id.txtAddress);
        txtEmail = findViewById(R.id.txtEmail);
        txtOrderId = findViewById(R.id.txtOrderId);
        txtPhone = findViewById(R.id.txtPhone);
        txtUserId = findViewById(R.id.txtUserId);
        txtSubTotal = findViewById(R.id.txtSubTotal);
        txtShipFee = findViewById(R.id.txtShipFee);
        txtPaymentMethod = findViewById(R.id.txtPaymentMethod);
        txtDiscount = findViewById(R.id.txtDiscount);
        txtTotal = findViewById(R.id.txtTotal);
        lvProduct = findViewById(R.id.lvProduct);
        txtNote=findViewById(R.id.txtNote);

        btnAction = findViewById(R.id.btnAction);

    }
    private void cancelOrder() {
        if (order != null && order.getOrderId() != null && order.getOrderBy() != null) {
            order.setOrderStatus("Cancelled");
            updateOrderStatusInFirebase();
        } else {
            Log.e("OrderDetailsActivity", "Order or essential fields are null.");
            Toast.makeText(this, getString(R.string.order_incomplete), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateOrderStatusInFirebase() {
        if (order.getOrderBy() != null && order.getOrderId() != null) {
            DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("allorders")
                    .child(order.getOrderBy()) // Sử dụng getOrderBy()
                    .child("Orders")
                    .child(order.getOrderId()); // Sử dụng getOrderId()


            orderRef.child("orderStatus").setValue("Cancelled")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(OrderDetailsActivity.this, getString(R.string.order_cancelled), Toast.LENGTH_SHORT).show();
                        txtStatus.setText("Cancelled");
                    })
                    .addOnFailureListener(e -> Toast.makeText(OrderDetailsActivity.this, getString(R.string.cancel_order_failed), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, getString(R.string.missing_information), Toast.LENGTH_SHORT).show();
        }
    }

    private void markOrderAsFinished() {
        if (order != null && order.getOrderId() != null && order.getOrderBy() != null) {
            DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("allorders")
                    .child(order.getOrderBy())
                    .child("Orders")
                    .child(order.getOrderId());

            orderRef.child("orderStatus").setValue("Finished")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(OrderDetailsActivity.this, getString(R.string.order_marked_finished), Toast.LENGTH_SHORT).show();
                        txtStatus.setText("Finished");
                        updateButtonBasedOnStatus(); // Update the button again
                    })
                    .addOnFailureListener(e -> Toast.makeText(OrderDetailsActivity.this, getString(R.string.update_order_failed), Toast.LENGTH_SHORT).show());
        }
    }

    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.cancel_confirmation_title))
                .setMessage(getString(R.string.cancel_confirmation_message))
                .setPositiveButton(getString(R.string.strYes), (dialog, which) -> cancelOrder())
                .setNegativeButton(getString(R.string.strNo), (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void showFinishConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.strReceviedConf))
                .setMessage(getString(R.string.strReceivedMes))
                .setPositiveButton(getString(R.string.strYes), (dialog, which) -> markOrderAsFinished())
                .setNegativeButton(getString(R.string.strNo), (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void updateButtonBasedOnStatus() {
        if (order != null) {
            String status = order.getOrderStatus();

            switch (status) {
                case "Processing":
                    btnAction.setText(getString(R.string.strCancelOrder));
                    btnAction.setBackgroundTintList(getResources().getColorStateList(R.color.cancelled));
                    btnAction.setOnClickListener(v -> showCancelConfirmationDialog());
                    break;
                case "Delivering":
                    btnAction.setText(getString(R.string.strReceived));
                    btnAction.setBackgroundTintList(getResources().getColorStateList(R.color.delivering));

                    btnAction.setOnClickListener(v -> showFinishConfirmationDialog());
                    break;
                case "Finished":
                    btnAction.setText("Writing Review");
                    btnAction.setBackgroundTintList(getResources().getColorStateList(R.color.finished));

                    // Xử lý thêm sự kiện cho việc viết đánh giá ở đây
                    btnAction.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (order != null && order.getItems() != null && !order.getItems().isEmpty()) {
                                HashMap<String, Product> products = order.getItems();

                                // Kiểm tra xem có ít nhất một sản phẩm trong đơn hàng
                                if (!products.isEmpty()) {
                                    // Lấy productId của sản phẩm đầu tiên
                                    String productId = products.keySet().iterator().next();

                                    // Tạo một Intent mới để chuyển đến WriteReviewActivity
                                    Intent intent = new Intent(OrderDetailsActivity.this, WriteReviewActivity.class);
                                    intent.putExtra("productId", productId);
                                    startActivity(intent);
                                } else {
                                    // Xử lý trường hợp không có sản phẩm nào trong đơn hàng
                                    Toast.makeText(OrderDetailsActivity.this, "No products found in the order!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Xử lý trường hợp order hoặc order.getItems() là null hoặc rỗng
                                Toast.makeText(OrderDetailsActivity.this, "Order or items are null or empty!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    break;
                case "Cancelled":
                    btnAction.setVisibility(View.GONE); // Ẩn nút
                    break;
                default:
                    btnAction.setVisibility(View.GONE); // Ẩn nút nếu trạng thái không xác định
                    break;
            }
        }
    }


    private void populateOrderDetails() {
        order = (Order) getIntent().getSerializableExtra("order");
        if (order != null) {
            txtOrderId.setText(order.getOrderId());
            txtStatus.setText(order.getOrderStatus());
            switch (order.getOrderStatus()) {
                case "Processing":
                    txtStatus.setTextColor(getResources().getColor(R.color.processing));
                    break;
                case "Delivering":
                    txtStatus.setTextColor(getResources().getColor(R.color.delivering));
                    break;
                case "Finished":
                    txtStatus.setTextColor(getResources().getColor(R.color.finished));
                    break;
                case "Cancelled":
                    txtStatus.setTextColor(getResources().getColor(R.color.cancelled));
                    break;
                default:
                    // Màu mặc định khi trạng thái không xác định
                    txtStatus.setTextColor(getResources().getColor(android.R.color.black));
                    break;
            }
            txtFullname.setText(order.getFullName());
            txtAddress.setText(order.getAddress());
            txtUserId.setText(order.getOrderBy());
            txtEmail.setText(order.getEmail());
            txtPhone.setText(order.getPhone());
            txtNote.setText(order.getOrderNote());
            txtSubTotal.setText(String.format("%s đ", order.getSubTotal()));
            txtShipFee.setText(String.format("%s đ", order.getShippingFee()));
            txtPaymentMethod.setText(order.getPaymentMethod());
            txtDiscount.setText(order.getDiscount());
            txtTotal.setText(String.format("%s đ", order.getTotalCost()));

            DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference("allorders")
                    .child(order.getOrderBy())
                    .child("Orders")
                    .child(order.getOrderId())
                    .child("Items");

            itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<Product> products = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Product product = snapshot.getValue(Product.class);
                        Log.d("ProductData", "Product Name: " + product.getProductName() + ", Cart Quantity: " + product.getCartQuantity());
                        products.add(product);
                    }

                    ProductOrderAdapter adapter = new ProductOrderAdapter(OrderDetailsActivity.this, products);
                    lvProduct.setAdapter(adapter);
                    runOnUiThread(() -> updateButtonBasedOnStatus());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("OrderDetailsActivity", "Failed to read products.", databaseError.toException());
                }
            });
        } else {
            Log.e("OrderDetailsActivity", "Failed to retrieve the order object.");
            Toast.makeText(this, getString(R.string.strFailedLoadOrder), Toast.LENGTH_SHORT).show();
        }
    }
}
