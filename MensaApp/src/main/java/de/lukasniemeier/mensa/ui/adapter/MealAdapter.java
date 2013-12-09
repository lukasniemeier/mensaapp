package de.lukasniemeier.mensa.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.lukasniemeier.mensa.R;
import de.lukasniemeier.mensa.model.Meal;
import de.lukasniemeier.mensa.model.MealType;
import de.lukasniemeier.mensa.utils.Utils;

public class MealAdapter extends CardAdapter<Meal> {

    private final List<CardState<Meal>> filteredItems;
    private final MealAdapterMealTypeFilter filter;

    public MealAdapter(Context context, TextView informationView) {
        super(context);
        this.filteredItems = new ArrayList<CardState<Meal>>();
        this.filter = new MealAdapterMealTypeFilter(this, informationView);
    }

    public List<CardState<Meal>> getFilteredItems() {
        return filteredItems;
    }

    @Override
    public void add(CardState<Meal> object) {
        filteredItems.add(object);
        super.add(object);
        filter.filter();
    }

    @Override
    public void addAll(Collection<? extends CardState<Meal>> collection) {
        filteredItems.addAll(collection);
        super.addAll(collection);
        filter.filter();
    }

    @Override
    public void clear() {
        filteredItems.clear();
        super.clear();
        filter.filter();
    }

    @Override
    public CardState<Meal> getItem(int position) {
        synchronized (filteredItems) {
            return filteredItems.get(position);
        }
    }

    @Override
    public int getCount() {
        synchronized (filteredItems) {
            return filteredItems.size();
        }
    }

    public List<CardState<Meal>> getAllItems() {
        List<CardState<Meal>> allItems = new ArrayList<CardState<Meal>>();
        for (int i = 0; i < super.getCount(); i++) {
            allItems.add(super.getItem(i));
        }
        return allItems;
    }

    @Override
    public MealAdapterMealTypeFilter getFilter() {
        return filter;
    }

    @Override
    protected Animator createAddAnimator(Context context) {
        return AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_in);
    }

    @Override
    protected View inflateCardContentLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.card_content_meal, container, false);
    }

    @Override
    protected void initializeView(View view, Meal meal, boolean isTurned, boolean hasBeenTurned) {
        final View ribbonLeftView = view.findViewById(R.id.card_ribbon_left);
        final View ribbonRightView = view.findViewById(R.id.card_ribbon_right);
        final TextView nameView = (TextView) view.findViewById(R.id.card_meal_name);
        final TextView descriptionView = (TextView) view.findViewById(R.id.card_meal_description);
        final ViewGroup typeGroup = (ViewGroup) view.findViewById(R.id.card_meal_types);

        if (isTurned) {
            setupViewBack(meal, ribbonLeftView, ribbonRightView, nameView, descriptionView, typeGroup);
        } else {
            setupViewFront(meal, ribbonLeftView, ribbonRightView, nameView, descriptionView, typeGroup);
        }
    }

    private void setupViewFront(Meal meal, View ribbonLeftView, View ribbonRightView, TextView nameView, TextView descriptionView, ViewGroup typeGroup) {
        nameView.setText(meal.getName());
        typeGroup.removeAllViews();

        ribbonLeftView.setVisibility(View.GONE);
        ribbonRightView.setVisibility(View.VISIBLE);
        nameView.setGravity(Gravity.LEFT);

        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("settings_check_prices", true)) {
            TextView ribbonText = (TextView) ribbonRightView.findViewById(R.id.card_ribbon_right_text);
            ribbonText.setText(meal.getPrice());
        }

        descriptionView.setText(meal.getDescription());
    }

    private void setupViewBack(Meal meal, View ribbonLeftView, View ribbonRightView, TextView nameView, TextView descriptionView, ViewGroup typeGroup) {
        nameView.setText(meal.getName());
        typeGroup.removeAllViews();

        ribbonLeftView.setVisibility(View.VISIBLE);
        ribbonRightView.setVisibility(View.GONE);
        nameView.setGravity(Gravity.RIGHT);

        StringBuilder descriptionText = new StringBuilder();
        if (meal.getAdditives().isEmpty()) {
            descriptionText.append(getContext().getString(R.string.meal_additives_no));
        } else {
            descriptionText.append(TextUtils.join(", ", meal.getAdditives()));
        }

        int margin = Utils.dpToPx(getContext(), 5);
        LinearLayout.LayoutParams typeViewLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        typeViewLayout.setMargins(0, margin,  0, margin);

        for (MealType type : meal.getTypes()) {
            ImageView typeView = new ImageView(getContext());
            typeView.setLayoutParams(typeViewLayout);
            typeView.setImageBitmap(type.getIcon());
            typeGroup.addView(typeView);
        }

        descriptionView.setText(descriptionText.toString());
    }


}