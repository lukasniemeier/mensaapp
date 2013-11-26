package de.lukasniemeier.mensa.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.text.format.Time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.lukasniemeier.mensa.R;

/**
 * Created on 18.09.13.
 */
public class Utils {

    private static final DateFormat defaultMenuDateFormat = new SimpleDateFormat("c, d.MM");

    public static String formatDate(Context context, SerializableTime time) {
        return formatDate(context, time, defaultMenuDateFormat);
    }

    public static String formatDate(Context context, SerializableTime time, DateFormat format) {
        if (DateUtils.isToday(time.toMillis())) {
            return context.getString(R.string.today);
        } else if(DateUtils.isToday(time.toMillis() - 1000 * 60 * 60 * 24)) {
            return context.getString(R.string.tomorrow);
        } else {
            return format.format(time.toDate());
        }
    }

    public static SerializableTime today() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return new SerializableTime(today.getTime());
    }

    public static SerializableTime now() {
        Time now = new Time();
        now.setToNow();
        return new SerializableTime(now);
    }

    public static void restartApp(Activity context) {
        Intent i = context.getBaseContext().getPackageManager().getLaunchIntentForPackage(context.getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }

    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int)(dp * density);
    }
}
