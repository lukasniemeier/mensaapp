package de.lukasniemeier.mensa.ui.preference;

import android.os.Bundle;

import de.lukasniemeier.mensa.R;

/**
 * Created on 18.09.13.
 */
public class PricesPreferenceFragment extends BasePreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_prices);
    }
}
