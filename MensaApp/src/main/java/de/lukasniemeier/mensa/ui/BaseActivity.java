package de.lukasniemeier.mensa.ui;

import android.app.DialogFragment;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import de.lukasniemeier.mensa.R;
import de.lukasniemeier.mensa.ui.preference.GeneralPreferenceFragment;
import de.lukasniemeier.mensa.ui.preference.SettingsActivity;

/**
 * Created on 21.11.13.
 */
public abstract class BaseActivity extends ThemedActivity {

    public static final String ABOUT_DIALOG_TAG = "AboutDialog";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mensa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT, GeneralPreferenceFragment.class.getName());
                intent.putExtra(SettingsActivity.EXTRA_NO_HEADERS, true );
                startActivity(intent);
                return true;
            case R.id.action_about:
                DialogFragment aboutDialog = new AboutDialog();
                aboutDialog.show(getFragmentManager(), ABOUT_DIALOG_TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
