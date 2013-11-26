package de.lukasniemeier.mensa.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;

import de.lukasniemeier.mensa.model.WeeklyMenu;

/**
 * Created on 26.11.13.
 */
public abstract class NavigationAdapterState {

    protected final NavigationAdapter stateContext;
    protected final Context context;

    public NavigationAdapterState(NavigationAdapter stateContext, Context context) {
        this.stateContext = stateContext;
        this.context = context;
    }

    public void displayMenu(WeeklyMenu menu, int initialMenuIndex) {
        stateContext.setState(
                new NavigationAdapterMenuState(stateContext, context, menu, initialMenuIndex));
    }

    public void displayError(String errorMessage) {
        stateContext.setState(new NavigationAdapterErrorState(stateContext, context, errorMessage));
    }

    public abstract Fragment getItem(int position);
    public abstract int getItemPosition(Object object);
    public abstract int getCount();
}
