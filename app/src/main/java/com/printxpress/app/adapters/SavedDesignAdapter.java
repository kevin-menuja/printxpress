package com.printxpress.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.printxpress.app.R;
import com.printxpress.app.database.entities.SavedDesign;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class SavedDesignAdapter extends RecyclerView.Adapter<SavedDesignAdapter.SavedDesignViewHolder> {

    public interface Actions {
        void select(SavedDesign design);
        void delete(SavedDesign design);
    }

    private final List<SavedDesign> items;
    private final Actions actions;

    public SavedDesignAdapter(List<SavedDesign> items, Actions actions) {
        this.items = items;
        this.actions = actions;
    }

    @NonNull
    @Override
    public SavedDesignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_design, parent, false);
        return new SavedDesignViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedDesignViewHolder holder, int position) {
        SavedDesign design = items.get(position);
        holder.nameText.setText(design.name);
        holder.dateText.setText(
            new SimpleDateFormat("dd MMM yyyy", Locale.US).format(design.createdAt)
        );
        holder.itemView.setOnClickListener(v -> actions.select(design));
        holder.deleteView.setOnClickListener(v -> actions.delete(design));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class SavedDesignViewHolder extends RecyclerView.ViewHolder {
        final TextView nameText;
        final TextView dateText;
        final View deleteView;

        SavedDesignViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.designName);
            dateText = itemView.findViewById(R.id.designDate);
            deleteView = itemView.findViewById(R.id.deleteDesign);
        }
    }
}
