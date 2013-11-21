package de.lukasniemeier.mensa.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created on 18.09.13.
 */
public class DefaultMensaManager {

    public static final String SETTINGS_LIST_MENSA_DEFAULT = "settings_list_mensa_default";
    public static final String SETTINGS_CHECK_MENSA_DEFAULT = "settings_check_mensa_default";

    private final SharedPreferences sharedPreferences;

    public DefaultMensaManager(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean shouldAskForDefault() {
        return sharedPreferences.getBoolean(SETTINGS_CHECK_MENSA_DEFAULT, true) &&
                sharedPreferences.getString(SETTINGS_LIST_MENSA_DEFAULT, "").isEmpty();
    }

    public void markAsDefault(String mensaShortName) {
        sharedPreferences.edit().putString(SETTINGS_LIST_MENSA_DEFAULT, mensaShortName).commit();
    }

    public boolean hasDefault() {
        return sharedPreferences.getBoolean(SETTINGS_CHECK_MENSA_DEFAULT, true) &&
                !sharedPreferences.getString(SETTINGS_LIST_MENSA_DEFAULT, "").isEmpty();
    }

    public String getDefaultMensaShortName() {
        return sharedPreferences.getString(SETTINGS_LIST_MENSA_DEFAULT, "");
    }
}
