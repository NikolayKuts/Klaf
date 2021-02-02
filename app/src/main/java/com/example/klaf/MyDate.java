package com.example.klaf;

public class MyDate {
    private int day;
    private int month;
    private int year;
    private int hour;
    private int minute;

    public MyDate(String formattedDate) {
        day = Integer.parseInt(formattedDate.substring(0, 2));
        month = Integer.parseInt(formattedDate.substring(3, 5));
        year = Integer.parseInt(formattedDate.substring(6, 8));
        hour = Integer.parseInt(formattedDate.substring(9, 11));
        minute = Integer.parseInt(formattedDate.substring(12));
    }

}
