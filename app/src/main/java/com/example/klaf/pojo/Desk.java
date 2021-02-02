package com.example.klaf.pojo;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "desk")
public class Desk {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private long lastRepetitionDate;
    private long scheduledDate;
    private double lastRepeatDuration;
    private int repetitionDay;
    private boolean succeededLastRepetition;


    public Desk(int id, String name, long lastRepetitionDate, long scheduledDate, double lastRepeatDuration, int repetitionDay, boolean succeededLastRepetition) {
        this.id = id;
        this.name = name;
        this.lastRepetitionDate = lastRepetitionDate;
        this.scheduledDate = scheduledDate;
        this.lastRepeatDuration = lastRepeatDuration;
        this.repetitionDay = repetitionDay;
        this.succeededLastRepetition = succeededLastRepetition;
    }

    @Ignore
    public Desk(String name, long currentTime, double lastRepeatDuration, int repetitionDay) {
        this.name = name;
        this.lastRepetitionDate = currentTime;
        this.scheduledDate = currentTime;
        this.lastRepeatDuration = lastRepeatDuration;
        this.repetitionDay = repetitionDay;
        this.succeededLastRepetition = true;
    }

    @Ignore
    public Desk(String name, long lastRepetitionDate, long scheduledDate, double lastRepeatDuration, int repetitionDay) {
        this.name = name;
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

    public double getLastRepeatDuration() {
        return lastRepeatDuration;
    }

    public long getLastRepetitionDate() {
        return lastRepetitionDate;
    }

    public long getScheduledDate() {
        return scheduledDate;
    }

    public int getRepetitionDay() {
        return repetitionDay;
    }

    public boolean isSucceededLastRepetition() {
        return succeededLastRepetition;
    }
}
