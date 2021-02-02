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

public class LettersBarAdapter extends RecyclerView.Adapter<LettersBarAdapter.Holder> {
    private List<CheckedLetterHolder> list;
    private OnClickHelper onClickHelper;

    public LettersBarAdapter(List<CheckedLetterHolder> list) {
        this.list = list;
    }

    public interface OnClickHelper {
        void onItemClick(int position);
    }

    public void setOnClickHelper(OnClickHelper onClickHelper) {
        this.onClickHelper = onClickHelper;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_letters_bar, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        CheckedLetterHolder checkedHolder = list.get(position);
        holder.textView.setText(checkedHolder.getLetter());
        boolean checked = checkedHolder.isChecked();
        if (checked) {
//                holder.textView.setTextColor(ContextCompat.getColor(holder.textView.getContext(), R.color.black));
            holder.textView.setTextSize(50.0f);
            holder.textView.setBackgroundColor(ContextCompat.getColor(holder.textView.getContext(), R.color.teal_700));
        } else {
//                holder.textView.setTextColor(R.color.black);
            holder.textView.setTextSize(40.0f);
            holder.textView.setBackgroundColor(ContextCompat.getColor(holder.textView.getContext(), R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewBarLetter);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckedLetterHolder checkedHolder = list.get(getAdapterPosition());
                    checkedHolder.setChecked(!checkedHolder.isChecked());
                    onClickHelper.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
