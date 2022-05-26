package com.hh.resume.helper;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateHelper {

    public static boolean isValidDate(String date){
        if (date == null) return false;
        String format = "yyyy-MM-dd";
        DateFormat dateFormat = new SimpleDateFormat(format);
        try {
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            System.out.printf("Error while parsing date:%s to format %s; %s\n", date, format, e.getMessage());
            return false;
        }
    }

    public static Date toDate(String date){
        return isValidDate(date) ? Date.valueOf(date) : null;
    }

    public static String toString(Date date){
        if (date == null) return "";
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }
}
