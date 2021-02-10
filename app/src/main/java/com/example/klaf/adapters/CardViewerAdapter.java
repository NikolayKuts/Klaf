package com.example.klaf.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.klaf.IpaProcesser;
import com.example.klaf.R;
import com.example.klaf.pojo.Card;

import java.util.List;

public class CardViewerAdapter extends RecyclerView.Adapter<CardViewerAdapter.Holder> {
    private List<Card> cards;

    public CardViewerAdapter(List<Card> cards) {
        this.cards = cards;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_viewer, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Card card = cards.get(position);
        IpaProcesser  ipaProcesser = new IpaProcesser();
        holder.textViewNativeWord.setText(card.getNativeWord());
        holder.textViewForeignWord.setText(card.getForeignWord());
        holder.textViewIpa.setText(ipaProcesser.getCompletedCouplesForCardViewer(card.getIpa()));

        if (position % 2 == 0) {
            holder.constraintLayout.setBackgroundColor(ContextCompat.getColor(holder.textViewNativeWord.getContext(), R.color.item_desk_background_dark));
        } else {
            holder.constraintLayout.setBackgroundColor(ContextCompat.getColor(holder.textViewNativeWord.getContext(), R.color.transparent));
        }
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        private ConstraintLayout  constraintLayout;
        private TextView textViewNativeWord;
        private TextView textViewForeignWord;
        private TextView textViewIpa;

        public Holder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.layoutItemCardViewer);
            textViewNativeWord = itemView.findViewById(R.id.textViewNativeWord);
            textViewForeignWord = itemView.findViewById(R.id.textViewForeignWord);
            textViewIpa = itemView.findViewById(R.id.textViewIpa);
        }
    }
}
