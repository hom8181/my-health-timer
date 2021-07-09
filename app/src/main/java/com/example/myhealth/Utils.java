package com.example.myhealth;

public class Utils {

    /**
     * 측정시간을 HH:mm 형태의 String으로 나타내 주는 method
     *
     * @param seconds 측정시간
     * @return String
     */
    public static String viewTime(long seconds) {
        String record = "";
        long minute = seconds / 60;
        long second = seconds % 60;

        if (minute >= 10 && second >= 10) {
            record = minute + ":" + second;
        }
        if (minute < 10 && second >= 10) {
            record = "0" + minute + ":" + second;
        }
        if (minute >= 10 && second < 10) {
            record = minute + ":" + "0" + second;
        }
        if (minute < 10 && second < 10) {
            record = "0" + minute + ":" + "0" + second;
        }
        return record;
    }

}