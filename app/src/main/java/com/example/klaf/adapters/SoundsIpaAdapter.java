package com.example.klaf.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.klaf.CheckedLetterHolder;
import com.example.klaf.R;

import java.util.List;

public class SoundsIpaAdapter extends RecyclerView.Adapter<SoundsIpaAdapter.Holder> {
    private final List<CheckedLetterHolder> list;

    public SoundsIpaAdapter(List<CheckedLetterHolder> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ipa, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        CheckedLetterHolder checkedHolder = list.get(position);
        holder.textViewItemResult.setText(checkedHolder.getLetter());
        if (checkedHolder.isChecked()) {
            holder.textViewItemResult.setTextColor(ContextCompat.getColor(holder.textViewItemResult.getContext(), R.color.red));
//            holder.textViewItemResult.setBackgroundColor(ContextCompat.getColor(holder.textViewItemResult.getContext(), R.color.blue));
        } else {
            holder.textViewItemResult.setTextColor(ContextCompat.getColor(holder.textViewItemResult.getContext(), R.color.transparent));
//            holder.textViewItemResult.setBackgroundColor(ContextCompat.getColor(holder.textViewItemResult.getContext(), R.color.black_transparent));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        private final TextView textViewItemResult;

        public Holder(@NonNull View itemView) {
            super(itemView);
            textViewItemResult = itemView.findViewById(R.id.textViewItemIpa);
        }
    }
}
