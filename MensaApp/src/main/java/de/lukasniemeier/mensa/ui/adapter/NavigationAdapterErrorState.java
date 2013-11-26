package de.lukasniemeier.mensa.ui.adapter;

import android.app.ActionBar;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;

import de.lukasniemeier.mensa.ui.MenuViewErrorFragment;

/**
 * Created on 26.11.13.
 */

public class NavigationAdapterErrorState extends NavigationAdapterState {

    private final Fragment errorFragment;

    public NavigationAdapterErrorState(NavigationAdapter stateContext, Context context,
                                       String errorMessage) {
        super(stateContext, context);
        this.errorFragment = MenuViewErrorFragment.create(errorMessage);

        stateContext.getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }

    @Override
    public Fragment getItem(int position) {
        return errorFragment;
    }

    @Override
    public int getItemPosition(Object object) {
        if (object.equals(errorFragment)) {
            return PagerAdapter.POSITION_UNCHANGED;
        } else {
            return PagerAdapter.POSITION_NONE;
        }
    }

    @Override
    public int getCount() {
        return 1;
    }
}
