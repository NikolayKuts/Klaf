package com.example.klaf.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.klaf.pojo.Desk;

import java.util.List;

@Dao
public interface DeskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insetDesk(Desk desk);

    @Query("SELECT * FROM desk")
    LiveData<List<Desk>> getAllDesks();

    @Query("SELECT * FROM desk WHERE id = :idDesk")
    Desk getDeskById(int idDesk);

    @Delete()
    void deleteDesk(Desk desk);

}
