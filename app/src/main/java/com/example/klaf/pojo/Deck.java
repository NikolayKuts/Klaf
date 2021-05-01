package com.example.klaf.pojo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.klaf.DateWorker;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "deck")
public class Deck implements Serializable {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private long creationDate;
    private long lastRepetitionDate;
    private long scheduledDate;
    private int lastRepeatDuration;
    private int repetitionDay;
    private int repetitionQuantity;
    private boolean succeededLastRepetition;


    public Deck(
            int id,
            String name,
            long creationDate,
            long lastRepetitionDate,
            long scheduledDate,
            int lastRepeatDuration,
            int repetitionDay,
            int repetitionQuantity,
            boolean succeededLastRepetition) {

        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.lastRepetitionDate = lastRepetitionDate;
        this.scheduledDate = scheduledDate;
        this.lastRepeatDuration = lastRepeatDuration;
        this.repetitionDay = repetitionDay;
        this.repetitionQuantity = repetitionQuantity;
        this.succeededLastRepetition = succeededLastRepetition;
    }

    @Ignore
    public Deck(String name, long creationDate) {
        DateWorker dateWorker = new DateWorker();
        long currentDate = dateWorker.getCurrentDate();
        this.name = name;
        this.creationDate = creationDate;
        this.lastRepetitionDate = currentDate;
        this.scheduledDate = currentDate;
        this.lastRepeatDuration = 0;
        this.repetitionDay = 1;
        this.repetitionQuantity = 0;
        this.succeededLastRepetition = true;
    }

    @Ignore
    public Deck(
            String name,
            long creationDate,
            long lastRepetitionDate,
            long scheduledDate,
            int lastRepeatDuration,
            int repetitionDay) {

        this.name = name;
        this.creationDate = creationDate;
        this.lastRepetitionDate = lastRepetitionDate;
        this.scheduledDate = scheduledDate;
        this.lastRepeatDuration = lastRepeatDuration;
        this.repetitionDay = repetitionDay;
        this.succeededLastRepetition = true;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public long getLastRepetitionDate() {
        return lastRepetitionDate;
    }

    public long getScheduledDate() {
        return scheduledDate;
    }

    public int getLastRepeatDuration() {
        return lastRepeatDuration;
    }

    public int getRepetitionDay() {
        return repetitionDay;
    }

    public void setRepetitionDay(int day) {
        repetitionDay = day;
    }

    public int getRepetitionQuantity() {
        return repetitionQuantity;
    }

    public boolean isSucceededLastRepetition() {
        return succeededLastRepetition;
    }

    public void setName(String name) {
        this.name = name;
    }
}
