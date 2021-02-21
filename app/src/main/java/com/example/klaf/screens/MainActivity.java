package com.example.klaf.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.klaf.adapters.DeskAdapter;
import com.example.klaf.pojo.Card;
import com.example.klaf.pojo.Desk;
import com.example.klaf.services.CheckerJobService;
import com.example.klaf.services.RepetitionDayUpdater;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG_DESK_ID = "desk_id";

    private static boolean firstStart = true;
    private MainViewModel viewModel;
    private List<Desk> desks;
    private List<Integer> cardQuantityInDesk;
    private List<Card> cards;
    private DeskAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewDesks);
        desks = new ArrayList<>();
        cardQuantityInDesk = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getDesks().observe(this, new Observer<List<Desk>>() {
            @Override
            public void onChanged(List<Desk> desksFromDB) {
                desks.clear();
                desks.addAll(desksFromDB);
                cardQuantityInDesk.clear();
                cardQuantityInDesk.addAll(viewModel.getCardQuantityList());
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new DeskAdapter(desks, cardQuantityInDesk);
        adapter.setOnDeskClickListener(new DeskAdapter.OnDeskClickListener() {
            @Override
            public void onDeskClick(int position) {
                Intent intent = new Intent(MainActivity.this, LessonActivity.class);
                intent.putExtra(TAG_DESK_ID, desks.get(position).getId());
                startActivity(intent);
            }
        });
        adapter.setOnDeskLongClickListener(new DeskAdapter.OnDeskLongClickListener() {
            @Override
            public void onDeckLongClick(int position) {
                Desk desk = desks.get(position);
                showGeneralDialog(desk);
            }
        });

        recyclerView.setAdapter(adapter);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        firstStart = sharedPreferences.getBoolean("first_start", true);

        runRepetitionDayUpdater(firstStart);

        if (firstStart) {
            sharedPreferences.edit().putBoolean("first_start", false).apply();
        }

//        deleteDatabase("klaf.db");
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

    @Override
    protected void onResume() {
        super.onResume();
        desks.clear();
        desks.addAll(viewModel.getDeskList());
        cardQuantityInDesk.clear();
        cardQuantityInDesk.addAll(viewModel.getCardQuantityList());
        adapter.notifyDataSetChanged();
    }

    public void onCreateDesk(View view) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_anim);
        view.startAnimation(animation);
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_desk);
        EditText editText = dialog.findViewById(R.id.editTextDeskName);
        Button buttonConfirm = dialog.findViewById(R.id.buttonConfirm);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);
        dialog.show();

        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        buttonConfirm.setOnClickListener(v -> {
            String deskName = editText.getText().toString().trim();

            if (deskName.isEmpty()) {
                Toast.makeText(MainActivity.this, "The field \"desk name\" can't be empty", Toast.LENGTH_SHORT).show();
            } else {
                DateWorker dateWorker = new DateWorker();
                long currentTime = dateWorker.getCurrentDate();
                Desk newDesk = new Desk(deskName, currentTime);
                viewModel.insertDesk(newDesk);
                dialog.dismiss();
            }
        });
    }

    private void showDeletingDeskDialog(Desk desk) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_remove_desk);

        TextView textViewDeskName = dialog.findViewById(R.id.textViewNameTebleForRemoving);
        Button buttonDelete = dialog.findViewById(R.id.buttonDeleteDeskRemoving);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancelDeskRemoving);

        buttonDelete.setOnClickListener(v -> {
            viewModel.deleteCardsByDeskId(desk.getId());
            viewModel.removeDesk(desk);
            dialog.dismiss();
        });
        buttonCancel.setOnClickListener(v -> dialog.dismiss());
        textViewDeskName.setText(desk.getName());
        dialog.show();
    }

    private void showEditingDeskDialog(Desk desk) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_edit_desk);

        EditText editTextDeskName = dialog.findViewById(R.id.editTextDeskNameForChanging);
        Button buttonCancelChanges = dialog.findViewById(R.id.buttonCancleChanges);
        Button buttonApplyChangedDeskName = dialog.findViewById(R.id.buttonApplyChangedDeskName);

        editTextDeskName.setText(desk.getName());

        dialog.show();

        buttonCancelChanges.setOnClickListener(v -> dialog.dismiss());

        buttonApplyChangedDeskName.setOnClickListener(v -> {
            String deskName = editTextDeskName.getText().toString();
            Toast toast;

            if (deskName.isEmpty()) {
                toast = Toast.makeText(MainActivity.this, "The field \"desk name\" can't be empty", Toast.LENGTH_SHORT);
            } else {
                if (desk.getName().equals(deskName)) {
                    toast = Toast.makeText(MainActivity.this, "You haven't changed the name", Toast.LENGTH_SHORT);
                } else {
                    desk.setName(deskName);
                    viewModel.insertDesk(desk);
                    dialog.dismiss();
                    toast = Toast.makeText(MainActivity.this, "The name has been changed", Toast.LENGTH_SHORT);
                }
            }
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });

    }

    private void showGeneralDialog(Desk desk) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_general_on_desk);

        TextView textViewTitle = dialog.findViewById(R.id.textViewTitleGeneral);
        Button buttonCallEditingDialog = dialog.findViewById(R.id.buttonCallEditingDeskDialog);
        Button buttonCallDeletingDialog = dialog.findViewById(R.id.buttonCallDeletingDeskDialog);
        Button buttonShowCardsViewer = dialog.findViewById(R.id.buttonShowCardsViewer);
        Button buttonToAddCardActivity = dialog.findViewById(R.id.buttonToAddCardActivity);

        textViewTitle.setText(desk.getName());

        dialog.show();

        buttonCallDeletingDialog.setOnClickListener(v -> {
            showDeletingDeskDialog(desk);
            dialog.dismiss();
        });
        buttonCallEditingDialog.setOnClickListener(v -> {
            showEditingDeskDialog(desk);
            dialog.dismiss();
        });
        buttonShowCardsViewer.setOnClickListener(v -> {
            if (viewModel.getCardsByDeskId(desk.getId()).isEmpty()) {
                Toast.makeText(MainActivity.this, "The desk is empty", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, CardViewerActivity.class);
                intent.putExtra(TAG_DESK_ID, desk.getId());
                startActivity(intent);
                dialog.dismiss();
            }
        });
        buttonToAddCardActivity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
            intent.putExtra(TAG_DESK_ID, desk.getId());
            startActivity(intent);
            dialog.dismiss();
        });
    }

}