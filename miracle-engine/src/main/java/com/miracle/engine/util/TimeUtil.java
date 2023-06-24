package com.miracle.engine.util;

import android.content.Context;

import com.miracle.engine.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeUtil {
    private static final long ONE_MINUTE_SEC = 60;
    private static final long ONE_HOUR_SEC = 3600;
    private static final long ONE_DAY_SEC = 86400;
    private static final long ONE_WEEK_SEC = 604800;
    private static final long ONE_MOTH_SEC = 2678400;
    private static final long ONE_YEAR_SEC = 31536000;

    public static String getLowDateString(Context context, long dateSec){
        long currentDate = System.currentTimeMillis();
        long currentDateSec = currentDate/1000;
        long deltaSec = currentDateSec - dateSec;

        if(deltaSec<0){
            SimpleDateFormat shorFormat = new SimpleDateFormat("H:mm", Locale.ENGLISH);
            long date = dateSec*1000;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            int moth = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            return months(context, moth, dayOfMonth)+" "+context.getString(R.string.formatted_date_in, shorFormat.format(date));
        }

        if(deltaSec<5){
            return context.getString(R.string.formatted_date_now1);
        }

        if(deltaSec<ONE_MINUTE_SEC){
            return secs(context, (int) deltaSec);
        }

        if(deltaSec<ONE_HOUR_SEC){
            long minutesDelta = deltaSec/ONE_MINUTE_SEC;
            return minutes(context, (int) minutesDelta);
        }

        if(deltaSec<ONE_HOUR_SEC*2){
            return context.getString(R.string.formatted_hour_ago1);
        }

        long date = dateSec*1000;
        Calendar calendarCurrent = Calendar.getInstance();
        calendarCurrent.setTimeInMillis(currentDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);

        int currentYear = calendarCurrent.get(Calendar.YEAR);
        int year = calendar.get(Calendar.YEAR);
        int moth = calendar.get(Calendar.MONTH);
        int currentDayOfYear = calendarCurrent.get(Calendar.DAY_OF_YEAR);
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        if(currentYear==year){

            SimpleDateFormat shorFormat = new SimpleDateFormat("H:mm", Locale.ENGLISH);

            if(currentDayOfYear==dayOfYear){
                return context.getString(R.string.formatted_date_today1, shorFormat.format(date));
            }
            if(currentDayOfYear-1==dayOfYear){
                return context.getString(R.string.formatted_date_yesterday1, shorFormat.format(date));
            }
            return months(context, moth, dayOfMonth)+" "+context.getString(R.string.formatted_date_in, shorFormat.format(date));
        } else {
            return months(context, moth, dayOfMonth)+" "+year;
        }
    }

    public static String getDateString(Context context, long dateSec){

        long currentDate = System.currentTimeMillis();
        long currentDateSec = currentDate/1000;
        long deltaSec = currentDateSec - dateSec;

        if(deltaSec<0){
            SimpleDateFormat shorFormat = new SimpleDateFormat("H:mm", Locale.ENGLISH);
            long date = dateSec*1000;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            int moth = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            return months(context, moth, dayOfMonth)+" "+context.getString(R.string.formatted_date_in, shorFormat.format(date));
        }

        if(deltaSec<5){
            return context.getString(R.string.formatted_date_now);
        }

        if(deltaSec<ONE_MINUTE_SEC){
            return secs(context, (int) deltaSec);
        }

        if(deltaSec<ONE_HOUR_SEC){
            long minutesDelta = deltaSec/ONE_MINUTE_SEC;
            return minutes(context, (int) minutesDelta);
        }

        if(deltaSec<ONE_HOUR_SEC*2){
            return context.getString(R.string.formatted_hour_ago);
        }

        long date = dateSec*1000;
        Calendar calendarCurrent = Calendar.getInstance();
        calendarCurrent.setTimeInMillis(currentDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);

        int currentYear = calendarCurrent.get(Calendar.YEAR);
        int year = calendar.get(Calendar.YEAR);
        int moth = calendar.get(Calendar.MONTH);
        int currentDayOfYear = calendarCurrent.get(Calendar.DAY_OF_YEAR);
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        if(currentYear==year){

            SimpleDateFormat shorFormat = new SimpleDateFormat("H:mm", Locale.ENGLISH);

            if(currentDayOfYear==dayOfYear){
                return context.getString(R.string.formatted_date_today, shorFormat.format(date));
            }
            if(currentDayOfYear-1==dayOfYear){
                return context.getString(R.string.formatted_date_yesterday, shorFormat.format(date));
            }
            return months(context, moth, dayOfMonth)+" "+context.getString(R.string.formatted_date_in, shorFormat.format(date));
        } else {
            return months(context, moth, dayOfMonth)+" "+year;
        }
    }

    public static String getShortDateString(Context context, long dateSec){

        long currentDate = System.currentTimeMillis();
        long currentDateSec = currentDate/1000;
        long deltaSec = currentDateSec - dateSec;


        if(deltaSec<ONE_MINUTE_SEC){
            return "";
        }

        if(deltaSec<ONE_HOUR_SEC){
            long minutesDelta = deltaSec/ONE_MINUTE_SEC;
            return String.format(context.getString(R.string.formatted_minute_ago2), minutesDelta);
        }

        if(deltaSec<ONE_DAY_SEC){
            long hoursDelta = deltaSec/ONE_HOUR_SEC;
            return String.format(context.getString(R.string.formatted_hour_ago2), hoursDelta);
        }

        if(deltaSec<ONE_WEEK_SEC){
            long dayDelta = deltaSec/ONE_DAY_SEC;
            return String.format(context.getString(R.string.formatted_day_ago2), dayDelta);
        }

        if(deltaSec<ONE_MOTH_SEC){
            long weekDelta = deltaSec/ONE_WEEK_SEC;
            return String.format(context.getString(R.string.formatted_week_ago2), weekDelta);
        }

        if(deltaSec<ONE_YEAR_SEC){
            long monthDelta = deltaSec/ONE_MOTH_SEC;
            return String.format(context.getString(R.string.formatted_minute_ago2), monthDelta);
        }

        long yearDelta = deltaSec/ONE_YEAR_SEC;
        return String.format(context.getString(R.string.formatted_year_ago2), yearDelta);

    }

    public static String secs(Context context, int sec){
        if(sec<20 && sec>10) {
            return context.getString(R.string.formatted_date_sec2, sec);
        }

        switch (sec%10){
            default:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 0:{
                return context.getString(R.string.formatted_date_sec2, sec);
            }
            case 1:{
                return context.getString(R.string.formatted_date_sec3, sec);
            }
            case 3:
            case 4:
            case 2:{
                return context.getString(R.string.formatted_date_sec1, sec);
            }
        }
    }

    public static String minutes(Context context, int min){
        if(min<20 && min>10) {
            return context.getString(R.string.formatted_date_min2, min);
        }

        switch (min%10){
            default:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 0:{
                return context.getString(R.string.formatted_date_min2, min);
            }
            case 1:{
                return context.getString(R.string.formatted_date_min3, min);
            }
            case 3:
            case 4:
            case 2:{
                return context.getString(R.string.formatted_date_min1, min);
            }
        }
    }

    public static int getMonthStringRecourseId(int month){
        switch (month){
            default:
            case 0: return R.string.formatted_date_M1;
            case 1: return R.string.formatted_date_M2;
            case 2: return R.string.formatted_date_M3;
            case 3: return R.string.formatted_date_M4;
            case 4: return R.string.formatted_date_M5;
            case 5: return R.string.formatted_date_M6;
            case 6: return R.string.formatted_date_M7;
            case 7: return R.string.formatted_date_M8;
            case 8: return R.string.formatted_date_M9;
            case 9: return R.string.formatted_date_M10;
            case 10: return R.string.formatted_date_M11;
            case 11: return R.string.formatted_date_M12;
        }
    }

    public static String months(Context context, int month, int dayOfMonth){
        return context.getString(getMonthStringRecourseId(month), dayOfMonth);
    }

    public static String getDurationStringSecs(Locale locale,long secs){
        if(secs>=3600){
            return String.format(locale,"%d:%d:%02d",secs/3600,(secs%3600)/60,secs%60);
        } else {
            return String.format(locale,"%d:%02d",(secs%3600)/60,secs%60);
        }
    }

    public static String getDurationWithNamesStringSecs(Context context, long secs){
        if(secs>=3600){
            return context.getString(R.string.formatted_time_1, secs/3600,(secs%3600)/60,secs%60);
        } else {
            if(secs>60) {
                return context.getString(R.string.formatted_time_2, (secs % 3600) / 60, secs % 60);
            } else {
                return context.getString(R.string.formatted_time_3, secs % 60);
            }
        }
    }

    public static String getDurationStringMills(Locale locale,long millis){
        return  getDurationStringSecs(locale,millis/1000);
    }

    public static String getDurationWithNamesStringMills(Context context, long millis){
        return getDurationWithNamesStringSecs(context, millis/1000);
    }
}
