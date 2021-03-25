package com.example.klaf.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.klaf.DateWorker;
import com.example.klaf.R;
import com.example.klaf.pojo.Deck;

import java.util.List;
import java.util.Locale;

import io.reactivex.internal.operators.observable.ObservableNever;

public class DeckAdapter extends RecyclerView.Adapter<DeckAdapter.Holder> {
    private List<Deck> decks;
    private List<Integer> cardQuantityList;
    //    private MainViewModel viewModel;
    private OnDeckClickListener onDeckClickListener;
    private OnDeckLongClickListener onDeckLongClickListener;
    private OnBottomReachedListener onBottomREachedListener;
    private OnTopReachedListener onTopReachedListener;
    private boolean scrolled;

    public DeckAdapter(List<Deck> decks, List<Integer> cardQuantityList) {
        this.decks = decks;
        this.cardQuantityList = cardQuantityList;
        scrolled = false;
    }

    public interface OnDeckClickListener {
        void onDeckClick(int position);
    }

    public interface OnDeckLongClickListener {
        void onDeckLongClick(int position);
    }

    public interface OnBottomReachedListener {
        void onBottomReached();
    }

    public interface OnTopReachedListener {
        void onTopReached();
    }

    public void setOnDeckClickListener(OnDeckClickListener onDeckClickListener) {
        this.onDeckClickListener = onDeckClickListener;
    }

    public void setOnDeckLongClickListener(OnDeckLongClickListener onDeckLongClickListener) {
        this.onDeckLongClickListener = onDeckLongClickListener;
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomREachedListener) {
        this.onBottomREachedListener = onBottomREachedListener;
    }

    public void setOnTopReachedListener(OnTopReachedListener onTopReachedListener) {
        this.onTopReachedListener = onTopReachedListener;
    }

    public void setScrolled(boolean scrolled) {
        this.scrolled = scrolled;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deck, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        if (position == decks.size() - 1 && scrolled) {
            onBottomREachedListener.onBottomReached();
        }
        if (position == 0) {
            onTopReachedListener.onTopReached();
            scrolled = false;
        }
            Deck deck = decks.get(position);
            DateWorker dateWorker = new DateWorker();

            int size = cardQuantityList.get(position);
            String scheduledDate = dateWorker.getFormattedDate(deck.getScheduledDate());
            holder.textViewDeckName.setText(deck.getName());
            holder.textViewScheduledDate.setText(scheduledDate);
            if (dateWorker.getCurrentDate() > deck.getScheduledDate()) {
                holder.textViewScheduledDate.setTextColor(ContextCompat.getColor(holder.textViewScheduledDate.getContext(), R.color.scheduled_date_delay));
            } else {
                holder.textViewScheduledDate.setTextColor(ContextCompat.getColor(holder.textViewScheduledDate.getContext(), R.color.scheduled_date));
            }
            holder.textViewCardsQuantity.setText(String.format(Locale.getDefault(), "%d", size));
            holder.textViewRepetitionDay.setText(String.format(Locale.getDefault(), "%d", deck.getRepetitionDay()));
            holder.textViewRepetitionQuantity.setText(String.format(Locale.getDefault(), "%d", deck.getRepetitionQuantity()));
            holder.setColorOnNameAndBackground(position);
    }

    @Override
    public int getItemCount() {
        return decks.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private final ConstraintLayout constraintLayout;
        private final TextView textViewDeckName;
        private final TextView textViewScheduledDate;
        private final TextView textViewCardsQuantity;
        private final TextView textViewRepetitionDay;
        private final TextView textViewRepetitionQuantity;

        public Holder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.layoutDeckItem);
            textViewDeckName = itemView.findViewById(R.id.textViewDeckName);
            textViewScheduledDate = itemView.findViewById(R.id.textViewDeckScheduledDate);
            textViewCardsQuantity = itemView.findViewById(R.id.textViewDeckQuantityCars);
            textViewRepetitionDay = itemView.findViewById(R.id.textViewRepetitionDay);
            textViewRepetitionQuantity = itemView.findViewById(R.id.textViewRepetitionQuantity);
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDeckClickListener != null) {
                        onDeckClickListener.onDeckClick(getAdapterPosition());
                    }
                }
            });
            constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onDeckLongClickListener != null) {
                        onDeckLongClickListener.onDeckLongClick(getAdapterPosition());
                    }
                    return true;
                }
            });
        }

        private void setColorOnNameAndBackground(int position) {
            if (position % 2 == 0) {
                constraintLayout.setBackground(ContextCompat.getDrawable(constraintLayout.getContext(), R.drawable.deck_item_background_1));
                textViewDeckName.setTextColor(ContextCompat.getColor(constraintLayout.getContext(), R.color.item_deck_name_green));
            } else {
                constraintLayout.setBackgroundResource(R.drawable.deck_item_background_2);
                textViewDeckName.setTextColor(ContextCompat.getColor(constraintLayout.getContext(), R.color.item_deck_name_grey));
            }
        }
    }
}
