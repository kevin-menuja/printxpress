package com.printxpress.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.printxpress.app.R;
import com.printxpress.app.database.entities.Promotion;
import java.util.List;

public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.PromotionViewHolder> {
    private final List<Promotion> items;

    public PromotionAdapter(List<Promotion> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public PromotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promotion, parent, false);
        return new PromotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionViewHolder holder, int position) {
        Promotion promotion = items.get(position);
        holder.titleText.setText(promotion.discountText + "  •  " + promotion.title);
        holder.descriptionText.setText(promotion.description);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class PromotionViewHolder extends RecyclerView.ViewHolder {
        final TextView titleText;
        final TextView descriptionText;

        PromotionViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.promotionTitle);
            descriptionText = itemView.findViewById(R.id.promotionDescription);
        }
    }
}
