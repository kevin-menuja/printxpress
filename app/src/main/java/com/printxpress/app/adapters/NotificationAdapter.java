package com.printxpress.app.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.printxpress.app.R;
import com.printxpress.app.database.entities.Notification;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private final List<Notification> notifications;

    public NotificationAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        
        holder.iconText.setText(notification.type.equals("promotion") ? "🏷" : "🔔");
        holder.titleText.setText(notification.title);
        holder.messageText.setText(notification.message);
        holder.timeText.setText(
            new SimpleDateFormat("dd MMM, h:mm a", Locale.US).format(notification.createdAt)
        );
        
        holder.cardView.setStrokeWidth(notification.isRead ? 0 : 6);
        holder.cardView.setStrokeColor(Color.rgb(255, 111, 0));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        final MaterialCardView cardView;
        final TextView iconText;
        final TextView titleText;
        final TextView messageText;
        final TextView timeText;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            iconText = itemView.findViewById(R.id.icon);
            titleText = itemView.findViewById(R.id.title);
            messageText = itemView.findViewById(R.id.message);
            timeText = itemView.findViewById(R.id.time);
        }
    }
}
