package com.hh.resume.helper;

public class StringHelper {

    public static boolean isValid(String s){
        return s != null && s.trim().length() > 0;
    }

    public static boolean isNumber(String s){
        if (s == null) return false;
        try {
            getNumber(s);
            return true;
        } catch (NumberFormatException e){
            e.printStackTrace();
            return false;
        }
    }

    public static Integer getNumber(String s){
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e){
            e.printStackTrace();
            return null;
        }
    }
}
