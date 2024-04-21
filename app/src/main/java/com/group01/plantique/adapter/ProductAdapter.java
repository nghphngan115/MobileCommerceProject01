package com.group01.plantique.adapter;
//để test cho homescreen, sau này có chính thức thì có thể xóa bản này
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.group01.plantique.R;
import com.group01.plantique.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Filterable {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }
    private Context context;
    private List<Product> productList;
    private List<Product> productListFiltered; // Danh sách được lọc sau khi tìm kiếm

    // Constructor
    public ProductAdapter(Context context, List<Product> productList) {
        super();
        this.context = context;
        this.productList = productList;
        this.productListFiltered = productList;
    }

    // ViewHolder class
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgProductPic;
        public TextView txtProductname;
        public TextView txtPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProductPic = itemView.findViewById(R.id.imgProductPic);
            txtProductname = itemView.findViewById(R.id.txtProductname);
            txtPrice = itemView.findViewById(R.id.txtPrice);

        }
    }

    // Phương thức này sẽ được gọi từ bên ngoài để đặt người nghe sự kiện
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_on_search, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productListFiltered.get(position);
        holder.txtProductname.setText(product.getProductname());
        holder.txtPrice.setText(product.getPrice());
        Picasso.get().load(product.getImageUrl()).into(holder.imgProductPic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(product);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    productListFiltered = productList;
                } else {
                    List<Product> filteredList = new ArrayList<>();
                    for (Product row : productList) {
                        // Kiểm tra xem tên sản phẩm có chứa chuỗi tìm kiếm không
                        if (row.getProductname().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    productListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = productListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                productListFiltered = (ArrayList<Product>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    // Phương thức này sẽ trả về danh sách sản phẩm đã lọc
    public List<Product> getProductListFiltered() {
        return productListFiltered;
    }
}
