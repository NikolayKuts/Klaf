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
import android.widget.EditText;
import android.widget.TextView;

import com.example.klaf.DateWorker;
import com.example.klaf.R;
import com.example.klaf.adapters.DeskAdapter;
import com.example.klaf.pojo.Card;
import com.example.klaf.pojo.Desk;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;
    private List<Desk> desks;
    private List<Card> cards;
    private DeskAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewDesks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        desks = new ArrayList<>();
        Log.i("log", "before_observe");
        viewModel.getDesks().observe(this, new Observer<List<Desk>>() {
            @Override
            public void onChanged(List<Desk> desksFromDB) {
                desks.clear();
                desks.addAll(desksFromDB);
                adapter.notifyDataSetChanged();

            }
        });

        adapter = new DeskAdapter(desks, viewModel);
        adapter.setOnDeskClickListener(new DeskAdapter.OnDeskClickListener() {
            @Override
            public void onDeskClick(int position) {
                Intent intent = new Intent(MainActivity.this, LessonActivity.class);
                intent.putExtra("id_desk", desks.get(position).getId());
                startActivity(intent);
            }
        });
        adapter.setOnDeskLongClickListener(new DeskAdapter.OnDeskLongClickListener() {
            @Override
            public void onDeckLongClick(int position) {
                showRemoveDeskDialog(position);
            }
        });

        recyclerView.setAdapter(adapter);



//        deleteDatabase("klaf.db");
//        Desk desk2 = new Desk("test", new DateWorker().getCurrentDate());
//        viewModel.insertDesk(desk2);

//        LiveData<List<Desk>> liveData = viewModel.getDesks();
//        List<Desk> arrayList = liveData.getValue();
//        desks.addAll(viewModel.getDesks().getValue());

//        Desk desk3 = new Desk("name3", "start date3", "end date3", 0.0);
//
//        Desk desk4 = new Desk("name3", "start date3", "end date3", 0.0);
//        Desk desk5 = new Desk("name3", "start date3", "end date3", 0.0);
//        Desk desk6 = new Desk("name3", "start date3", "end date3", 0.0);
//        Desk desk7 = new Desk("name3", "start date3", "end date3", 0.0);
//
//
//        Desk desk1 = new Desk("name1", "start date1", "end date1", 0.0);
//        viewModel.insertDesk(desk1);
//        for (int i = 0; i < 10; i++) {
//            Log.i("log", "isn't empty");
//            viewModel.insertCard(new Card(1, "слово_" + i, "word_" + i, "[fafda]_" + i));
//        }
//        viewModel.insertDesk(desk3);
//
//        viewModel.insertDesk(desk4);
//        viewModel.insertDesk(desk5);
//        viewModel.insertDesk(desk6);
//        viewModel.insertDesk(desk7);
//////////////////////////////////////
//        ComponentName componentName = new ComponentName(getApplicationContext(), CheckerJobService.class);
//        JobInfo.Builder infoBuilder = new JobInfo.Builder(1234567, componentName);
//        infoBuilder.setMinimumLatency(10000)
//                .setOverrideDeadline(10000);
//        JobInfo jobInfo = infoBuilder.build();
//        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        jobScheduler.schedule(jobInfo);

    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    public void onAddNewDesk(View view) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click_anim);
        view.startAnimation(animation);
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_desk);
        EditText editText = dialog.findViewById(R.id.editTextDeskName);
        Button buttonConfirm = dialog.findViewById(R.id.buttonConfirm);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);
        dialog.show();
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deskName = editText.getText().toString();

                DateWorker dateWorker = new DateWorker();
                long currentTime = dateWorker.getCurrentDate();
                Desk newDesk = new Desk(deskName, currentTime);
                viewModel.insertDesk(newDesk);
                if (!deskName.isEmpty()) {
                    dialog.dismiss();
                }
            }
        });
    }

    private void showRemoveDeskDialog(int position) {
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_remove_desk);

        TextView textViewDeskName = dialog.findViewById(R.id.textViewNameTebleForRemoving);
        Button buttonDelete = dialog.findViewById(R.id.buttonDeleteDeskRemoving);
        Button buttonCancel = dialog.findViewById(R.id.buttonCancelDeskRemoving);

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.removeDesk(desks.get(position));
                dialog.dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        textViewDeskName.setText(desks.get(position).getName());
        dialog.show();
    }

}