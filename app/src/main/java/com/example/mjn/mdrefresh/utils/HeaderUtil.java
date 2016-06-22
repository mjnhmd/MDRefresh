package com.example.mjn.mdrefresh.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HeaderUtil {
    public static boolean getIsNight(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        String hour= sdf.format(new Date());

        int hour_date= Integer.parseInt(hour);
        return (hour_date >= 19 && hour_date <= 23) || (hour_date >= 0 && hour_date <= 6);

    }
}
