package com.printxpress.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;
import com.printxpress.app.R;
import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqViewHolder> {
    private final List<String[]> faqList;

    public FaqAdapter(List<String[]> faqList) {
        this.faqList = faqList;
    }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faq, parent, false);
        return new FaqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqViewHolder holder, int position) {
        String[] faq = faqList.get(position);
        holder.questionText.setText(faq[0]);
        holder.answerText.setText(faq[1]);

        holder.questionText.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(
                (ViewGroup) holder.itemView, 
                new AutoTransition().setDuration(180)
            );
            boolean isVisible = holder.answerText.getVisibility() == View.VISIBLE;
            holder.answerText.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            
            holder.questionText.animate()
                .alpha(0.65f)
                .setDuration(70)
                .withEndAction(() -> holder.questionText.animate().alpha(1.0f).setDuration(100).start())
                .start();
        });
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    static class FaqViewHolder extends RecyclerView.ViewHolder {
        final TextView questionText;
        final TextView answerText;

        FaqViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.question);
            answerText = itemView.findViewById(R.id.answer);
        }
    }
}
