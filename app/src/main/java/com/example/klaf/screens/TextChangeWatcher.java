package com.example.klaf.screens;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.klaf.CheckedLetterHolder;
import com.example.klaf.IpaProcesser;
import com.example.klaf.adapters.LettersBarAdapter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TextChangeWatcher implements TextWatcher {
    private List<CheckedLetterHolder> holders;
    private LettersBarAdapter adapter;
    private EditText editTextIpa;

    public TextChangeWatcher(List<CheckedLetterHolder> holders, LettersBarAdapter adapter, EditText editTextIpa) {
        this.holders = holders;
        this.adapter = adapter;
        this.editTextIpa = editTextIpa;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        IpaProcesser ipaProcesser = new IpaProcesser();
        String foreignWord = s.toString();
        holders.clear();

        if (!foreignWord.isEmpty()) {
            String[] array = foreignWord.split("");
            LinkedList<String> arrayList = new LinkedList<>(Arrays.asList(array));
//            arrayList.remove(0);
            for (int i = 0; i < arrayList.size(); i++) {
                holders.add(new CheckedLetterHolder(arrayList.get(i), false));
            }
        }
        adapter.notifyDataSetChanged();
        editTextIpa.setText(ipaProcesser.getUncompletedCouples(holders));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
