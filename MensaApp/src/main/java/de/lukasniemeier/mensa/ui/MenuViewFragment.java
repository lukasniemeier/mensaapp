package de.lukasniemeier.mensa.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import de.lukasniemeier.mensa.R;
import de.lukasniemeier.mensa.model.Menu;
import de.lukasniemeier.mensa.ui.adapter.CardState;
import de.lukasniemeier.mensa.ui.adapter.MealAdapter;

/**
 * Created on 17.09.13.
 */
public class MenuViewFragment extends Fragment {

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

    private RefreshViewListener listener;
    private GridView gridView;

    public MenuViewFragment() {
        // nothing yet.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_view, container, false);
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

        boolean isFirstStart = savedInstanceState == null;
        Menu menu = (Menu) getArguments().getSerializable(ARG_MENU);

        final MealAdapter adapter = new MealAdapter(getActivity());
        gridView = (GridView) getView().findViewById(R.id.fragment_menu_view);
        gridView.setAdapter(adapter);
        adapter.addAll(menu.getMeals(), false);

        final Animator first = AnimatorInflater.loadAnimator(getActivity(), R.animator.card_turn_over);
        final Animator second = AnimatorInflater.loadAnimator(getActivity(), R.animator.card_turn_over_2);
        second.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                gridView.setEnabled(true);
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int i, long l) {
                gridView.setEnabled(false);
                final CardState<?> state = (CardState<?>) adapterView.getItemAtPosition(i);
                first.setTarget(view);
                first.removeAllListeners();
                first.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        state.turn();
                        adapter.notifyDataSetChanged();
                        second.setTarget(view);
                        second.start();
                    }
                });
                first.start();
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        listener.attachRefreshableView(gridView);
    }

    @Override
    public void onPause() {
        listener.removeRefreshableView(gridView);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("EXTRA_MARKER", true);
    }
}
