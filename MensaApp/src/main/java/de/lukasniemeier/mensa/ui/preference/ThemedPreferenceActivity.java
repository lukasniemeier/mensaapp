package de.lukasniemeier.mensa.ui.preference;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import de.lukasniemeier.mensa.ui.ThemeHelper;

/**
 * Created on 19.11.13.
 */
public abstract class ThemedPreferenceActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.currentTheme(this));
        super.onCreate(savedInstanceState);
    }

}
