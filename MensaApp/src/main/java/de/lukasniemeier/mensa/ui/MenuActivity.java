package de.lukasniemeier.mensa.ui;


import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lukasniemeier.mensa.R;
import de.lukasniemeier.mensa.WeeklyMenuTask;
import de.lukasniemeier.mensa.model.WeeklyMenu;
import de.lukasniemeier.mensa.parser.WeeklyMenuParseException;
import de.lukasniemeier.mensa.ui.preference.SettingsActivity;
import de.lukasniemeier.mensa.utils.DefaultMensaManager;
import de.lukasniemeier.mensa.utils.Utils;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public class MenuActivity extends ThemedActivity implements
        ActionBar.OnNavigationListener,
        MenuViewFragment.RefreshViewListener,
        MenuViewSpecialFragment.RefreshListener {

    private static final String TAG = MenuActivity.class.getSimpleName();

    public static final String ABOUT_DIALOG_TAG = "AboutDialog";

    public static final String EXTRA_MENSA_NAME = "EXTRA_MENSA_NAME";
    public static final String EXTRA_MENSA_SHORTNAME = "EXTRA_MENSA_SHORTNAME";
    public static final String EXTRA_MENSA_URL = "EXTRA_MENSA_URL";

    private static final String STATE_WEEKLY_MENU = "weekly_menu";
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private static final DateFormat menuDateFormat = new SimpleDateFormat("c, d.MM");

    private URL mensaURL;

    private WeeklyMenu weeklyMenu;
    private Map<Integer, Date> navigationMap;

    private PullToRefreshAttacher refresher;
    private View bottomBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        refresher = PullToRefreshAttacher.get(this);
        ((DefaultHeaderTransformer)refresher.getHeaderTransformer()).setProgressBarColor(
                ThemeHelper.getRefreshBarColor(this));

        mensaURL = (URL) getIntent().getSerializableExtra(EXTRA_MENSA_URL);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(getIntent().getStringExtra(EXTRA_MENSA_SHORTNAME));
        actionBar.setDisplayHomeAsUpEnabled(true);

        int selectedDateIndex = 0;
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            selectedDateIndex = savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_WEEKLY_MENU)) {
            weeklyMenu = (WeeklyMenu) savedInstanceState.getSerializable(STATE_WEEKLY_MENU);
        }

        if (weeklyMenu == null || weeklyMenu.isOutdated()) {
            refresh();
        } else {
            initializeNavigation(selectedDateIndex);
        }

        setupBottomBar();
    }

    private void setupBottomBar() {
        bottomBar = findViewById(R.id.menu_bottom_bar);
        Button cancelButton = (Button) bottomBar.findViewById(R.id.menu_bottom_bar_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomBar.setVisibility(View.GONE);
            }
        });
        Button defaultButton = (Button) bottomBar.findViewById(R.id.menu_bottom_bar_default);
        defaultButton.setText(
                String.format(getString(R.string.bottom_bar_make_default), getIntent().getStringExtra(EXTRA_MENSA_SHORTNAME)));
        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DefaultMensaManager(getApplicationContext()).markAsDefault(MenuActivity.this);
                bottomBar.setVisibility(View.GONE);
                Toast.makeText(
                        getApplicationContext(),
                        String.format("'%1$s marked as default.", getIntent().getStringExtra(EXTRA_MENSA_SHORTNAME)),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refresh() {
        if (!refresher.isRefreshing()) {
            refresher.setRefreshing(true);
        } else {
            Log.w(TAG, "Refreshing already ongoing...");
            return;
        }

        new WeeklyMenuTask(getApplicationContext(), new WeeklyMenuTask.WeeklyMenuReceiver() {
            @Override
            public void onWeeklyMenuSuccess(WeeklyMenu newWeeklyMenu) {
                weeklyMenu = newWeeklyMenu;
                refresher.setRefreshComplete();

                FragmentManager manager = getFragmentManager();
                Fragment fragment = manager.findFragmentById(R.id.container);
                if (fragment != null) {
                    manager.beginTransaction().remove(fragment).commitAllowingStateLoss();
                }

                initializeNavigation(0);
                checkForDefaultMensa();
            }

            @Override
            public void onWeeklyMenuError(Exception error) {
                refresher.setRefreshComplete();

                String errorMessage;
                if (error instanceof WeeklyMenuParseException) {
                    errorMessage = getString(R.string.menu_error_description_parse, error.getMessage());
                } else if (error instanceof IOException) {
                    errorMessage = getString(R.string.menu_error_description_network, error.getMessage());
                } else {
                    errorMessage = getString(R.string.menu_error_description_exception, error.getMessage());
                }

                if (weeklyMenu == null) {
                    getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, MenuViewErrorFragment.create(errorMessage))
                            .commit();
                } else {
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        }).execute("http://www.studentenwerk-potsdam.de/speiseplan.html", mensaURL.toString());
    }

    private void checkForDefaultMensa() {
        final DefaultMensaManager manager = new DefaultMensaManager(getApplicationContext());
        if (manager.shouldAskForDefault()) {
            bottomBar.setVisibility(View.VISIBLE);
            bottomBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bottom_bar_slide_up));
        }
    }

    private void initializeNavigation(int selectedDateIndex) {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        navigationMap = new HashMap<Integer, Date>();
        Date today = Utils.today();
        int index = 0;
        List<String> labels = new ArrayList<String>();


        if (!weeklyMenu.hasMenu(today)) {
            navigationMap.put(index++, today);
            labels.add(getString(R.string.today));
            // Since there is no 'today' we preselect tomorrow
            if (selectedDateIndex == 0) {
                selectedDateIndex++;
            }
        }

        for (Date date : weeklyMenu.getMenus().keySet()) {
            navigationMap.put(index++, date);
            if (DateUtils.isToday(date.getTime())) {
                labels.add(getString(R.string.today));
            } else if(DateUtils.isToday(date.getTime() - 1000 * 60 * 60 * 24)) {
                labels.add(getString(R.string.tomorrow));
            } else {
                labels.add(menuDateFormat.format(date));
            }
        }

        actionBar.setListNavigationCallbacks(
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        labels),
                this);

        getActionBar().setSelectedNavigationItem(selectedDateIndex);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (weeklyMenu != null) {
            outState.putSerializable(STATE_WEEKLY_MENU, weeklyMenu);
        }

        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void navigateToMensaActivity() {
        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.putExtra(MensaActivity.EXTRA_NO_DEFAULT_REDIRECT, true);
        NavUtils.navigateUpTo(this, intent);
    }

    @Override
    public void onBackPressed() {
        navigateToMensaActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                navigateToMensaActivity();
                return true;
            case R.id.action_refresh:
                refresh();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_about:
                DialogFragment aboutDialog = new AboutDialog();
                aboutDialog.show(getFragmentManager(), ABOUT_DIALOG_TAG);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        FragmentManager manager = getFragmentManager();
        Date date = navigationMap.get(position);
        String dateTag = date.toString();
        if (weeklyMenu.hasMenu(date)) {

            if (manager.findFragmentByTag(dateTag) == null) {
                Fragment fragment = MenuViewFragment.create(weeklyMenu.getMenu(date));

                FragmentTransaction transaction = manager.beginTransaction();
                /* Use back button for ActionBar navigation
                if (manager.findFragmentById(R.id.container) != null) {
                    transaction.addToBackStack(null);
                }*/
                transaction.replace(R.id.container, fragment, dateTag).commit();
            }
        } else if (DateUtils.isToday(date.getTime())) {
            manager.beginTransaction()
                    .replace(R.id.container, new MenuViewEmptyFragment(), dateTag)
                    .commit();
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    String.format(getString(R.string.missing_menu), menuDateFormat.format(date)),
                    Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    public void attachRefreshableView(View target) {
        refresher.addRefreshableView(target, new PullToRefreshAttacher.OnRefreshListener() {
            @Override
            public void onRefreshStarted(View view) {
                refresh();
            }
        });
    }

    @Override
    public void onRefreshRequested() {
        refresh();
    }
}
