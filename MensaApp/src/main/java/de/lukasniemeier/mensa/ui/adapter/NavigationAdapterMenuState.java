package de.lukasniemeier.mensa.ui.adapter;

import android.app.ActionBar;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import de.lukasniemeier.mensa.R;
import de.lukasniemeier.mensa.model.WeeklyMenu;
import de.lukasniemeier.mensa.ui.MenuViewEmptyFragment;
import de.lukasniemeier.mensa.ui.MenuViewErrorFragment;
import de.lukasniemeier.mensa.ui.MenuViewFragment;
import de.lukasniemeier.mensa.utils.SerializableTime;
import de.lukasniemeier.mensa.utils.Utils;

/**
 * Created on 26.11.13.
 */


public class NavigationAdapterMenuState extends NavigationAdapterState implements
        ActionBar.OnNavigationListener {

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
        SerializableTime today = Utils.today();
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

        SortedSet<SerializableTime> dates = new TreeSet<SerializableTime>(weeklyMenu.getMenus().keySet());
        for (SerializableTime date : dates) {
            navigationMap.put(index++, MenuViewFragment.create(weeklyMenu.getMenu(date)));
            labels.add(Utils.formatDate(context, date));
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

            for (int i = 0; i < getCount(); i++) {
                Fragment hiddenFragment = getItem(i);
                if (hiddenFragment instanceof OnPageChangeListener) {
                    ((OnPageChangeListener)hiddenFragment).onPageChange(i == position);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
