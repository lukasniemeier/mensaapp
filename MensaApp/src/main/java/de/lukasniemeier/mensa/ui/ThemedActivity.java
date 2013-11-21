package de.lukasniemeier.mensa.ui;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created on 19.11.13.
 */
public abstract class ThemedActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.currentTheme(this));
        super.onCreate(savedInstanceState);
    }
}
