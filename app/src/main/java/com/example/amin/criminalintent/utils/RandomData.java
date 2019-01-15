package com.example.amin.criminalintent.utils;

import java.util.Date;
import java.util.GregorianCalendar;

public class RandomData {
    public static Date randomDate() {
        GregorianCalendar gc = new GregorianCalendar();
        int year = randBetween(2000, 2018);
        gc.set(gc.YEAR, year);
        int dayOfYear = randBetween(1, gc.getActualMaximum(gc.DAY_OF_YEAR));
        gc.set(gc.DAY_OF_YEAR, dayOfYear);

        return gc.getTime();
//        System.out.println(gc.get(gc.YEAR) + "-" + (gc.get(gc.MONTH) + 1) + "-" + gc.get(gc.DAY_OF_MONTH));
    }

    public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }
}
