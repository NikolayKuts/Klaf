package com.example.klaf.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.example.klaf.CheckedLetterHolder;
import com.example.klaf.IpaProcesser;
import com.example.klaf.R;
import com.example.klaf.adapters.LettersBarAdapter;
import com.example.klaf.pojo.Card;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditCardActivity extends AppCompatActivity {
    private int cardId;
    private MainViewModel viewModel;
    private Card card;

    private EditText editTextNativeWord;
    private EditText editTextForeignWord;
    private EditText editTextIpa;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);

        editTextNativeWord = findViewById(R.id.editTextEditNativeWor);
        editTextForeignWord = findViewById(R.id.editTextEditForeignWord);
        editTextIpa = findViewById(R.id.editTextEditIpa);
        recyclerView = findViewById(R.id.recyclerViewEditLettersBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false));

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        cardId = getIntent().getIntExtra(LessonActivity.TAG_CARD_ID, -1);
        card = viewModel.getCardById(cardId);

        IpaProcesser ipaProcesser = new IpaProcesser();
        List<CheckedLetterHolder> list = ipaProcesser.getCheckedLetterHolder(card.getIpa());
        LettersBarAdapter adapter = new LettersBarAdapter(list);

        editTextNativeWord.setText(card.getNativeWord());
        editTextForeignWord.setText(card.getForeignWord());
        editTextIpa.setText(ipaProcesser.getCompletedCouples(card.getIpa()));

        recyclerView.setAdapter(adapter);

    }
}