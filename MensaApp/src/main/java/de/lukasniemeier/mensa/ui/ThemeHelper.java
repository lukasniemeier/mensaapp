package de.lukasniemeier.mensa.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;

import de.lukasniemeier.mensa.R;

/**
 * Created on 19.11.13.
 */
public class ThemeHelper {

    public static int getRefreshBarColor(Context context) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.refresh_bar_color});
        int colorId;
        if (attributes != null) {
            colorId = attributes.getColor(0, R.color.dark_green_studi);
            attributes.recycle();
        } else {
            colorId = context.getResources().getColor(R.color.dark_green_studi);
        }
        return colorId;
    }

    public static int currentTheme(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String themeValue = preferences.getString("settings_list_theme", String.valueOf(R.style.Theme_Studi_Theme));
        // ensure value is saved
        preferences.edit().putString("settings_list_theme", themeValue).commit();
        return Integer.valueOf(themeValue);
    }
}
