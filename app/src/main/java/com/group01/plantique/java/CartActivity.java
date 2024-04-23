package com.group01.plantique.java;

        import android.content.Intent;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;
        import androidx.annotation.Nullable;
        import androidx.constraintlayout.widget.ConstraintLayout;

        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import java.util.ArrayList;
        import com.group01.plantique.R;
        import com.group01.plantique.adapter.CartListAdapter;
        import com.group01.plantique.model.Product;

public class CartActivity extends DrawerBaseActivity {

    private ListView lvCart;
    private TextView txtTotalCart;
    private ArrayList<Product> cartProducts;
    private CartListAdapter cartListAdapter;
    private DatabaseReference productsRef;
    private ConstraintLayout btnContinue;
    private ConstraintLayout btnOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        lvCart = findViewById(R.id.lvCart);
        txtTotalCart = findViewById(R.id.txtTotalCart);
        btnContinue = findViewById(R.id.btnContinue);
        btnOrder = findViewById(R.id.btnOrder);

        // Khởi tạo danh sách sản phẩm trong giỏ hàng
        cartProducts = new ArrayList<>();

        // Lấy tham chiếu đến cơ sở dữ liệu Firebase
        productsRef = FirebaseDatabase.getInstance().getReference().child("products");

        // Thực hiện lắng nghe sự kiện khi dữ liệu thay đổi
        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Duyệt qua danh sách sản phẩm
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    // Lấy thông tin về sản phẩm
                    String productId = productSnapshot.getKey();
                    String productName = productSnapshot.child("productName").getValue(String.class);
                    String description = productSnapshot.child("description").getValue(String.class);
                    int price = productSnapshot.child("price").getValue(Integer.class);
                    int discountPrice = productSnapshot.child("discount_price").getValue(Integer.class);
                    String imageUrl = productSnapshot.child("imageurl").getValue(String.class);
                    String categoryId = productSnapshot.child("categoryId").getValue(String.class);
                    int quantity = productSnapshot.child("quantity").getValue(Integer.class); // Lấy số lượng từ Firebase

                    // Tạo đối tượng sản phẩm với số lượng
                    Product product = new Product(productId, productName, description, price, discountPrice, imageUrl, categoryId, quantity);

                    // Thêm sản phẩm vào giỏ hàng
                    cartProducts.add(product);
                }

                // Hiển thị danh sách sản phẩm trong giỏ hàng
                showCart();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
                Log.e("Firebase", "Error fetching data", databaseError.toException());
                Toast.makeText(CartActivity.this, "Lỗi khi lấy dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
            }
        });
        // Thiết lập sự kiện click cho nút "Tiếp tục mua hàng"
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở ProductListActivity khi click vào nút "Tiếp tục mua hàng"
                startActivity(new Intent(CartActivity.this, ProductListActivity.class));
            }
        });

        // Thiết lập sự kiện click cho nút "Đặt hàng"
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở ListOrderActivity khi click vào nút "Đặt hàng"
                startActivity(new Intent(CartActivity.this, ListOrderActivity.class));
            }
        });
    }

    // Phương thức để hiển thị danh sách sản phẩm trong giỏ hàng
    private void showCart() {
        // Kiểm tra nếu giỏ hàng trống
        if (cartProducts.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set adapter cho ListView
        cartListAdapter = new CartListAdapter(this, cartProducts);
        lvCart.setAdapter(cartListAdapter);

        // Tính toán và hiển thị tổng giá trị của giỏ hàng
        int totalCartValue = calculateTotalCartValue(cartProducts);
        txtTotalCart.setText(String.valueOf(totalCartValue) + " đ");
    }

    // Phương thức để cập nhật số lượng sản phẩm trong giỏ hàng
    private void updateQuantity(Product product, int newQuantity) {
        // Lấy số lượng có sẵn từ Firebase
        int availableQuantity = product.getQuantity(); // Đây là giá trị từ Firebase

        // Kiểm tra xem newQuantity có hợp lệ không
        if (newQuantity >= 0 && newQuantity <= availableQuantity) {
            // Cập nhật số lượng sản phẩm trong giỏ hàng
            product.setQuantity(newQuantity);
            // Hiển thị lại danh sách sản phẩm trong giỏ hàng sau khi cập nhật
            showCart();
        } else {
            // Hiển thị thông báo hoặc xử lý lỗi khi newQuantity không hợp lệ
            Toast.makeText(this, "Số lượng không hợp lệ hoặc vượt quá số lượng có sẵn", Toast.LENGTH_SHORT).show();
        }
    }

    // Phương thức để tính toán tổng giá trị của giỏ hàng
    private int calculateTotalCartValue(ArrayList<Product> products) {
        int total = 0;
        for (Product product : products) {
            total += product.getPrice() * product.getQuantity();
        }
        return total;
    }

    // Phương thức để thêm sản phẩm vào giỏ hàng
    private void addToCart(Product product) {
        // Mặc định số lượng là 1 khi thêm mới sản phẩm vào giỏ hàng
        product.setQuantity(1);
        cartProducts.add(product); // Thêm sản phẩm vào danh sách giỏ hàng
        showCart(); // Hiển thị lại danh sách sản phẩm trong giỏ hàng sau khi thêm mới
    }
}
