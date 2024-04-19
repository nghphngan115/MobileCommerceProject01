package com.group01.plantique.java;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group01.plantique.adapter.ProductAdapter;
import com.group01.plantique.model.Product;

import java.util.ArrayList;
import java.util.List;

import com.group01.plantique.R;

public class HomeScreenActivity extends AppCompatActivity {
    SearchView svSearch;
    Button btnMuangay, btnViewAll, btnViewAll2;
    RecyclerView rvCategory;

    List<Product> productList;
    ProductAdapter productAdapter;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        svSearch = findViewById(R.id.svSearch);
        btnMuangay = findViewById(R.id.btnMuaNgay);
        btnViewAll = findViewById(R.id.btnViewAll);
        btnViewAll2 = findViewById(R.id.btnViewAll2);
        rvCategory = findViewById(R.id.rvCategory);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);
        rvCategory.setLayoutManager(new LinearLayoutManager(this));
        rvCategory.setAdapter(productAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("products");

        svSearch.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một Intent để chuyển từ HomeScreenActivity sang SearchProductActivity
                Intent intent = new Intent(HomeScreenActivity.this, SearchActivity.class);
                // Bắt đầu SearchProductActivity
                startActivity(intent);
            }
        });

// Bạn có thể giữ nguyên phần này nếu muốn xử lý khi người dùng thay đổi query
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Không cần xử lý khi submit vì đã chuyển màn hình khi bấm vào khung search
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Bạn có thể giữ nguyên hoặc xóa phương thức này, vì sẽ không cần thiết khi đã chuyển màn hình
                return false;
            }
        });

        fetchProductsFromFirebase(); // Gọi phương thức để tải sản phẩm từ Firebase
    }

    private void fetchProductsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    productList.add(product);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(HomeScreenActivity.this, "Failed to read value from Firebase.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
