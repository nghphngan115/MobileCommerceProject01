package com.group01.plantique.adapter;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group01.plantique.R;
import com.group01.plantique.java.OrderDetailsActivity;
import com.group01.plantique.model.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orders;
    private LayoutInflater inflater;

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.txtOrderId.setText(order.getOrderId());
        holder.txtTotalAmount.setText(String.format("%s đ", order.getTotalCost()));
        String orderStatus = order.getOrderStatus();

        // Set text based on status string resource
        int statusResId = getStatusStringResource(orderStatus);
        holder.txtStatus.setText(statusResId);

        // Setting text color based on order status
        if (orderStatus.equals("Processing")) {
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.processing));
        } else if (orderStatus.equals("Delivering")) {
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.delivering));
        } else if (orderStatus.equals("Finished")) {
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.finished));
        } else if (orderStatus.equals("Cancelled")) {
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.cancelled));
        }

        try {
            long timestamp = Long.parseLong(order.getOrderDate());
            String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(new Date(timestamp));
            holder.txtOrderDate.setText(formattedDate);
        } catch (NumberFormatException e) {
            holder.txtOrderDate.setText("Unknown date");
            Log.e("OrderAdapter", "Failed to parse date: " + order.getOrderDate(), e);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("order", order);  // Ensure Order is Serializable or Parcelable
            context.startActivity(intent);
        });
    }

    private int getStatusStringResource(String orderStatus) {
        switch (orderStatus) {
            case "Processing":
                return R.string.processing_status;
            case "Delivering":
                return R.string.delivering_status;
            case "Finished":
                return R.string.finished_status;
            case "Cancelled":
                return R.string.cancelled_status;
            default:
                return R.string.default_status; // Default string resource if status is not recognized
        }
    }


    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtTotalAmount, txtOrderDate, txtStatus;
        ImageView imageView;

        public OrderViewHolder(View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtTotalAmount = itemView.findViewById(R.id.txtTotalAmount);
            txtOrderDate = itemView.findViewById(R.id.txtOrderDate);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            imageView = itemView.findViewById(R.id.imageView13);
        }
    }
}
