package com.group01.plantique.java;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.group01.plantique.R;
import com.group01.plantique.adapter.ProductAdapter;
import com.group01.plantique.model.Product;

import java.util.ArrayList;
import java.util.List;

public class SearchProductActivity extends AppCompatActivity {
    SearchView svSearchProduct;
    RecyclerView rvProductList;
    TextView emptyView;
    ProductAdapter productAdapter;
    ArrayList<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_product);
        addViews();

    }

    private void addViews() {
        emptyView=findViewById(R.id.emptyView);
        svSearchProduct=findViewById(R.id.svSearchProduct);
        rvProductList=findViewById(R.id.rvProductList);

        // Khởi tạo adapter và gán vào RecyclerView
        productAdapter = new ProductAdapter(this, productList);
        rvProductList.setAdapter(productAdapter);

        // Thiết lập sự kiện tìm kiếm
        svSearchProduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Xử lý tìm kiếm khi người dùng nhấn nút tìm kiếm trên bàn phím
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Xử lý tìm kiếm khi người dùng thay đổi văn bản trong ô tìm kiếm
                performDynamicSearch(newText);
                return true;
            }
        });
    }

    private void performSearch(String query) {
        // Thực hiện tìm kiếm sản phẩm dựa trên query và cập nhật RecyclerView

        // Ví dụ: Xử lý khi người dùng chọn một sản phẩm
        productAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                // Chuyển sang SearchResultActivity khi người dùng chọn một sản phẩm
                Intent intent = new Intent(SearchProductActivity.this, SearchResultActivity.class);
                intent.putExtra("PRODUCT", product);
                startActivity(intent);
            }
        });
    }

    private void performDynamicSearch(String newText) {
        // Thực hiện tìm kiếm sản phẩm động dựa trên newText và cập nhật RecyclerView
    }
}


