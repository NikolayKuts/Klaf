package com.example.klaf.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.klaf.DateWorker;
import com.example.klaf.R;
import com.example.klaf.adapters.DeckAdapter;
import com.example.klaf.pojo.Deck;
import com.example.klaf.services.RepetitionDayUpdater;
import com.example.klaf.services.RepetitionReminder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG_DECK_ID = "deck_id";

    private static boolean firstStart = true;
    private MainViewModel viewModel;
    private List<Deck> decks;
    private List<Integer> cardQuantityInDeck;
    private DeckAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewDecks);
        decks = new ArrayList<>();
        cardQuantityInDeck = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        adapter = new DeckAdapter(decks, cardQuantityInDeck);
        adapter.setOnDeckClickListener(new DeckAdapter.OnDeckClickListener() {
            @Override
            public void onDeckClick(int position) {
                Intent intent = new Intent(MainActivity.this, LessonActivity.class);
                intent.putExtra(TAG_DECK_ID, decks.get(position).getId());
                startActivity(intent);
            }
        });
        adapter.setOnDeckLongClickListener(new DeckAdapter.OnDeckLongClickListener() {
            @Override
            public void onDeckLongClick(int position) {
                Deck deck = decks.get(position);
                showGeneralDialog(deck);
            }
        });

        recyclerView.setAdapter(adapter);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        firstStart = sharedPreferences.getBoolean("first_start", true);

        if (firstStart) {
            sharedPreferences.edit().putBoolean("first_start", false).apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        decks.clear();
        decks.addAll(viewModel.getDeckList());
        cardQuantityInDeck.clear();
        cardQuantityInDeck.addAll(viewModel.getCardQuantityList());
        adapter.notifyDataSetChanged();
        updateRepetitionDate();
        runRepetitionDayUpdater(firstStart);
        runRepetitionReminder(firstStart);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        showInfoForChecking();
    }

    private void runRepetitionDayUpdater(boolean firstStart) {
        if (firstStart) {
            ComponentName componentName = new ComponentName(getApplicationContext(), RepetitionDayUpdater.class);
            JobInfo.Builder infoBuilder = new JobInfo.Builder(1123434324, componentName);
            infoBuilder.setMinimumLatency(10_000)
                    .setOverrideDeadline(10_000);
            JobInfo jobInfo = infoBuilder.build();
            JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(jobInfo);
        }
    }

    private void runRepetitionReminder(boolean firstStart) {
        if (firstStart) {
            for (Deck deck : decks) {
                long currentTime = System.currentTimeMillis();

                if (deck.getScheduledDate() > currentTime) {
                    ComponentName componentName = new ComponentName(getApplicationContext(), RepetitionReminder.class);
                    int serviceId = (int) currentTime;
                    long scheduledInterval = deck.getScheduledDate() - currentTime;
                    PersistableBundle bundle = new PersistableBundle();
                    bundle.putString("deck_name", deck.getName());

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

    private void updateRepetitionDate() {
        for (Deck deck : decks) {
            long daysInMilliseconds = System.currentTimeMillis() - deck.getCreationDate();
            double days = (double) daysInMilliseconds / 86_400_000;
            Log.i("log", "Deck: ");
            Log.i("log", "days  " + days);
            if (deck.getRepetitionDay() < days) {
                deck.setRepetitionDay((int) days + 1);
            }
        }
        viewModel.insertDeckList(decks);
    }


    public void onCreateDeck(View view) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_anim);
        view.startAnimation(animation);
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_deck);
        EditText editText = dialog.findViewById(R.id.editTextDeckName);
        Button buttonConfirm = dialog.findViewById(R.id.buttonConfirm);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);
        dialog.show();

        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        buttonConfirm.setOnClickListener(v -> {
            String deckName = editText.getText().toString().trim();

            if (deckName.isEmpty()) {
                Toast.makeText(MainActivity.this, "The field \"deck name\" can't be empty", Toast.LENGTH_SHORT).show();
            } else {
                DateWorker dateWorker = new DateWorker();
                long currentTime = dateWorker.getCurrentDate();
                Deck newDeck = new Deck(deckName, currentTime);
                viewModel.insertDeck(newDeck);
                onResume();
                dialog.dismiss();
            }
        });
    }

    private void showDeletingDeckDialog(Deck deck) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_remove_deck);

        TextView textViewDeckName = dialog.findViewById(R.id.textViewNameTebleForRemoving);
        Button buttonDelete = dialog.findViewById(R.id.buttonDeleteDeckRemoving);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancelDeckRemoving);

        buttonDelete.setOnClickListener(v -> {
            viewModel.deleteCardsByDeckId(deck.getId());
            viewModel.removeDeck(deck);
            dialog.dismiss();
        });
        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        textViewDeckName.setText(deck.getName());
        dialog.show();
    }

    private void showEditingDeckDialog(Deck deck) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_edit_deck);

        EditText editTextDeckName = dialog.findViewById(R.id.editTextDeckNameForChanging);
        Button buttonCancelChanges = dialog.findViewById(R.id.buttonCancleChanges);
        Button buttonApplyChangedDeckName = dialog.findViewById(R.id.buttonApplyChangedDeckName);

        editTextDeckName.setText(deck.getName());

        dialog.show();

        buttonCancelChanges.setOnClickListener(v -> dialog.dismiss());

        buttonApplyChangedDeckName.setOnClickListener(v -> {
            String deckName = editTextDeckName.getText().toString();
            Toast toast;

            if (deckName.isEmpty()) {
                toast = Toast.makeText(MainActivity.this, "The field \"deck name\" can't be empty", Toast.LENGTH_SHORT);
            } else {
                if (deck.getName().equals(deckName)) {
                    toast = Toast.makeText(MainActivity.this, "You haven't changed the name", Toast.LENGTH_SHORT);
                } else {
                    deck.setName(deckName);
                    viewModel.insertDeck(deck);
                    dialog.dismiss();
                    toast = Toast.makeText(MainActivity.this, "The name has been changed", Toast.LENGTH_SHORT);
                }
            }
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });

    }

    private void showGeneralDialog(Deck deck) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_general_on_deck);

        TextView textViewTitle = dialog.findViewById(R.id.textViewTitleGeneral);
        Button buttonCallEditingDialog = dialog.findViewById(R.id.buttonCallEditingDeckDialog);
        Button buttonCallDeletingDialog = dialog.findViewById(R.id.buttonCallDeletingDeckDialog);
        Button buttonShowCardsViewer = dialog.findViewById(R.id.buttonShowCardsViewer);
        Button buttonToAddCardActivity = dialog.findViewById(R.id.buttonToAddCardActivity);

        textViewTitle.setText(deck.getName());
        dialog.show();

        buttonCallDeletingDialog.setOnClickListener(v -> {
            showDeletingDeckDialog(deck);
            dialog.dismiss();
        });
        buttonCallEditingDialog.setOnClickListener(v -> {
            showEditingDeckDialog(deck);
            dialog.dismiss();
        });
        buttonShowCardsViewer.setOnClickListener(v -> {
            if (viewModel.getCardsByDeckId(deck.getId()).isEmpty()) {
                Toast.makeText(MainActivity.this, "The deck is empty", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, CardViewerActivity.class);
                intent.putExtra(TAG_DECK_ID, deck.getId());
                startActivity(intent);
                dialog.dismiss();
            }
        });
        buttonToAddCardActivity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
            intent.putExtra(TAG_DECK_ID, deck.getId());
            startActivity(intent);
            dialog.dismiss();
        });
    }

    private void showInfoForChecking() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        scheduler.cancelAll();
        List<JobInfo> jobInfos = scheduler.getAllPendingJobs();
        Log.i("log", "onResume: " + jobInfos.size());
        int i = 0;
        for (JobInfo jobinfo : jobInfos) {
            Log.i("log", "onResume: " + i++ + jobinfo.getId());
        }
    }

}