package de.lukasniemeier.mensa.ui.preference;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import de.lukasniemeier.mensa.R;

/**
 * Created on 27.11.13.
 */
public class FilterPreferenceFragment extends BasePreferenceFragment {

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT, FilterPreferenceFragment.class.getName());
        intent.putExtra(SettingsActivity.EXTRA_NO_HEADERS, true );
        return intent;
    }

    private StandaloneMultiSelectListPreference preference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_filter);

        preference = (StandaloneMultiSelectListPreference) findPreference("filter_meal_types");
        preference.setEntries(new CharSequence[]{"1", "2"});
        preference.setEntryValues(new CharSequence[]{"a", "b"});
    }

    @Override
    public void onResume() {
        super.onResume();
        preference.show();
        preference.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
        });
    }
}
