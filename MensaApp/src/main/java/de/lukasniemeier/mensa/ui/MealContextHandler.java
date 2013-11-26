package de.lukasniemeier.mensa.ui;

import android.app.Activity;
import android.content.Intent;
import android.view.ActionMode;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ShareActionProvider;

import java.text.SimpleDateFormat;

import de.lukasniemeier.mensa.R;
import de.lukasniemeier.mensa.model.Meal;
import de.lukasniemeier.mensa.ui.adapter.CardAdapter;
import de.lukasniemeier.mensa.ui.adapter.OnPageChangeListener;
import de.lukasniemeier.mensa.utils.SerializableTime;
import de.lukasniemeier.mensa.utils.Utils;

/**
 * Created on 26.11.13.
 */
public class MealContextHandler implements ActionMode.Callback, AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener, OnPageChangeListener {

    private final Activity activity;
    private final CardAdapter<Meal> adapter;

    private ActionMode actionMode;
    private View selectedView;

    private ShareActionProvider shareActionProvider;

    public MealContextHandler(Activity activity, CardAdapter<Meal> adapter) {
        this.activity = activity;
        this.adapter = adapter;
        this.selectedView = null;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, android.view.Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.meal_context_menu, menu);
        MenuItem shareItem = menu.findItem(R.id.action_meal_share);
        shareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, android.view.Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_meal_share:
                finish();
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        finish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (actionMode != null) {
            return false;
        }

        actionMode = activity.startActionMode(this);
        selectedView = view;
        selectedView.setSelected(true);
        setShareIntent(adapter.getItem(position).getValue());
        return true;
    }

    @Override
    public void onPageChange(boolean isShown) {
        if (!isShown) {
            finish();
        }
    }

    public void finish() {
        if (selectedView != null) {
            selectedView.setSelected(false);
        }
        if (actionMode != null) {
            actionMode.finish();
        }
        selectedView = null;
    }

    private void setShareIntent(Meal meal) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getShareText(meal));
        shareIntent.setType("text/plain");

        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(shareIntent);
        }
    }

    private String getShareText(Meal meal) {
        SerializableTime time = Utils.now();
        SerializableTime mealDay = meal.getMenu().getMenuDay();
        String mealDayText;
        if (mealDay.getYearDay() - time.getYearDay() >= 7) {
            mealDayText = Utils.formatDate(activity, mealDay, new SimpleDateFormat("cccc (d.MM)"));
        } else {
            mealDayText = Utils.formatDate(activity, mealDay, new SimpleDateFormat("cccc"));
        }
        return activity.getString(R.string.share_meal,
                meal.getMenu().getWeeklyMenu().getMensa().getShortName(),
                mealDayText,
                meal.getName(),
                meal.getDescription());
    }
}
