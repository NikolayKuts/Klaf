package com.example.klaf;

import android.util.Log;

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


//    public String getCurrentTime() {
//        Calendar calendar = Calendar.getInstance();
//        Date date = calendar.getTime();
//        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy|hh:mm", Locale.getDefault());
//        return dateFormat.format(date);
//    }

    public long getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        return date.getTime();
    }

    public long getScheduledTimeNextRepetition(long lastRepetitionDate, long lastScheduledDate, int repetitionDay,
                                               int lastRepetitionDuration, int currentRepetitionDuration, boolean succeededLastRepetition) {
        long newInterval;
        long lastInterval = lastScheduledDate - lastRepetitionDate;

        if (currentRepetitionDuration + lastRepetitionDuration * DERATION_FACTOR <= lastRepetitionDuration) {
            newInterval = lastInterval + (lastInterval * (long) getDayFactorByNumberDay(repetitionDay));
        } else {
            if (succeededLastRepetition) {
                newInterval = lastInterval;
            } else {
                newInterval = lastInterval - (lastInterval * (long) DECREASE_FACTOR);
            }
        }
        return getCurrentTime() + newInterval;
    }

    private double getDayFactorByNumberDay(int numberDay) {
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
        return result;
    }
}
