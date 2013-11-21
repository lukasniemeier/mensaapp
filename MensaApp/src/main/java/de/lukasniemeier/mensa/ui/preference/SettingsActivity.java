package de.lukasniemeier.mensa.ui.preference;

import android.content.res.Configuration;

import java.util.List;

import de.lukasniemeier.mensa.R;

public class SettingsActivity extends ThemedPreferenceActivity {

    @Override
    public boolean onIsMultiPane() {
        return (this.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE ||
                this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }
}
