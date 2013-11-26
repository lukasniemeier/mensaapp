package de.lukasniemeier.mensa.ui;


import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

import de.lukasniemeier.mensa.R;
import de.lukasniemeier.mensa.WeeklyMenuTask;
import de.lukasniemeier.mensa.model.Mensa;
import de.lukasniemeier.mensa.model.WeeklyMenu;
import de.lukasniemeier.mensa.parser.WeeklyMenuParseException;
import de.lukasniemeier.mensa.ui.adapter.NavigationAdapter;
import de.lukasniemeier.mensa.utils.DefaultMensaManager;
import uk.co.senab.actionbarpulltorefresh.library.DefaultHeaderTransformer;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public class MenuActivity extends BaseActivity implements
        MenuViewFragment.RefreshViewListener,
        MenuViewSpecialFragment.RefreshListener {

    private static final String TAG = MenuActivity.class.getSimpleName();

    public static Intent createIntent(Context context, Mensa mensa) {
        Intent intent = new Intent(context, MenuActivity.class);
        intent.putExtra(MenuActivity.EXTRA_MENSA_SHORTNAME, mensa.getShortName());
        return intent;
    }

    private static final String EXTRA_MENSA_SHORTNAME = "EXTRA_MENSA_SHORTNAME";
    private static final String STATE_WEEKLY_MENU = "weekly_menu";
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private Mensa mensa;
    private WeeklyMenu weeklyMenu;

    private ViewPager viewPager;
    private NavigationAdapter viewPagerAdapter;

    private MenuItem refreshMenuItem;
    private PullToRefreshAttacher refresher;
    private View bottomBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        String shortName = getIntent().getStringExtra(MenuActivity.EXTRA_MENSA_SHORTNAME);
        mensa = Mensa.getMensa(shortName);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setTitle(mensa.getShortName());

        viewPager = (ViewPager) findViewById(R.id.container);
        viewPagerAdapter = new NavigationAdapter(viewPager, this, actionBar, getSupportFragmentManager());

        refresher = PullToRefreshAttacher.get(this);
        ((DefaultHeaderTransformer)refresher.getHeaderTransformer()).setProgressBarColor(
                ThemeHelper.getRefreshBarColor(this));

        int selectedDateIndex = 0;
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
                selectedDateIndex = savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM);
            }
            if (savedInstanceState.containsKey(STATE_WEEKLY_MENU)) {
                weeklyMenu = (WeeklyMenu) savedInstanceState.getSerializable(STATE_WEEKLY_MENU);
                Log.i(TAG, "Restored WeeklyMenu from state.");
            }
        }

        if (weeklyMenu != null && !weeklyMenu.isOutdated()) {
            viewPagerAdapter.displayMenu(weeklyMenu, selectedDateIndex);
        }

        setupBottomBar();
    }

    @Override
    protected void onResume() {
        super.onStart();
        if (weeklyMenu == null || weeklyMenu.isOutdated()) {
            refresh();
        }
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
        final String mensaShortName = mensa.getShortName();
        Button defaultButton = (Button) bottomBar.findViewById(R.id.menu_bottom_bar_default);
        defaultButton.setText(String.format(getString(R.string.bottom_bar_make_default), mensaShortName));
        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DefaultMensaManager(getApplicationContext()).markAsDefault(mensaShortName);
                bottomBar.setVisibility(View.GONE);
                Toast.makeText(
                        getApplicationContext(),
                        String.format(getString(R.string.mensa_marked_as_default), mensaShortName),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refresh() {
        Log.i(TAG, "Refresh requested...");
        refresher.setRefreshing(true);
        if (refreshMenuItem != null) {
            refreshMenuItem.setEnabled(false);
        }

        new WeeklyMenuTask(mensa, getApplicationContext(), new WeeklyMenuTask.WeeklyMenuReceiver() {
            @Override
            public void onWeeklyMenuSuccess(WeeklyMenu newWeeklyMenu) {
                refresher.setRefreshComplete();
                if (refreshMenuItem != null) {
                    refreshMenuItem.setEnabled(true);
                }
                Log.i(TAG, "Refresh succeed)");

                weeklyMenu = newWeeklyMenu;
                viewPagerAdapter.displayMenu(weeklyMenu, 0);
                checkForDefaultMensa();
            }

            @Override
            public void onWeeklyMenuError(Exception error) {
                refresher.setRefreshComplete();
                if (refreshMenuItem != null) {
                    refreshMenuItem.setEnabled(true);
                }
                Log.i(TAG, "Refresh failed");

                String errorMessage = getErrorMessage(error);
                if (weeklyMenu == null) {
                    viewPagerAdapter.displayError(errorMessage);
                } else {
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            private String getErrorMessage(Exception error) {
                String errorMessage;
                if (error instanceof WeeklyMenuParseException) {
                    errorMessage = getString(R.string.menu_error_description_parse, error.getMessage());
                } else if (error instanceof IOException) {
                    errorMessage = getString(R.string.menu_error_description_network, error.getMessage());
                } else {
                    errorMessage = getString(R.string.menu_error_description_exception, error.getMessage());
                }
                return errorMessage;
            }
        }).execute(
                "http://www.studentenwerk-potsdam.de/speiseplan.html",
                mensa.getDetailMenuURL().toString());
    }

    private void checkForDefaultMensa() {
        final DefaultMensaManager manager = new DefaultMensaManager(getApplicationContext());
        if (manager.shouldAskForDefault()) {
            bottomBar.setVisibility(View.VISIBLE);
            bottomBar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bottom_bar_slide_up));
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        refreshMenuItem = menu.findItem(R.id.action_refresh);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isEnoughSpaceOnActionBar()) {
            refreshMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else {
            refreshMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private boolean isEnoughSpaceOnActionBar() {
        return ViewConfiguration.get(this).hasPermanentMenuKey() ||
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
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
            default:
                return super.onOptionsItemSelected(item);
        }
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
    public void removeRefreshableView(View target) {
        refresher.removeRefreshableView(target);
    }

    @Override
    public void onRefreshRequested() {
        refresh();
    }
}
