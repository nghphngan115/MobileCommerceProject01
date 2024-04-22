package com.group01.plantique.java;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.group01.plantique.R;
import com.group01.plantique.model.Product;

import java.util.ArrayList;

public class SearchProductActivity extends AppCompatActivity {
    SearchView svSearchProduct;
    RecyclerView rvProductList;
    TextView emptyView;

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




    }



    private void performDynamicSearch(String newText) {
        // Thực hiện tìm kiếm sản phẩm động dựa trên newText và cập nhật RecyclerView
    }
}


