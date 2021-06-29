package com.example.klaf.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.klaf.CheckedLetterHolder;
import com.example.klaf.IpaProcesser;
import com.example.klaf.R;
import com.example.klaf.adapters.LettersBarAdapter;
import com.example.klaf.pojo.Card;
import com.example.klaf.pojo.Deck;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddCardActivity extends AppCompatActivity {
    private EditText editTextNativeWord;
    private EditText editTextForeignWord;
    private EditText editTextIpa;
    private TextView textViewDeskName;
    private TextView textViewDeskQuantity;
    private int deskId;
    private RecyclerView recyclerViewLettersBar;

    private List<CheckedLetterHolder> checkedLetterHolders = new ArrayList<>();

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        editTextNativeWord = findViewById(R.id.editTextNativeWor);
        editTextForeignWord = findViewById(R.id.editTexForeignWord);
        editTextIpa = findViewById(R.id.editTextIpa);
        textViewDeskName = findViewById(R.id.textViewAddCardActivityDeskName);
        textViewDeskQuantity = findViewById(R.id.textViewCardQuantity);
        recyclerViewLettersBar = findViewById(R.id.recyclerViewLettersBar);

        deskId = getIntent().getIntExtra(MainActivity.TAG_DECK_ID, -1);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getCards().observe(this, new Observer<List<Card>>() {
            @Override
            public void onChanged(List<Card> cards) {
                int quantity = viewModel.getCardQuantityByDeckId(deskId);
                textViewDeskQuantity.setText(String.format(Locale.getDefault(), "%d", quantity));
            }
        });

        recyclerViewLettersBar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        LettersBarAdapter lettersBarAdapter = new LettersBarAdapter(checkedLetterHolders);
        recyclerViewLettersBar.setAdapter(lettersBarAdapter);
        lettersBarAdapter.setOnClickHelper(new LettersBarAdapter.OnClickHelper() {
            @Override
            public void onItemClick(int position) {
                lettersBarAdapter.notifyDataSetChanged();
                IpaProcesser ipaProcesser = new IpaProcesser();
                editTextIpa.setText(ipaProcesser.getUncompletedCouples(checkedLetterHolders));
            }
        });

        editTextForeignWord.addTextChangedListener(new TextChangeWatcher(checkedLetterHolders, lettersBarAdapter, editTextIpa));
        Deck desk = viewModel.getDeckById(deskId);
        textViewDeskName.setText(desk.getName());

    }

    public void onClickAddCard(View view) {
        Card newCard = getCard();
        Toast toast;
        if (newCard != null) {
            viewModel.insertCard(newCard);
            toast = Toast.makeText(this, "new card has been added", Toast.LENGTH_LONG);
            clearViews();
        } else {

            toast = Toast.makeText(this, "native and foreign words must be filled", Toast.LENGTH_SHORT);
        }
        toast.setGravity(Gravity.CENTER, 0, 100);
        toast.show();
    }

    private void clearViews() {
        editTextNativeWord.setText("");
        editTextForeignWord.setText("");
        editTextIpa.setText("");

    }

    private Card getCard() {
        IpaProcesser ipaProcesser = new IpaProcesser();
        String nativeWord = this.editTextNativeWord.getText().toString().trim();
        String foreign = editTextForeignWord.getText().toString().trim();
        String ipa = ipaProcesser.getCodedIpaForDB(checkedLetterHolders, editTextIpa.getText().toString().trim());
        if (!nativeWord.isEmpty() && !foreign.isEmpty()) {
            return new Card(deskId, nativeWord, foreign, ipa);
        }
        return null;
    }
}