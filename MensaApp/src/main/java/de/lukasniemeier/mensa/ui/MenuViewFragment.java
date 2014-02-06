package de.lukasniemeier.mensa.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.EnumSet;

import de.lukasniemeier.mensa.R;
import de.lukasniemeier.mensa.model.MealType;
import de.lukasniemeier.mensa.model.Menu;
import de.lukasniemeier.mensa.ui.adapter.CardState;
import de.lukasniemeier.mensa.ui.adapter.MealAdapter;
import de.lukasniemeier.mensa.ui.adapter.OnPageChangeListener;
import de.lukasniemeier.mensa.ui.preference.FilterPreferenceFragment;
import de.lukasniemeier.mensa.utils.Utils;

/**
 * Created on 17.09.13.
 */
public class MenuViewFragment extends Fragment implements OnPageChangeListener {

    public static MenuViewFragment create(Menu menu) {
        MenuViewFragment fragment = new MenuViewFragment();

        Bundle args = new Bundle();
        args.putSerializable(MenuViewFragment.ARG_MENU, menu);
        fragment.setArguments(args);

        return fragment;
    }

    public interface RefreshViewListener {
        public void attachRefreshableView(View target);
        public void removeRefreshableView(View target);
    }

    public static final String ARG_MENU = "ARG_MENU";

    private static final int filterMenuItemId = Utils.generateViewId();

    private RefreshViewListener listener;
    private ListView mealView;
    private MealAdapter mealAdapter;
    private MealContextHandler mealContextHandler;
    private MenuItem filterMenuItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_view, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof RefreshViewListener) {
            this.listener = (RefreshViewListener) activity;
        } else {
            throw new IllegalStateException("Must be attached to instance of " + RefreshViewListener.class.getName());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Menu menu = (Menu) getArguments().getSerializable(ARG_MENU);

        mealView = (ListView) getView().findViewById(R.id.fragment_menu_view);
        TextView mealViewFooter = new TextView(getActivity());
        mealView.addFooterView(mealViewFooter);

        mealAdapter = new MealAdapter(getActivity(), mealViewFooter);
        mealContextHandler = new MealContextHandler(getActivity(), mealAdapter);
        mealView.setAdapter(mealAdapter);
        mealAdapter.addAll(menu.getMeals(), false);

        final Animator first = AnimatorInflater.loadAnimator(getActivity(), R.animator.card_turn_over);
        final Animator second = AnimatorInflater.loadAnimator(getActivity(), R.animator.card_turn_over_2);
        second.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mealView.setEnabled(true);
            }
        });
        mealView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int i, long l) {
                // pass through MealContextHandler
                mealContextHandler.onItemClick(adapterView, view, i, l);
                mealView.setEnabled(false);
                final CardState<?> state = (CardState<?>) adapterView.getItemAtPosition(i);
                first.setTarget(view);
                first.removeAllListeners();
                first.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        state.turn();
                        mealAdapter.notifyDataSetChanged();
                        second.setTarget(view);
                        second.start();
                    }
                });
                first.start();
            }
        });
        mealView.setOnItemLongClickListener(mealContextHandler);
    }

    @Override
    public void onResume() {
        super.onResume();
        listener.attachRefreshableView(mealView);
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        /*filterMenuItem = menu.findItem(filterMenuItemId);
        if (filterMenuItem == null) {
            int none = android.view.Menu.NONE;
            filterMenuItem = menu.add(none, filterMenuItemId, none, R.string.app_name);
            filterMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    startActivityForResult(FilterPreferenceFragment.createIntent(getActivity()), 42);
                    return true;
                }
            });
        }*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 42) {
            Log.i("TAGTAG", "" + resultCode);
            mealAdapter.getFilter().filter(EnumSet.<MealType>of(MealType.PORK, MealType.BEEF));
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        listener.removeRefreshableView(mealView);
        mealContextHandler.finish();
        super.onPause();
    }

    @Override
    public void onPageChange(boolean isShown) {
        if (mealContextHandler != null) {
            mealContextHandler.onPageChange(isShown);
        }
    }
}
