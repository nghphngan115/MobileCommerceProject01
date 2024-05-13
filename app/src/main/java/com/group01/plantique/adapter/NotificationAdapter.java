package com.group01.plantique.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group01.plantique.R;
import com.group01.plantique.model.NotificationApp;
import com.group01.plantique.java.OrderHistoryActivity;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private Context context;
    private List<NotificationApp> notificationList;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClicked();
    }

    public NotificationAdapter(Context context, List<NotificationApp> notificationList, OnNotificationClickListener listener) {
        this.context = context;
        this.notificationList = notificationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationApp notification = notificationList.get(position);
        holder.textViewTitle.setText(notification.getTitle());
        holder.textViewDetails.setText(notification.getContent());

        // Set the click listener to handle the click event
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onNotificationClicked();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        TextView textViewDetails;
        ImageView imageView;

        NotificationViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.txtNotiTitle);
            textViewDetails = itemView.findViewById(R.id.txtNotiDetails);
            imageView = itemView.findViewById(R.id.imageView2);
        }
    }
}
