package com.example.klaf.data;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.klaf.pojo.Card;
import com.example.klaf.pojo.Deck;

@Database(entities = {Deck.class, Card.class}, version = 3, exportSchema = false)
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

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE desk " +
                    "RENAME TO deck;");

            database.execSQL("CREATE TABLE temporaryTable (" +
                    "id INTEGER PRIMARY KEY NOT NULL, " +
                    "idDeck INTEGER NOT NULL, " +
                    "nativeWord TEXT, " +
                    "foreignWord TEXT, " +
                    "ipa TEXT);");

            database.execSQL("INSERT INTO temporaryTable (" +
                    "id, idDeck, nativeWord, foreignWord, ipa) " +
                    "SELECT id, idDesk, nativeWord, foreignWord, ipa " +
                    "FROM card;");

            database.execSQL("DROP TABLE card;");

            database.execSQL("ALTER TABLE temporaryTable " +
                    "RENAME TO card;");
        }
    };

    public static KlafDatabase getInstance(Context context) {
        synchronized (LOCK) {
            database = Room.databaseBuilder(context, KlafDatabase.class, DB_NAME)
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build();
            return database;
        }
    }

    public abstract DeckDao deckDao();
    public abstract  CardDao cardDao();

}
