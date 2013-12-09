package de.lukasniemeier.mensa.ui.preference;

import android.content.Context;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

/**
 * Created on 27.11.13.
 */
public class StandaloneMultiSelectListPreference extends MultiSelectListPreference {

    public StandaloneMultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StandaloneMultiSelectListPreference(Context context) {
        super(context);
    }

    public void show() {
        showDialog(null);
    }
}
