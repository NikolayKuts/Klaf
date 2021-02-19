package com.example.klaf.data;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.klaf.pojo.Card;
import com.example.klaf.pojo.Desk;

@Database(entities = {Desk.class, Card.class}, version = 2, exportSchema = false)
public abstract class KlafDatabase extends RoomDatabase {
    private static KlafDatabase database;
    private static final String DB_NAME = "klaf.db";
    private static final Object LOCK = new Object();

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE desk " +
                    "ADD COLUMN repetitionQuantity INTEGER NOT NULL DEFAULT 0;");
        }
    };

    public static KlafDatabase getInstance(Context context) {
        synchronized (LOCK) {
            database = Room.databaseBuilder(context, KlafDatabase.class, DB_NAME)
                    .addMigrations(MIGRATION_1_2)
                    .build();
            return database;
        }
    }

    public abstract DeskDao deskDao();
    public abstract  CardDao cardDao();

}
