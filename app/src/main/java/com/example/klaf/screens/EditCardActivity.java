package com.example.klaf.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
    List<CheckedLetterHolder> checkedLetterHolders;

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
        checkedLetterHolders = ipaProcesser.getCheckedLetterHolder(card.getIpa());
        LettersBarAdapter adapter = new LettersBarAdapter(checkedLetterHolders);

        editTextNativeWord.setText(card.getNativeWord());
        editTextForeignWord.setText(card.getForeignWord());
        editTextIpa.setText(ipaProcesser.getCompletedCouples(card.getIpa()));

        recyclerView.setAdapter(adapter);
        adapter.setOnClickHelper(new LettersBarAdapter.OnClickHelper() {
            @Override
            public void onItemClick(int position) {
                adapter.notifyDataSetChanged();
                editTextIpa.setText(ipaProcesser.getInCompletedCouples(checkedLetterHolders));
            }
        });

        editTextForeignWord.addTextChangedListener(new TextChangeWatcher(checkedLetterHolders, adapter, editTextIpa));

    }

    public void onClickApplyChanges(View view) {
        Card changeCard = getCard();
        if (changeCard != null) {
            viewModel.insertCard(changeCard);
            Toast.makeText(this, "new card was added", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "native and foreign words must be filled", Toast.LENGTH_SHORT).show();
        }
    }

    private Card getCard() {
        Card changedCard = null;
        IpaProcesser ipaProcesser = new IpaProcesser();
        int idDesk = card.getIdDesk();
        String nativeWord = editTextNativeWord.getText().toString().trim();
        String foreignWord = editTextForeignWord.getText().toString().trim();
        String ipaTemplate = editTextIpa.getText().toString().trim();
        String ipa = ipaProcesser.getCodedIpaForDB(checkedLetterHolders, ipaTemplate);
        if (!nativeWord.isEmpty() || !foreignWord.isEmpty()) {
            changedCard = new Card(cardId, idDesk, nativeWord, foreignWord, ipa);
        }
        return changedCard;
    }
}