package com.example.klaf.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.state.State;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.klaf.DateWorker;
import com.example.klaf.R;
import com.example.klaf.pojo.Desk;
import com.example.klaf.screens.MainViewModel;

import java.util.List;
import java.util.Locale;

public class DeskAdapter extends RecyclerView.Adapter<DeskAdapter.Holder> {
    private List<Desk> desks;
    private MainViewModel viewModel;
    private OnDeskClickListener onDeskClickListener;

    public DeskAdapter(List<Desk> desks, MainViewModel viewModel) {
        this.desks = desks;
        this.viewModel = viewModel;
    }

    public interface OnDeskClickListener {
        void onDeskClick(int position);

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
        int size = viewModel.getCardsByDeskId(desk.getId()).size();
        String scheduledDate = new DateWorker().getFormattedDate(desk.getScheduledDate());
        holder.textViewDeskName.setText(desk.getName());
        holder.textViewScheduledDate.setText(scheduledDate);
        holder.textViewCardsQuantity.setText(String.format(Locale.getDefault(), "%d", size));
    }

    @Override
    public int getItemCount() {
        return desks.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private ConstraintLayout constraintLayout;
        private TextView textViewDeskName;
        private TextView textViewScheduledDate;
        private TextView textViewCardsQuantity;

        public Holder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.layoutDeskItem);
            textViewDeskName = itemView.findViewById(R.id.textViewDeskName);
            textViewScheduledDate = itemView.findViewById(R.id.textViewDeskScheduledDate);
            textViewCardsQuantity = itemView.findViewById(R.id.textViewDeskQuantityCars);
            constraintLayout.setOnClickListener(new View.OnClickListener() {
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
