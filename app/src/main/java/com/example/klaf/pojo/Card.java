package com.example.klaf.pojo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "card")
public class Card {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;
    private final int idDeck;
    private final String nativeWord;
    private final String foreignWord;
    private final String ipa;

    @Ignore
    public Card(int idDeck, String nativeWord, String foreignWord, String ipa) {
        this.idDeck = idDeck;
        this.nativeWord = nativeWord;
        this.ipa = ipa;
        this.foreignWord = foreignWord;
    }

    public Card(int id, int idDeck, String nativeWord, String foreignWord, String ipa) {
        this.id = id;
        this.idDeck = idDeck;
        this.nativeWord = nativeWord;
        this.ipa = ipa;
        this.foreignWord = foreignWord;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdDeck() {
        return idDeck;
    }

    public String getNativeWord() {
        return nativeWord;
    }

    public String getIpa() {
        return ipa;
    }

    public String getForeignWord() {
        return foreignWord;
    }

//    @Override
//    public String toString() {
//        return nativeWord + "";
//    }

    public boolean compareOt(Card card) {
        return id == card.id
                && idDeck == card.idDeck
                && nativeWord.equals(card.nativeWord)
                && foreignWord.equals(card.foreignWord)
                && ipa.equals(card.ipa);
    }
}
