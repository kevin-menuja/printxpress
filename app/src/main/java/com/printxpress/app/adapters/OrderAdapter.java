package com.printxpress.app.adapters;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.printxpress.app.R;
import com.printxpress.app.database.entities.Order;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public interface Click {
        void onClick(Order order);
    }

    private final List<Order> items;
    private final Click clickListener;
    private int lastAnimated = -1;

    public OrderAdapter(List<Order> items, Click clickListener) {
        this.items = items;
        this.clickListener = clickListener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).id;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        int bindingPosition = holder.getBindingAdapterPosition();
        if (bindingPosition == RecyclerView.NO_POSITION) {
            return;
        }
        
        Order order = items.get(bindingPosition);
        holder.image.setImageResource(productImage(order.productName));
        holder.orderId.setText("#" + order.orderId);
        holder.product.setText(order.productName);
        
        String payment = (order.paymentMethod == null) ? "CASH" : order.paymentMethod;
        holder.meta.setText(order.category + "  •  " + order.deliveryType + "  •  " + payment);
        
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);
        holder.dates.setText("Placed " + dateFormat.format(order.orderDate) + "   →   Due " + dateFormat.format(order.requiredDate));
        holder.total.setText(String.format(Locale.US, "LKR %,.0f", order.totalPrice));
        holder.status.setText(friendlyStatus(order.status));
        
        int color = statusColor(order.status);
        holder.status.setChipBackgroundColor(ColorStateList.valueOf(color));
        holder.status.setTextColor(Color.WHITE);
        
        holder.progress.setIndicatorColor(color);
        holder.progress.setProgressCompat(progress(order.status), true);

        View.OnClickListener listener = v -> {
            int current = holder.getBindingAdapterPosition();
            if (current != RecyclerView.NO_POSITION) {
                clickListener.onClick(items.get(current));
            }
        };
        
        holder.details.setOnClickListener(listener);
        holder.itemView.setOnClickListener(listener);

        if (bindingPosition > lastAnimated) {
            holder.itemView.setAlpha(0f);
            holder.itemView.setTranslationY(24f);
            holder.itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(bindingPosition * 40L)
                .setDuration(260)
                .start();
            lastAnimated = bindingPosition;
        }
    }

    private int productImage(String name) {
        if (name == null) {
            return R.drawable.service_business_cards;
        }
        switch (name) {
            case "Standard Business Cards":
                return R.drawable.product_business_standard;
            case "Premium Business Cards":
                return R.drawable.product_business_premium;
            case "Square Business Cards":
                return R.drawable.product_business_square;
            case "Folded Business Cards":
                return R.drawable.product_business_folded;
            case "Flyers A5":
                return R.drawable.product_flyer_a5;
            case "Flyers A4":
                return R.drawable.product_flyer_a4;
            case "DL Tri-fold Flyers":
                return R.drawable.product_flyer_dl;
            case "Flyers A6":
                return R.drawable.product_flyer_a6;
            case "Banner 2x4ft":
                return R.drawable.product_banner_2x4;
            case "Banner 3x6ft":
                return R.drawable.product_banner_3x6;
            case "Pull-up Banner":
                return R.drawable.product_banner_pullup;
            case "X-Stand Banner":
                return R.drawable.product_banner_xstand;
            case "Poster A3":
                return R.drawable.product_poster_a3;
            case "Poster A2":
                return R.drawable.product_poster_a2;
            case "Poster A1":
                return R.drawable.product_poster_a1;
            case "Stickers Sheet":
                return R.drawable.product_stickers_sheet;
            case "Vinyl Sticker Pack":
                return R.drawable.product_stickers_vinyl;
            case "Die-cut Stickers":
                return R.drawable.product_stickers_diecut;
            case "T-Shirt Print":
                return R.drawable.product_tshirt_print;
            case "Premium T-Shirt Print":
                return R.drawable.product_tshirt_premium;
            case "Front & Back T-Shirt":
                return R.drawable.product_tshirt_frontback;
            case "Magic Mug":
                return R.drawable.product_mug_magic;
            case "Panoramic Photo Mug":
                return R.drawable.product_mug_photo;
            default:
                return R.drawable.product_mug_print;
        }
    }

    private int statusColor(String status) {
        if ("CANCELLED".equals(status)) {
            return Color.rgb(211, 47, 47);
        }
        if ("DELIVERED".equals(status)) {
            return Color.rgb(0, 137, 123);
        }
        if ("READY".equals(status)) {
            return Color.rgb(46, 125, 50);
        }
        if ("PRINTING".equals(status)) {
            return Color.rgb(30, 136, 229);
        }
        return Color.rgb(251, 140, 0);
    }

    private int progress(String status) {
        if ("DELIVERED".equals(status)) {
            return 100;
        }
        if ("READY".equals(status)) {
            return 75;
        }
        if ("PRINTING".equals(status)) {
            return 50;
        }
        if ("CANCELLED".equals(status)) {
            return 0;
        }
        return 25;
    }

    private String friendlyStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return "Processing";
        }
        if ("READY".equals(status)) {
            return "READY";
        }
        return status.substring(0, 1) + status.substring(1).toLowerCase(Locale.US);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView orderId;
        final TextView product;
        final TextView meta;
        final TextView dates;
        final TextView total;
        final Chip status;
        final LinearProgressIndicator progress;
        final View details;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.orderImage);
            orderId = itemView.findViewById(R.id.orderId);
            product = itemView.findViewById(R.id.product);
            meta = itemView.findViewById(R.id.meta);
            dates = itemView.findViewById(R.id.dates);
            total = itemView.findViewById(R.id.total);
            status = itemView.findViewById(R.id.status);
            progress = itemView.findViewById(R.id.progress);
            details = itemView.findViewById(R.id.details);
        }
    }
}
