package com.printxpress.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.printxpress.app.R;
import com.printxpress.app.database.entities.Product;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface Click {
        void onClick(Product product);
    }

    private final List<Product> shown = new ArrayList<>();
    private final List<Product> all = new ArrayList<>();
    private final Click clickListener;
    private int lastAnimated = -1;

    public ProductAdapter(List<Product> products, Click clickListener) {
        this.shown.addAll(products);
        this.all.addAll(products);
        this.clickListener = clickListener;
        setHasStableIds(true);
    }

    public void filter(String query) {
        String q = query.trim().toLowerCase(Locale.US);
        shown.clear();
        for (Product p : all) {
            if (p.name.toLowerCase(Locale.US).contains(q) || p.category.toLowerCase(Locale.US).contains(q)) {
                shown.add(p);
            }
        }
        lastAnimated = -1;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return shown.get(position).id;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        int bindingPosition = holder.getBindingAdapterPosition();
        if (bindingPosition == RecyclerView.NO_POSITION) {
            return;
        }
        
        Product item = shown.get(bindingPosition);
        holder.name.setText(item.name);
        holder.price.setText(String.format(Locale.US, "LKR %,.0f", item.basePrice));
        holder.image.setImageResource(imageFor(item.name));

        View.OnClickListener listener = v -> {
            int current = holder.getBindingAdapterPosition();
            if (current == RecyclerView.NO_POSITION) {
                return;
            }
            v.animate()
                .scaleX(0.96f)
                .scaleY(0.96f)
                .setDuration(70)
                .withEndAction(() -> {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                    int latest = holder.getBindingAdapterPosition();
                    if (latest != RecyclerView.NO_POSITION) {
                        clickListener.onClick(shown.get(latest));
                    }
                })
                .start();
        };

        holder.button.setOnClickListener(listener);
        holder.itemView.setOnClickListener(listener);

        if (bindingPosition > lastAnimated) {
            holder.itemView.setAlpha(0f);
            holder.itemView.setTranslationY(28f);
            holder.itemView.animate()
                .alpha(1.0f)
                .translationY(0f)
                .setStartDelay(bindingPosition * 45L)
                .setDuration(280)
                .start();
            lastAnimated = bindingPosition;
        } else {
            holder.itemView.setAlpha(1.0f);
            holder.itemView.setTranslationY(0f);
        }
    }

    private int imageFor(String name) {
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

    @Override
    public int getItemCount() {
        return shown.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView price;
        final ImageView image;
        final View button;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.productImage);
            button = itemView.findViewById(R.id.order);
        }
    }
}
