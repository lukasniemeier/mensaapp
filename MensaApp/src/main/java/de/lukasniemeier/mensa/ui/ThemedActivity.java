package de.lukasniemeier.mensa.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created on 19.11.13.
 */
public abstract class ThemedActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeHelper.currentTheme(this));
        super.onCreate(savedInstanceState);
    }
}
