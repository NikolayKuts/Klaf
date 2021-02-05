package com.example.klaf.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.klaf.DateWorker;
import com.example.klaf.FloatingActionButtonAnimator;
import com.example.klaf.IpaProcesser;
import com.example.klaf.MyTimer;
import com.example.klaf.R;
import com.example.klaf.adapters.SoundsIpaAdapter;
import com.example.klaf.pojo.Card;
import com.example.klaf.pojo.Desk;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class LessonActivity extends AppCompatActivity {
    private static final int EASE_LEVEL_EASY = 0;
    private static final int EASE_LEVEL_GOOD = 1;
    private static final int EASE_LEVEL_BAD = 2;

    private Card startElement;

    private List<Card> cards;
    private MainViewModel viewModel;
    private int deskId;
    private Desk lessonDesk;
    private int wordPosition = 0;
    private boolean front;

    private TextView textViewWord;
    private TextView textViewTimeCounter;
    private RecyclerView recyclerViewIpa;

    private MyTimer timer;


    private boolean clicked = false;

    private FloatingActionButton main;
    private FloatingActionButton button1;
    private FloatingActionButton button2;
    private FloatingActionButton button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        textViewWord = findViewById(R.id.textViewWord);
        textViewTimeCounter = findViewById(R.id.textViewTimeCounter);
        recyclerViewIpa = findViewById(R.id.recyclerviewIpa);

        timer = new MyTimer(textViewTimeCounter);

        recyclerViewIpa.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getDesks().observe(this, new Observer<List<Desk>>() {
            @Override
            public void onChanged(List<Desk> desks) {
                lessonDesk = viewModel.getDeskById(deskId);
            }
        });

        deskId = getIntent().getIntExtra("id_desk", 0);

        cards = new LinkedList<>();
//        cards.addAll(viewModel.getCardsByDeskId(deskId));

        setTextViewContent();

        main = findViewById(R.id.floatingActionButtonMain);
        button1 = findViewById(R.id.floatingActionButtonAddCard);
        button2 = findViewById(R.id.floatingActionButtonEditCard);
        button3 = findViewById(R.id.floatingActionButton3);

        FloatingActionButtonAnimator buttonAnimator = new FloatingActionButtonAnimator(button1, button2, button3);
        main.setOnClickListener(buttonAnimator);

    }

    @Override
    protected void onResume() {
        super.onResume();
        lessonDesk = viewModel.getDeskById(deskId);
        cards.addAll(viewModel.getCardsByDeskId(deskId));
        setTextViewContent();


        Log.i("log_after_moving", cards.toString());
    }

    private void setTextViewContent() {
        if (!cards.isEmpty()) {
            Card card = cards.get(0);
            SoundsIpaAdapter adapter;
            IpaProcesser ipaProcesser = new IpaProcesser();
            if (front) {
                textViewWord.setText(card.getForeignWord());
                adapter = new SoundsIpaAdapter(ipaProcesser.getDecodeSoundsListFromIpa(card.getIpa()));
            } else {
                textViewWord.setText(card.getNativeWord());
                adapter = new SoundsIpaAdapter(new ArrayList<>());
            }
            recyclerViewIpa.setAdapter(adapter);
        }
    }

    public void onClickTurn(View view) {
        front = !front;
        setTextViewContent();
    }


    private void moveCardByEaseLevel(int easeLevel) {
        Card cardForMoving = cards.get(0);
        if (easeLevel == EASE_LEVEL_EASY) {
            cards.remove(0);
            cards.add(cardForMoving);
        } else if (easeLevel == EASE_LEVEL_GOOD) {
            cards.remove(0);
            int newPosition = cards.size() * 3 / 4;
            Log.i("log_good", cardForMoving.getNativeWord() + "_to_" + newPosition);
            cards.add(newPosition, cardForMoving);
        } else {
            cards.remove(0);
            int newPosition = cards.size() / 4;
            Log.i("log_bad", cardForMoving.getNativeWord() + "_to_" + newPosition);
            cards.add(newPosition, cardForMoving);
        }
        Log.i("log_after_moving", cards.toString());

    }

    public void onClickEasy(View view) {
        if (!cards.isEmpty()) {
            if (startElement == null) {
                startElement = cards.get(0);
            }
            moveCardByEaseLevel(EASE_LEVEL_EASY);
            if (!timer.isAllowed()) {
                timer.runCount();
            }
            showEndDialogOnFinishing();
            front = false;
            setTextViewContent();

        }
    }

    public void onClickGood(View view) {
        if (!cards.isEmpty()) {
            moveCardByEaseLevel(EASE_LEVEL_GOOD);
            front = false;
            setTextViewContent();
            if (!timer.isAllowed()) {
                timer.runCount();
            }
        }
    }

    public void onClickBad(View view) {
        if (!cards.isEmpty()) {
            moveCardByEaseLevel(EASE_LEVEL_BAD);
            front = false;
            setTextViewContent();
            if (!timer.isAllowed()) {
                timer.runCount();
            }
        }
    }

    public void onClickToAddCardActivity(View view) {
        Intent intent = new Intent(this, AddCardActivity.class);
        intent.putExtra("id_desk", deskId);
        startActivity(intent);
    }

    private void showEndDialogOnFinishing() {
        if (startElement != null) {
            if (startElement.equals(cards.get(0))) {
                timer.stopCount();
                DateWorker dateWorker = new DateWorker();
                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog_end_repetition);

                TextView textViewCurrentDuration = dialog.findViewById(R.id.textViewValueCurrentDuration);
                TextView textViewLastDuration = dialog.findViewById(R.id.textViewValueLastDuration);
                TextView textViewNewScheduledDate = dialog.findViewById(R.id.textViewValueScheduldDate);
                TextView textViewLastScheduledDate = dialog.findViewById(R.id.textViewValueLastScheduledDate);

                long currentDate = dateWorker.getCurrentDate();
                int currentRepetitionDuration = timer.getSavedTotalSeconds();
                long newScheduledDate = dateWorker.getScheduledDateNextRepetition(lessonDesk, currentRepetitionDuration);
                int updatedRepetitionDay = dateWorker.getUpdatedRepetitionDay(lessonDesk); // method getUpdated
                boolean updatedSucceededLastRepetition = dateWorker.getUpdatedSucceededLastRepetition(lessonDesk, currentRepetitionDuration);
                Desk updatedDesk = new Desk(
                        deskId,
                        lessonDesk.getName(),
                        lessonDesk.getCreationDate(),
                        currentDate,
                        newScheduledDate,
                        currentRepetitionDuration,
                        updatedRepetitionDay,
                        updatedSucceededLastRepetition);

                viewModel.insertDesk(updatedDesk);

                textViewLastDuration.setText(String.format(Locale.getDefault(), "%d", lessonDesk.getLastRepeatDuration()));
                textViewCurrentDuration.setText(textViewTimeCounter.getText());
                textViewNewScheduledDate.setText(dateWorker.getFormattedDate(newScheduledDate));
                textViewLastScheduledDate.setText(dateWorker.getFormattedDate(lessonDesk.getScheduledDate()));
                dialog.show();
            }
        }
    }

    public void onButtonAddClick(View view) {
        Intent intent = new Intent(getApplicationContext(), AddCardActivity.class);
        intent.putExtra("id_desk", deskId);
        startActivity(intent);
    }

    public void onButtonEditClick(View view) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_rotate_anim);
        view.startAnimation(animation);
    }
}