package com.example.klaf.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
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

    @Query("SELECT * FROM card WHERE idDesk = :idDeskTable")
    List<Card> getCardsByDesk(int idDeskTable);
}
