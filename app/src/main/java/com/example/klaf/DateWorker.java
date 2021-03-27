package com.example.klaf;

import com.example.klaf.pojo.Deck;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateWorker {
    private static final double DERATION_FACTOR = 0.07;
    private static final double FIRST_DAY_FACTOR = 0.4;
    private static final double SECOND_DAY_FACTOR = 0.6;
    private static final double THIRD_DAY_FACTOR = 0.8;
//    private static final double FORTH_DAY_FACTOR = 0.7;
    private static final double WHOLE_DAY_FACTOR = 1.0;
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

    public long getScheduledDateNextRepetition(Deck desk, int currentRepetitionDuration) {
        long result;
        long newInterval;
        long lastInterval = desk.getScheduledDate() - desk.getLastRepetitionDate();

        if (desk.getRepetitionQuantity() >= 5) {
            if (desk.getRepetitionQuantity() == 5) {
                lastInterval = TimeUnit.MINUTES.toMillis(15);
            }

            if (desk.getRepetitionQuantity() % 2 != 0) {
                if (currentRepetitionDuration <= desk.getLastRepeatDuration() + desk.getLastRepeatDuration() * DERATION_FACTOR) {
                    newInterval = lastInterval + (long) (lastInterval * getDayFactorByNumberDay(desk.getRepetitionDay()));
                } else {
                    if (desk.isSucceededLastRepetition()) {
                        newInterval = lastInterval;
                    } else {
                        newInterval = lastInterval - (lastInterval * (long) DECREASE_FACTOR);
                    }
                }
                result = getCurrentDate() + newInterval;
            } else {
                result = desk.getScheduledDate();
            }
        } else {
            result = getCurrentDate();
        }
        return result;
    }

    public boolean isRepetitionSucceeded(Deck desk, int currentRepetitionDuration) {
        int lastRepetitionDuration = desk.getLastRepeatDuration();
        return currentRepetitionDuration <= lastRepetitionDuration + lastRepetitionDuration * DERATION_FACTOR;
    }

    public int getUpdatedRepetitionDay(Deck desk) {
        int date = desk.getRepetitionDay();
        long difference = getCurrentDate() - desk.getCreationDate();
        int differenceInDays = (int) difference / 86_400_000;
        return date < differenceInDays ? ++date : date;
    }

    private double getDayFactorByNumberDay(int numberDay) {
        double result;
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
//            case 4:
//                result = FORTH_DAY_FACTOR;
//                break;
            default:
                result = WHOLE_DAY_FACTOR;
        }
        return result;
    }
}
