package de.lukasniemeier.mensa.ui.adapter;

import android.app.ActionBar;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.widget.ArrayAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lukasniemeier.mensa.R;
import de.lukasniemeier.mensa.model.WeeklyMenu;
import de.lukasniemeier.mensa.ui.MenuViewEmptyFragment;
import de.lukasniemeier.mensa.ui.MenuViewErrorFragment;
import de.lukasniemeier.mensa.ui.MenuViewFragment;
import de.lukasniemeier.mensa.utils.Utils;

/**
 * Created on 26.11.13.
 */


public class NavigationAdapterMenuState extends NavigationAdapterState implements
        ActionBar.OnNavigationListener {

    private static final DateFormat menuDateFormat = new SimpleDateFormat("c, d.MM");

    private final WeeklyMenu weeklyMenu;
    private final Map<Integer, Fragment> navigationMap;

    public NavigationAdapterMenuState(NavigationAdapter stateContext, Context context,
                                      WeeklyMenu menu, int initialIndex) {
        super(stateContext, context);
        this.weeklyMenu = menu;
        this.navigationMap = new HashMap<Integer, Fragment>();

        initializeNavigation(initialIndex);
    }

    private void initializeNavigation(int selectedDateIndex) {
        Date today = Utils.today();
        int index = 0;

        List<String> labels = new ArrayList<String>();

        if (!weeklyMenu.hasMenu(today)) {
            navigationMap.put(index++, MenuViewEmptyFragment.create());
            labels.add(context.getString(R.string.today));
            // Since there is no 'today' we preselect tomorrow
            if (selectedDateIndex == 0 && !weeklyMenu.getMenus().isEmpty()) {
                selectedDateIndex++;
            }
        }

        for (Date date : weeklyMenu.getMenus().keySet()) {
            navigationMap.put(index++, MenuViewFragment.create(weeklyMenu.getMenu(date)));
            if (DateUtils.isToday(date.getTime())) {
                labels.add(context.getString(R.string.today));
            } else if(DateUtils.isToday(date.getTime() - 1000 * 60 * 60 * 24)) {
                labels.add(context.getString(R.string.tomorrow));
            } else {
                labels.add(menuDateFormat.format(date));
            }
        }

        ActionBar actionBar = stateContext.getActionBar();
        actionBar.setListNavigationCallbacks(
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        labels),
                this);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setSelectedNavigationItem(selectedDateIndex);
        stateContext.getPager().setOnPageChangeListener(pageSelectionListener);
    }

    @Override
    public void displayMenu(WeeklyMenu menu, int initialMenuIndex) {
        stateContext.getPager().setOnPageChangeListener(null);
        super.displayMenu(menu, initialMenuIndex);
    }

    @Override
    public void displayError(String errorMessage) {
        stateContext.getPager().setOnPageChangeListener(null);
        super.displayError(errorMessage);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = navigationMap.get(position);
        if (fragment == null) {
            fragment = MenuViewErrorFragment.create(context.getString(R.string.missing_menu));
        }
        return fragment;
    }

    @Override
    public int getItemPosition(Object object) {
        Fragment fragment = (Fragment) object;
        for (int i = 0; i < getCount(); i++) {
            if (fragment.equals(getItem(i))) {
                return i;
            }
        }
        // At this point we don't know this object... must be dead.
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return navigationMap.keySet().size();
    }

    @Override
    public boolean onNavigationItemSelected(int position, long l) {
        stateContext.getPager().setCurrentItem(position, false);
        return true;
    }

    private ViewPager.OnPageChangeListener pageSelectionListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
                stateContext.getActionBar().setSelectedNavigationItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
