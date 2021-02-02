package com.example.klaf.data;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.klaf.pojo.Card;
import com.example.klaf.pojo.Desk;

@Database(entities = {Desk.class, Card.class}, version = 1, exportSchema = false)
public abstract class KlafDatabase extends RoomDatabase {
    private static KlafDatabase database;
    private static final String DB_NAME = "klaf.db";
    private static final Object LOCK = new Object();

    public static KlafDatabase getInstance(Context context) {
        synchronized (LOCK) {
            database = Room.databaseBuilder(context, KlafDatabase.class, DB_NAME)
                    .build();
            return database;
        }
    }

    public abstract DeskDao deskDao();
    public abstract  CardDao cardDao();

}
