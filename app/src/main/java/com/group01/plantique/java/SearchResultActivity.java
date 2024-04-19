package com.group01.plantique.java;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group01.plantique.R;
import com.group01.plantique.adapter.ProductAdapter;
import com.group01.plantique.model.Product;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {
    TextView txtSearchResult;
    GridView gvResult;
    Product product;
    List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_result);
        addViews();
        addEvents();

    }

    private void addViews() {
        txtSearchResult=findViewById(R.id.txtSearchResult);
        gvResult=findViewById(R.id.gvResult);
        ArrayAdapter<Product> adapter = new ArrayAdapter<>(this, R.layout.highlighted_product, productList);
        gvResult.setAdapter(adapter);
    }


    private void addEvents() {
        displayProducts();
    }

    private void displayProducts() {
        product = getIntent().getParcelableExtra("PRODUCT");

        // Kiểm tra xem product có null hay không
        if (product != null) {
            // Hiển thị thông tin chi tiết của sản phẩm
            txtSearchResult.setText(product.getProductname());
            // Đây là nơi bạn muốn sử dụng Adapter để hiển thị danh sách sản phẩm trong GridView
            // Ví dụ:
            ArrayList<Product> productList = new ArrayList<>();
            productList.add(product); // Thêm sản phẩm vào danh sách
    }

}}