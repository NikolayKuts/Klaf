package com.example.klaf.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.klaf.CheckedLetterHolder;
import com.example.klaf.DateWorker;
import com.example.klaf.FloatingActionButtonAnimator;
import com.example.klaf.IpaProcesser;
import com.example.klaf.MyTimer;
import com.example.klaf.R;
import com.example.klaf.adapters.SoundsIpaAdapter;
import com.example.klaf.data.OnClickAudioPlayer;
import com.example.klaf.notifications.NotificationAssembler;
import com.example.klaf.pojo.Card;
import com.example.klaf.pojo.Desk;
import com.example.klaf.services.RepetitionReminder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LessonActivity extends AppCompatActivity {
    private static final int EASE_LEVEL_EASY = 0;
    private static final int EASE_LEVEL_GOOD = 1;
    private static final int EASE_LEVEL_BAD = 2;
    public static final String TAG_CARD_ID = "card_id";

    private TextView textViewWord;
    private TextView textViewWordType1;
    private TextView textViewWordType2;

    private TextView textViewTimeCounter;
    private TextView textViewDeskName;
    private RecyclerView recyclerViewIpa;

    private Switch switchRepetitionOrder;

    private Card startElement;
    private Card goodElement;
    private Card badElement;

    private List<Card> cards;
    private List<Card> savedProgressList = new LinkedList<>();
    private MainViewModel viewModel;
    private int deskId;
    private Desk lessonDesk;
    private boolean front;


    private SoundsIpaAdapter soundsIpaAdapter;
    private List<CheckedLetterHolder> soundList = new ArrayList<>();

    private MyTimer timer;

    FloatingActionButtonAnimator buttonAnimator;
    private FloatingActionButton main;
    private FloatingActionButton button1;
    private FloatingActionButton button2;
    private FloatingActionButton button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);

        textViewWord = findViewById(R.id.textViewWord);
        textViewWordType1 = findViewById(R.id.textViewWordTypeFront);
        textViewWordType2 = findViewById(R.id.textViewWordTypeBack);
        textViewTimeCounter = findViewById(R.id.textViewTimeCounter);
        textViewDeskName = findViewById(R.id.textViewLessonDeskName);
        recyclerViewIpa = findViewById(R.id.recyclerViewIpa);
        switchRepetitionOrder = findViewById(R.id.switch1);

        main = findViewById(R.id.floatingActionButtonMain);
        button1 = findViewById(R.id.floatingActionButtonAddCard);
        button2 = findViewById(R.id.floatingActionButtonEditCard);
        button3 = findViewById(R.id.floatingActionButton3);

        timer = new MyTimer(textViewTimeCounter);
        cards = new LinkedList<>();

        deskId = getIntent().getIntExtra(MainActivity.TAG_DESK_ID, 0);

        soundsIpaAdapter = new SoundsIpaAdapter(soundList);

        recyclerViewIpa.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewIpa.setAdapter(soundsIpaAdapter);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        buttonAnimator = new FloatingActionButtonAnimator(button1, button2, button3);
        buttonAnimator.setOnClickHelper(new FloatingActionButtonAnimator.OnClickHelper() {
            @Override
            public void onClick() {
                if (buttonAnimator.isClicked()) {
                    if (timer.isRun()) {
                        timer.pauseCount();
                    }
                } else {
                    if (timer.isPaused()) {
                        timer.runCount();
                    }
                }
            }
        });
        main.setOnClickListener(buttonAnimator);

        setVisibilityOnButtons(false);

        switchRepetitionOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setTextViewContent();
                setSoundAdapterContent();
                setTextOnLessonOrder(isChecked);
                textViewWord.setClickable(isChecked);
            }
        });
        textViewWord.setOnClickListener(new OnClickAudioPlayer());
        textViewWord.setClickable(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        front = true;
        lessonDesk = viewModel.getDeskById(deskId);
        cards.clear();
        cards.addAll(viewModel.getCardsByDeskId(deskId));
        if (cards.isEmpty()) {
            savedProgressList.clear();
            startElement = null;
            goodElement = null;
            badElement = null;
            timer.stopCount();
            setVisibilityOnButtons(false);
        }

        if (timer.isPaused() && !buttonAnimator.isClicked()) {
            timer.runCount();
        }

        Log.i("log", "on resume saved :" + savedProgressList);
        Log.i("log", "on resume cards :" + cards);
        Log.i("log", "on resume start :" + startElement);
        Log.i("log", "on resume good :" + goodElement);
        Log.i("log", "on resume bad :" + badElement);

        if (!savedProgressList.isEmpty()) {
            fillCardsByProgress();
        }
        textViewDeskName.setText(lessonDesk.getName());
        setTextViewContent();
        setSoundAdapterContent();
        onFinishLesson();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.pauseCount();
    }

    private void fillCardsByProgress() {
        List<Card> updatedList = new LinkedList<>();
        List<Card> addedCards = new LinkedList<>();

        if (savedProgressList.size() < cards.size()) {
            List<Card> temporary = new LinkedList<>(cards);

            for (Card card : cards) {
                boolean contains = false;
                for (Card savedCard : savedProgressList) {
                    if (card.getId() == savedCard.getId()) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    addedCards.add(card);
                    temporary.remove(card);
                }
            }
            cards.clear();
            cards.addAll(temporary);
        }

        for (Card savedCard : savedProgressList) {
            for (Card relevantCard : cards) {
                if (relevantCard.getId() == savedCard.getId()) {
                    updatedList.add(relevantCard);
                    break;
                }
            }
        }
        updatedList.addAll(addedCards);
        cards.clear();
        cards.addAll(updatedList);
    }

    private void setTextViewContent() {
        if (cards.isEmpty()) {
            textViewWord.setText("The desk is empty");
            textViewWord.setTextColor(getResources().getColor(R.color.hint));
        } else {
            Card card = cards.get(0);

            if (switchRepetitionOrder.isChecked()) {
                if (front) {
                    textViewWord.setText(card.getForeignWord());
                } else {
                    textViewWord.setText(card.getNativeWord());
                }

            } else {
                if (front) {
                    textViewWord.setText(card.getNativeWord());
                } else {
                    textViewWord.setText(card.getForeignWord());
                }
            }
            setTextWordColorBySide();

        }
    }

    private void setTextWordColorBySide() {
        if (front) {
            textViewWord.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.word_front));
        } else {
            textViewWord.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.word_back));
        }
    }

    private void setTextOnLessonOrder(boolean isChecked) {
        if (isChecked) {
            textViewWordType1.setText("F");
            textViewWordType2.setText("N");
        } else {
            textViewWordType1.setText("N");
            textViewWordType2.setText("F");
        }
    }


    private void setSoundAdapterContent() {
        IpaProcesser ipaProcesser = new IpaProcesser();
        if (!cards.isEmpty()) {
            Card card = cards.get(0);

            if (switchRepetitionOrder.isChecked()) {
                if (front) {
                    soundList.clear();
                    soundList.addAll(ipaProcesser.getDecodeSoundsListFromIpa(card.getIpa()));
                } else {
                    soundList.clear();
                }
            } else {
                if (front) {
                    soundList.clear();
                } else {
                    soundList.clear();
                    soundList.addAll(ipaProcesser.getDecodeSoundsListFromIpa(card.getIpa()));
                }
            }
        }
        soundsIpaAdapter.notifyDataSetChanged();
    }

    private void moveCardByEaseLevel(int easeLevel) {
        Card cardForMoving = cards.get(0);
        cards.remove(0);
        if (easeLevel == EASE_LEVEL_EASY) {
            cards.add(cardForMoving);
        } else if (easeLevel == EASE_LEVEL_GOOD) {
            int newPosition = cards.size() * 3 / 4;
            cards.add(newPosition, cardForMoving);
        } else {
            int newPosition = cards.size() / 4;
            cards.add(newPosition, cardForMoving);
        }

        saveProgress();
        Log.i("log", "moving: cards" + cards.toString());
    }

    private void saveProgress() {
        savedProgressList.clear();
        savedProgressList.addAll(cards);
    }

    public void onClickTurn(View view) {
        front = !front;
        setTextViewContent();
        setSoundAdapterContent();
        setOnTextViewWordClickable(switchRepetitionOrder.isChecked());
    }

    private void setOnTextViewWordClickable(boolean isChecked) {
        boolean clickable;

        if (isChecked) {
            clickable = front;
        } else {
            clickable = !front;
        }
        textViewWord.setClickable(clickable);
    }

    public void onClickEasy(View view) {
        if (!cards.isEmpty()) {
            Card card = cards.get(0);
            if (startElement == null) {
                startElement = card;
            } else if (startElement.getId() == card.getId()) {
                startElement = new Card(-1, 0, "", "", "");
            }

            if (goodElement != null && goodElement.getId() == card.getId()) {
                goodElement = null;
            }

            if (badElement != null && badElement.getId() == card.getId()) {
                badElement = null;
            }

            moveCardByEaseLevel(EASE_LEVEL_EASY);
            onFinishLesson();
            front = true;
            setTextViewContent();
            setSoundAdapterContent();
            setOnTextViewWordClickable(switchRepetitionOrder.isChecked());
        }
        closeFloatingButtonIfOpened();
        Log.i("TAG", "onClickGood: " + cards.toString());
    }

    public void onClickGood(View view) {
        if (!cards.isEmpty()) {
            goodElement = cards.get(0);

            moveCardByEaseLevel(EASE_LEVEL_GOOD);
            front = true;
            setTextViewContent();
            setSoundAdapterContent();
            setOnTextViewWordClickable(switchRepetitionOrder.isChecked());
        }
        closeFloatingButtonIfOpened();
        Log.i("TAG", "onClickGood: " + cards.toString());
    }

    public void onClickBad(View view) {
        if (!cards.isEmpty()) {
            badElement = cards.get(0);

            moveCardByEaseLevel(EASE_LEVEL_BAD);
            front = true;
            setTextViewContent();
            setSoundAdapterContent();
            setOnTextViewWordClickable(switchRepetitionOrder.isChecked());
        }
        closeFloatingButtonIfOpened();
        Log.i("TAG", "onClickGood: " + cards.toString());
    }


    public void onButtonAddClick(View view) {
        Intent intent = new Intent(getApplicationContext(), AddCardActivity.class);
        intent.putExtra(MainActivity.TAG_DESK_ID, deskId);
        startActivity(intent);
    }

    public void onButtonEditClick(View view) {
        if (cards.isEmpty()) {
            Toast.makeText(this, "There isn't anything for editing", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), EditCardActivity.class);
            intent.putExtra(TAG_CARD_ID, cards.get(0).getId());
            startActivity(intent);
        }
    }

    public void onButtonDeleteClick(View view) {
        if (cards.isEmpty()) {
            Toast.makeText(this, "There is nothing for deleting", Toast.LENGTH_SHORT).show();
        } else {
            Dialog dialog = new Dialog(view.getContext());
            dialog.setContentView(R.layout.dialog_delete_card);

            TextView textViewCardForDeleting = dialog.findViewById(R.id.textViewCardForDeleting);
            Button buttonCancel = dialog.findViewById(R.id.buttonCancleCardDeleting);
            Button buttonDelete = dialog.findViewById((R.id.buttonApplyCardDeleting));

            Card cardForDeleting = cards.get(0);
            String nativeWord = cardForDeleting.getNativeWord();
            String foreignWord = cardForDeleting.getForeignWord();

            String cardContent = String.format("%s | %s", nativeWord, foreignWord);
            textViewCardForDeleting.setText(cardContent);

            dialog.show();

            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewModel.deleteCard(cardForDeleting);
                    if (startElement != null && startElement.getId() == cardForDeleting.getId()) {
                        if (cards.size() > 1) {
                            startElement = cards.get(1);
                        }
                    }
                    if (goodElement != null && goodElement.getId() == cardForDeleting.getId()) {
                        goodElement = null;
                    }
                    if (badElement != null && badElement.getId() == cardForDeleting.getId()) {
                        badElement = null;
                    }
                    dialog.dismiss();
                    onResume();
                    Toast.makeText(LessonActivity.this, "The card has been deleted", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onClickStartLesson(View view) {
        if (!cards.isEmpty()) {
            setVisibilityOnButtons(true);
            if (!timer.isRun()) {
                timer.runCount();
            }
            saveProgress();
            closeFloatingButtonIfOpened();
        }
    }

    private void closeFloatingButtonIfOpened() {
        if (buttonAnimator.isClicked()) {
            buttonAnimator.onClick(main);
        }
    }


    private void onFinishLesson() {
        if (startElement != null) {
            if ((startElement.getId() == cards.get(0).getId() || startElement.getId() == -1)
                    && goodElement == null
                    && badElement == null) {

                timer.stopCount();
                Desk updatedDesk = getUpdatedDesk();
                viewModel.insertDesk(updatedDesk);

                showFinishDialog(updatedDesk);
                setVisibilityOnButtons(false);
                lessonDesk = viewModel.getDeskById(deskId);
                cards.clear();
                cards.addAll(viewModel.getCardsByDeskId(deskId));
                startElement = null;

                long currentTime = System.currentTimeMillis();
                if (updatedDesk.getScheduledDate() > currentTime
                        && lessonDesk.getRepetitionQuantity() > 5
                        && lessonDesk.getRepetitionQuantity() % 2 == 0) {
                    ComponentName componentName = new ComponentName(getApplicationContext(), RepetitionReminder.class);
                    int serviceId = (int) currentTime;
                    long scheduledInterval = updatedDesk.getScheduledDate() - currentTime;
                    PersistableBundle bundle = new PersistableBundle();
                    bundle.putString("desk_name", lessonDesk.getName());
                    JobInfo.Builder infoBuilder = new JobInfo.Builder(serviceId, componentName);
                    infoBuilder.setMinimumLatency(scheduledInterval)
                            .setOverrideDeadline(scheduledInterval)
                            .setPersisted(true)
                            .setExtras(bundle);
                    JobInfo jobInfo = infoBuilder.build();
                    JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                    jobScheduler.schedule(jobInfo);
                }
            }
        }
    }

    private void showFinishDialog(Desk updatedDesk) {
        DateWorker dateWorker = new DateWorker();
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_end_repetition);

        TextView textViewCurrentDuration = dialog.findViewById(R.id.textViewValueCurrentDuration);
        TextView textViewLastDuration = dialog.findViewById(R.id.textViewValueLastDuration);
        TextView textViewNewScheduledDate = dialog.findViewById(R.id.textViewValueScheduldDate);
        TextView textViewLastScheduledDate = dialog.findViewById(R.id.textViewValueLastScheduledDate);
        TextView textViewValueLastRepetitionDate = dialog.findViewById(R.id.textViewValueLastRepetitonDate);

        textViewLastDuration.setText(timer.getTimeAsString(lessonDesk.getLastRepeatDuration()));
        textViewCurrentDuration.setText(timer.getTimeAsString(timer.getSavedTotalSeconds()));
        textViewNewScheduledDate.setText(dateWorker.getFormattedDate(updatedDesk.getScheduledDate()));
        textViewLastScheduledDate.setText(dateWorker.getFormattedDate(lessonDesk.getScheduledDate()));
        textViewValueLastRepetitionDate.setText(dateWorker.getFormattedDate(updatedDesk.getLastRepetitionDate()));
        Log.i("log", "showFinishDialog: lessonDesk" + dateWorker.getFormattedDate(lessonDesk.getLastRepetitionDate()));
        dialog.show();
    }

    private Desk getUpdatedDesk() {
        DateWorker dateWorker = new DateWorker();
        long updatedLastRepetitionDate;
        int currentRepetitionDuration;
        if (lessonDesk.getRepetitionQuantity() % 2 != 0) {
            updatedLastRepetitionDate = dateWorker.getCurrentDate();
            currentRepetitionDuration = timer.getSavedTotalSeconds();
        } else {
            updatedLastRepetitionDate = lessonDesk.getLastRepetitionDate();
            currentRepetitionDuration = lessonDesk.getLastRepeatDuration();
        }

//        int currentRepetitionDuration = timer.getSavedTotalSeconds();
        long newScheduledDate = dateWorker.getScheduledDateNextRepetition(lessonDesk, currentRepetitionDuration);
        Log.i("logg", "getUpdatedDesk: " + dateWorker.getFormattedDate(newScheduledDate));
        int updatedRepetitionDay = dateWorker.getUpdatedRepetitionDay(lessonDesk); // method getUpdated
        int updatedRepetitionQuantity = lessonDesk.getRepetitionQuantity() + 1;
        boolean updatedSucceededLastRepetition = dateWorker.getUpdatedSucceededLastRepetition(lessonDesk, currentRepetitionDuration);

        //        viewModel.insertDesk(updatedDesk);
        return new Desk(
                deskId,
                lessonDesk.getName(),
                lessonDesk.getCreationDate(),
                updatedLastRepetitionDate,

                newScheduledDate,
                currentRepetitionDuration,
                updatedRepetitionDay,
                updatedRepetitionQuantity,
                updatedSucceededLastRepetition);
    }

    private void setVisibilityOnButtons(boolean visible) {
        Button buttonEasy = findViewById(R.id.buttonEasy);
        Button buttonGood = findViewById(R.id.buttonGood);
        Button buttonBad = findViewById(R.id.buttonBad);
        Button buttonTurn = findViewById(R.id.buttonTurn);
        Button buttonStartLesson = findViewById(R.id.buttonStartLesson);

        int visibility;
        int buttonStartLessonVisibility;
        if (visible) {
            visibility = View.VISIBLE;
            buttonStartLessonVisibility = View.INVISIBLE;
        } else {
            visibility = View.INVISIBLE;
            buttonStartLessonVisibility = View.VISIBLE;
        }

        buttonEasy.setVisibility(visibility);
        buttonGood.setVisibility(visibility);
        buttonBad.setVisibility(visibility);
        buttonTurn.setVisibility(visibility);
        buttonStartLesson.setVisibility(buttonStartLessonVisibility);
    }

}