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

    /**
     * 측정시간을 내림 후 second로 바꾸어주는 method
     *
     * @param time 측정시간
     * @return long
     */
    public static long mathFloorTime(long time) {
        final double millisecondToSecond = 1000.0;
        return (long) Math.floor(time / millisecondToSecond);
    }

}