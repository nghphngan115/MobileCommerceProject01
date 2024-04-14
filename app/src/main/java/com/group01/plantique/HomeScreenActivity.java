package com.group01.plantique;

import android.os.Bundle;
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

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    displayAllItems();
                } else {
                    performDynamicSearch(newText);
                }
                return true;
            }
        });

        fetchProductsFromFirebase();
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

    private void performSearch(String query) {
        // Your search logic here
        Toast.makeText(this, "Search query: " + query, Toast.LENGTH_SHORT).show();
    }

    private void performDynamicSearch(String newText) {
        // Your dynamic search logic here
        Toast.makeText(this, "Dynamic search: " + newText, Toast.LENGTH_SHORT).show();
    }

    private void displayAllItems() {
        // Your logic to display all items
        Toast.makeText(this, "Displaying all items", Toast.LENGTH_SHORT).show();
    }
}
