package com.example.klaf.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.klaf.pojo.Deck;

import java.util.List;

@Dao
public interface DeckDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insetDeck(Deck deck);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDeckList(List<Deck> decks);

    @Query("SELECT * FROM deck")
    LiveData<List<Deck>> getAllDecks();

    @Query("SELECT * FROM deck")
    List<Deck> getDeckList();

    @Query("SELECT * FROM deck WHERE id = :idDeck")
    Deck getDeckById(int idDeck);

    @Delete()
    void deleteDeck(Deck deck);

}
