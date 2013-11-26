package de.lukasniemeier.mensa.ui.adapter;

import android.app.ActionBar;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import de.lukasniemeier.mensa.model.WeeklyMenu;

/**
 * Created on 26.11.13.
 */
public class NavigationAdapter extends FragmentPagerAdapter {

    private final ViewPager pager;
    private final Context context;

    private final ActionBar actionBar;
    private final FragmentManager fragmentManager;

    private NavigationAdapterState state;

    public NavigationAdapter(ViewPager viewPager, Context context, final ActionBar actionBar,
                             FragmentManager fragmentManager) {
        super(fragmentManager);
        this.pager = viewPager;
        this.context = context;
        this.actionBar = actionBar;
        this.fragmentManager = fragmentManager;


        this.state = new NavigationAdapterState(this, context) {
            @Override
            public Fragment getItem(int position) {
                return null;
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public int getCount() {
                return 0;
            }
        };
        pager.setAdapter(this);
    }

    public ActionBar getActionBar() {
        return actionBar;
    }

    public ViewPager getPager() {
        return pager;
    }

    public void displayMenu(WeeklyMenu menu, int initialMenuIndex) {
        state.displayMenu(menu, initialMenuIndex);
    }

    public void displayError(String errorMessage) {
        state.displayError(errorMessage);
    }

    public void setState(NavigationAdapterState newState) {
        this.state = newState;
        notifyDataSetChanged();
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        return state.getItem(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return state.getItemPosition(object);
    }

    @Override
    public long getItemId(int position) {
        // return a state-scoped unique hash
        return (((long)state.hashCode()) << 32) | (position & 0xffffffffL);
    }

    @Override
    public int getCount() {
        return state.getCount();
    }
}
