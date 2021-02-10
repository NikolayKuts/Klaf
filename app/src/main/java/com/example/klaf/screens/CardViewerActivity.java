package com.example.klaf.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.klaf.R;
import com.example.klaf.adapters.CardViewerAdapter;
import com.example.klaf.pojo.Card;

import java.util.ArrayList;
import java.util.List;

public class CardViewerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CardViewerAdapter adapter;
    private int deskId;

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_viewer);

        recyclerView = findViewById(R.id.recyclerViewCardViewer);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        deskId = getIntent().getIntExtra(MainActivity.TAG_DESK_ID, -1);
        List<Card> cards = new ArrayList<>(viewModel.getCardsByDeskId(deskId));

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new CardViewerAdapter(cards);
        recyclerView.setAdapter(adapter);

    }
}