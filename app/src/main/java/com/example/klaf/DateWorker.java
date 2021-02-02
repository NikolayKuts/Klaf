package com.example.klaf;

import com.example.klaf.pojo.Desk;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateWorker {
    private static final double DERATION_FACTOR = 0.1;
    private static final double FIRST_DAY_FACTOR = 0.25;
    private static final double SECOND_DAY_FACTOR = 0.40;
    private static final double THIRD_DAY_FACTOR = 0.55;
    private static final double FORTH_DAY_FACTOR = 0.75;
    private static final double DECREASE_FACTOR = 0.1;


    public String getFormattedCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy|HH:mm", Locale.getDefault());
        return dateFormat.format(date);
    }

    public String getFormattedDate(long date) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy|HH:mm", Locale.getDefault());
        return dateFormat.format(date);
    }

    public long getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        return date.getTime();
    }

    public long getScheduledDateNextRepetition(Desk desk, int currentRepetitionDuration) {
        long newInterval;
        long lastInterval = desk.getScheduledDate() - desk.getLastRepetitionDate();

        if (currentRepetitionDuration <= desk.getLastRepeatDuration() + desk.getLastRepeatDuration() * DERATION_FACTOR) {
            newInterval = lastInterval + (lastInterval * getDayFactorByNumberDay(desk.getRepetitionDay()));
        } else {
            if (desk.isSucceededLastRepetition()) {
                newInterval = lastInterval;
            } else {
                newInterval = lastInterval - (lastInterval * (long) DECREASE_FACTOR);
            }
        }
        return getCurrentDate() + newInterval;
    }

    public boolean getUpdatedSucceededLastRepetition(Desk desk, int currentRepetitionDuration) {
        int lastRepetitionDuration = desk.getLastRepeatDuration();
        return currentRepetitionDuration + lastRepetitionDuration * DERATION_FACTOR <= lastRepetitionDuration;
    }

    public int getUpdatedRepetitionDay(Desk desk) {
        int date = desk.getRepetitionDay();
        long difference = getCurrentDate() - desk.getCreationDate();
        int differenceInDays = (int) difference / 86_400_000;
        return date < differenceInDays ? ++date : date;
    }

//    public long getScheduledTimeNextRepetition(long lastRepetitionDate, long lastScheduledDate, int repetitionDay,
//                                               int lastRepetitionDuration, int currentRepetitionDuration, boolean succeededLastRepetition) {
//        long newInterval;
//        long lastInterval = lastScheduledDate - lastRepetitionDate;
//
//        if (currentRepetitionDuration + lastRepetitionDuration * DERATION_FACTOR <= lastRepetitionDuration) {
//            newInterval = lastInterval + (lastInterval * (long) getDayFactorByNumberDay(repetitionDay));
//        } else {
//            if (succeededLastRepetition) {
//                newInterval = lastInterval;
//            } else {
//                newInterval = lastInterval - (lastInterval * (long) DECREASE_FACTOR);
//            }
//        }
//        return getCurrentDate() + newInterval;
//    }

    private long getDayFactorByNumberDay(int numberDay) {
        double result = 0.0;
        switch (numberDay) {
            case 1:
                result = FIRST_DAY_FACTOR;
                break;
            case 2:
                result = SECOND_DAY_FACTOR;
                break;
            case 3:
                result = THIRD_DAY_FACTOR;
                break;
            case 4:
                result = FORTH_DAY_FACTOR;
        }
        return (long) result;
    }
}
