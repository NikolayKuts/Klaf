package com.example.klaf.pojo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.klaf.DateWorker;

import java.util.List;

@Entity(tableName = "desk")
public class Desk {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private long creationDate;
    private long lastRepetitionDate;
    private long scheduledDate;
    private int lastRepeatDuration;
    private int repetitionDay;
    private boolean succeededLastRepetition;


    public Desk(int id, String name, long creationDate, long lastRepetitionDate, long scheduledDate, int lastRepeatDuration, int repetitionDay, boolean succeededLastRepetition) {
        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.lastRepetitionDate = lastRepetitionDate;
        this.scheduledDate = scheduledDate;
        this.lastRepeatDuration = lastRepeatDuration;
        this.repetitionDay = repetitionDay;
        this.succeededLastRepetition = succeededLastRepetition;
    }

    @Ignore
    public Desk(String name, long creationDate) {
        DateWorker dateWorker = new DateWorker();
        long currentDate = dateWorker.getCurrentDate();
        this.name = name;
        this.creationDate = creationDate;
        this.lastRepetitionDate = currentDate;
        this.scheduledDate = currentDate + 600000;
        this.lastRepeatDuration = 900000000;
        this.repetitionDay = 1;
        this.succeededLastRepetition = true;
    }

    @Ignore
    public Desk(String name, long creationDate, long lastRepetitionDate, long scheduledDate, int lastRepeatDuration, int repetitionDay) {
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

    public boolean isSucceededLastRepetition() {
        return succeededLastRepetition;
    }

    public void setName(String name) {
        this.name = name;
    }
}
