package com.example.klaf.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.klaf.pojo.Card;

import java.util.List;

import io.reactivex.internal.operators.observable.ObservableNever;

@Dao
public interface CardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insetCard(Card card);

    @Query("SELECT * FROM card")
    LiveData<List<Card>> getAllCards();

    @Query("SELECT * FROM card WHERE idDeck = :idDeckTable")
    List<Card> getCardsByDeck(int idDeckTable);

    @Query("SELECT * FROM card WHERE id = :cardId")
    Card getCardById(int cardId);

    @Delete
    void deleteCard(Card card);

    @Query("DELETE FROM card WHERE idDeck = :deckId")
    void deleteCardsByDeckId(int deckId);

    @Query("SELECT COUNT(*) FROM card WHERE idDeck = :deckId")
    int getCardQuantityByDeckId(int deckId);
}
