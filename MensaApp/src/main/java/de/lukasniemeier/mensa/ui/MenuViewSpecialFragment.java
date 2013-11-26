package de.lukasniemeier.mensa.ui;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * Created on 18.09.13.
 */
public abstract class MenuViewSpecialFragment extends Fragment {

    public interface RefreshListener {
        void onRefreshRequested();
    }

    protected RefreshListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof RefreshListener) {
            this.listener = (RefreshListener) activity;
        } else {
            throw new IllegalStateException("Must be attached to instance of " + RefreshListener.class.getName());
        }
    }
}
