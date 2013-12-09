package de.lukasniemeier.mensa.ui.preference;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.lukasniemeier.mensa.R;
import de.lukasniemeier.mensa.model.Mensa;
import de.lukasniemeier.mensa.utils.Utils;

/**
 * Created on 18.09.13.
 */

public class GeneralPreferenceFragment extends BasePreferenceFragment {

    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT, GeneralPreferenceFragment.class.getName());
        intent.putExtra(SettingsActivity.EXTRA_NO_HEADERS, true );
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        ListPreference selectTheme = (ListPreference) findPreference("settings_list_theme");
        setupThemeList(selectTheme);

        CheckBoxPreference enableDefaultMensa = (CheckBoxPreference) findPreference("settings_check_mensa_default");
        ListPreference selectDefaultMensa = (ListPreference) findPreference("settings_list_mensa_default");
        setupMensaList(selectDefaultMensa);

        bindPreferenceSummaryToValue(selectTheme);
        bindPreferenceToCheckbox(enableDefaultMensa, selectDefaultMensa);
        bindPreferenceSummaryToValue(selectDefaultMensa);
    }

    private void setupThemeList(ListPreference selectTheme) {
        Collection<String> entries = Arrays.asList("Studi", "Girly");
        Collection<String> values = Arrays.asList(String.valueOf(R.style.Theme_Studi_Theme),
                String.valueOf(R.style.Theme_Girly_Theme));
        selectTheme.setDefaultValue(String.valueOf(R.style.Theme_Studi_Theme));
        selectTheme.setEntries(entries.toArray(new String[entries.size()]));
        selectTheme.setEntryValues(values.toArray(new String[values.size()]));

        selectTheme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Utils.restartApp(getActivity());
                return true;
            }
        });
    }

    private void setupMensaList(ListPreference selectDefault) {
        Collection<Mensa> mensas = Mensa.getMensas();
        selectDefault.setDefaultValue("");
        Collection<String> entries = new ArrayList<String>(Collections2.transform(mensas, new Function<Mensa, String>() {
            @Override
            public String apply(Mensa mensa) {
                return mensa.getName();
            }
        }));
        entries.add(getString(R.string.pref_none_select_mensa_default));
        Collection<String> values = new ArrayList<String>(Collections2.transform(mensas, new Function<Mensa, String>() {
            @Override
            public String apply(Mensa mensa) {
                return mensa.getShortName();
            }
        }));
        values.add("");
        selectDefault.setEntries(entries.toArray(new String[entries.size()]));
        selectDefault.setEntryValues(values.toArray(new String[values.size()]));
    }
}
