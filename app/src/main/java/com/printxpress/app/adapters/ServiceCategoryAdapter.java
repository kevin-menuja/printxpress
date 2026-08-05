package com.printxpress.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.printxpress.app.R;
import com.printxpress.app.models.ServiceCategory;
import java.util.List;

public class ServiceCategoryAdapter extends RecyclerView.Adapter<ServiceCategoryAdapter.ServiceViewHolder> {

    public interface Click {
        void onClick(String categoryName);
    }

    private final List<ServiceCategory> items;
    private final Click clickListener;

    public ServiceCategoryAdapter(List<ServiceCategory> items, Click clickListener) {
        this.items = items;
        this.clickListener = clickListener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).name.hashCode();
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ServiceCategory category = items.get(position);
        holder.name.setText(category.name);
        holder.image.setImageResource(category.imageRes);
        
        holder.itemView.setOnClickListener(v -> {
            v.animate()
                .scaleX(0.94f)
                .scaleY(0.94f)
                .setDuration(80)
                .withEndAction(() -> {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120).start();
                    clickListener.onClick(category.name);
                })
                .start();
        });
        
        holder.itemView.setAlpha(0f);
        holder.itemView.setTranslationX(24f);
        holder.itemView.animate()
            .alpha(1.0f)
            .translationX(0f)
            .setStartDelay(position * 35L)
            .setDuration(260)
            .start();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final ImageView image;

        ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.serviceImage);
        }
    }
}
