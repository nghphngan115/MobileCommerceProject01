package com.group01.plantique.adapter;

import static com.group01.plantique.java.AdminOrderListActivity.REQUEST_CODE;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group01.plantique.R;
import com.group01.plantique.java.AdminOrderDetailsActivity;
import com.group01.plantique.java.OrderDetailsActivity;
import com.group01.plantique.model.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {
    private List<Order> ordersList;
    private LayoutInflater inflater;
    // In AdminOrderListActivity
    // Or any other unique integer.


    public AdminOrderAdapter(Context context, List<Order> ordersList) {
        this.inflater = LayoutInflater.from(context);
        this.ordersList = ordersList;
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        Order order = ordersList.get(position);
        holder.txtOrderId.setText(order.getOrderId());
        holder.txtTotalAmount.setText(String.format("%s đ", (order.getTotalCost() != null ? order.getTotalCost() : "N/A")));

        holder.txtOrderDate.setText((order.getOrderDate() != null ? convertTimestampToDate(Long.parseLong(order.getOrderDate())) : "N/A"));
        holder.txtStatus.setText(order.getOrderStatus() != null ? order.getOrderStatus() : "Unknown");

        // Corrected the reference to txtStatus to holder.txtStatus
        String orderStatus = order.getOrderStatus() != null ? order.getOrderStatus() : "Unknown";
        Context context = holder.itemView.getContext(); // Getting context from the itemView
        if (orderStatus.equals("Processing")) {
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.processing));
        } else if (orderStatus.equals("Delivering")) {
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.delivering));
        } else if (orderStatus.equals("Finished")) {
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.finished));
        } else if (orderStatus.equals("Cancelled")) {
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.cancelled));
        }
        holder.itemView.setOnClickListener(v -> {
            Activity activity = (Activity) context;  // Cast context to Activity
            Intent intent = new Intent(context, AdminOrderDetailsActivity.class);
            intent.putExtra("order", order); // Ensure you're passing the correct order

            // Start AdminOrderDetailsActivity expecting a result
            activity.startActivityForResult(intent, REQUEST_CODE); // Use a constant integer for REQUEST_CODE
        });


    }


    private String convertTimestampToDate(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(date);
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtTotalAmount, txtOrderDate, txtStatus;

        public OrderViewHolder(View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtTotalAmount = itemView.findViewById(R.id.txtTotalAmount);
            txtOrderDate = itemView.findViewById(R.id.txtOrderDate);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }

    public void updateList(List<Order> newList) {
        ordersList.clear();
        ordersList.addAll(newList);
        notifyDataSetChanged();
    }
}
