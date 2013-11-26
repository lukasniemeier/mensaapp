package de.lukasniemeier.mensa.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;

import java.util.Calendar;
import java.util.Date;

/**
 * Created on 18.09.13.
 */
public class Utils {

    public static Date today() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return today.getTime();
    }

    public static Time now() {
        Time now = new Time();
        now.setToNow();
        return now;
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
