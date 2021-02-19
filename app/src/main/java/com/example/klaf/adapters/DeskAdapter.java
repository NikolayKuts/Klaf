package com.example.klaf.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.state.State;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.klaf.DateWorker;
import com.example.klaf.R;
import com.example.klaf.pojo.Desk;
import com.example.klaf.screens.MainViewModel;

import java.util.List;
import java.util.Locale;

public class DeskAdapter extends RecyclerView.Adapter<DeskAdapter.Holder> {
    private List<Desk> desks;
    private List<Integer> cardQuantityList;
//    private MainViewModel viewModel;
    private OnDeskClickListener onDeskClickListener;
    private OnDeskLongClickListener onDeskLongClickListener;

    public DeskAdapter(List<Desk> desks, List<Integer> cardQuantityList) {
        this.desks = desks;
        this.cardQuantityList = cardQuantityList;
    }

    public interface OnDeskClickListener {
        void onDeskClick(int position);
    }

    public interface OnDeskLongClickListener {
        void onDeckLongClick(int position);
    }

    public void setOnDeskClickListener(OnDeskClickListener onDeskClickListener) {
        this.onDeskClickListener = onDeskClickListener;
    }

    public void setOnDeskLongClickListener(OnDeskLongClickListener onDeskLongClickListener) {
        this.onDeskLongClickListener = onDeskLongClickListener;
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
        int size = cardQuantityList.get(position);
        String scheduledDate = new DateWorker().getFormattedDate(desk.getScheduledDate());
        holder.textViewDeskName.setText(desk.getName());
        holder.textViewScheduledDate.setText(scheduledDate);
        holder.textViewCardsQuantity.setText(String.format(Locale.getDefault(), "%d", size));
        holder.textViewRepetitionDay.setText(String.format(Locale.getDefault(), "%d_d", desk.getRepetitionDay()));
        holder.setBackgroundColor(position);
    }

    @Override
    public int getItemCount() {
        return desks.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private final ConstraintLayout constraintLayout;
        private final TextView textViewDeskName;
        private final TextView textViewScheduledDate;
        private final TextView textViewCardsQuantity;
        private final TextView textViewRepetitionDay;

        public Holder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.layoutDeskItem);
            textViewDeskName = itemView.findViewById(R.id.textViewDeskName);
            textViewScheduledDate = itemView.findViewById(R.id.textViewDeskScheduledDate);
            textViewCardsQuantity = itemView.findViewById(R.id.textViewDeskQuantityCars);
            textViewRepetitionDay = itemView.findViewById(R.id.textViewRepetitionDay);
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDeskClickListener != null) {
                        onDeskClickListener.onDeskClick(getAdapterPosition());
                    }
                }
            });
            constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onDeskLongClickListener != null) {
                        onDeskLongClickListener.onDeckLongClick(getAdapterPosition());
                    }
                    return true;
                }
            });
        }

        private void setBackgroundColor(int position) {
            if (position % 2 == 0) {
                constraintLayout.setBackgroundColor(ContextCompat.getColor(constraintLayout.getContext(), R.color.item_desk_background_dark));
            } else {
                constraintLayout.setBackgroundColor(ContextCompat.getColor(constraintLayout.getContext(), R.color.transparent));
            }
        }
    }
}
