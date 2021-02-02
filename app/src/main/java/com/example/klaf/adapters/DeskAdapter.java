package com.example.klaf.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.klaf.R;
import com.example.klaf.pojo.Desk;

import java.util.List;

public class DeskAdapter extends RecyclerView.Adapter<DeskAdapter.Holder> {
    private List<Desk> desks;
    private OnDeskClickListener onDeskClickListener;

    public interface OnDeskClickListener {
        void onDeskClick(int position);
    }

    public DeskAdapter(List<Desk> desks) {
        this.desks = desks;
    }

    public void setOnDeskClickListener(OnDeskClickListener onDeskClickListener) {
        this.onDeskClickListener = onDeskClickListener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_desk, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Desk desk = desks.get(position);
        holder.textViewDeskName.setText(desk.getName());
    }

    @Override
    public int getItemCount() {
        return desks.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private TextView textViewDeskName;

        public Holder(@NonNull View itemView) {
            super(itemView);
            textViewDeskName = itemView.findViewById(R.id.textViewDeskName);
            textViewDeskName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDeskClickListener != null) {
                        onDeskClickListener.onDeskClick(getAdapterPosition());
                    }
                }
            });
        }
    }
}
