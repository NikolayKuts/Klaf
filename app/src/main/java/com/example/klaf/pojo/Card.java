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
    private int idDesk;
    private String nativeWord;
    private String foreignWord;
    private String ipa;

    @Ignore
    public Card(int idDesk, String nativeWord, String foreignWord, String ipa) {
        this.idDesk = idDesk;
        this.nativeWord = nativeWord;
        this.ipa = ipa;
        this.foreignWord = foreignWord;
    }

    public Card(int id, int idDesk, String nativeWord, String foreignWord, String ipa) {
        this.id = id;
        this.idDesk = idDesk;
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

    public int getIdDesk() {
        return idDesk;
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

    @Override
    public String toString() {
        return nativeWord + "";
    }

    public boolean compareOt(Card card) {
        return id == card.id
                && idDesk == card.idDesk
                && nativeWord.equals(card.nativeWord)
                && foreignWord.equals(card.foreignWord)
                && ipa.equals(card.ipa);
    }
}
