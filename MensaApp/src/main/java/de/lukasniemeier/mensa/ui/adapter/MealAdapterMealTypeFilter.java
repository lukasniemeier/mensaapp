package de.lukasniemeier.mensa.ui.adapter;

import android.text.TextUtils;
import android.widget.Filter;
import android.widget.TextView;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import de.lukasniemeier.mensa.model.Meal;
import de.lukasniemeier.mensa.model.MealType;

/**
* Created on 27.11.13.
*/
public class MealAdapterMealTypeFilter extends Filter {

    private final MealAdapter adapter;
    private final TextView informationView;
    private EnumSet<MealType> filterQuery = EnumSet.allOf(MealType.class);

    public MealAdapterMealTypeFilter(MealAdapter adapter, TextView informationView) {
        this.adapter = adapter;
        this.informationView = informationView;
    }

    public void filter(EnumSet<MealType> mealTypes) {
        filterQuery = mealTypes;
        filter("");
    }

    public void filter() {
        filter(filterQuery);
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        List<CardState<Meal>> allItems = adapter.getAllItems();
        if (EnumSet.complementOf(filterQuery).isEmpty()) {
            results.values = allItems;
            results.count = allItems.size();
        } else {
            Collection<CardState<Meal>> filteredItems = Collections2.filter(allItems,
                    new Predicate<CardState<Meal>>() {
                        @Override
                        public boolean apply(CardState<Meal> mealCardState) {
                            return !Collections.disjoint(mealCardState.getValue().getTypes(),
                                    filterQuery);
                        }
                    });
            results.values = filteredItems;
            results.count = filteredItems.size();
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        List<CardState<Meal>> filteredItems = adapter.getFilteredItems();
        synchronized (adapter.getFilteredItems()) {
            filteredItems.clear();
            filteredItems.addAll((Collection<CardState<Meal>>) filterResults.values);
        }
        adapter.notifyDataSetChanged();
        int hiddenItems = adapter.getAllItems().size() - filteredItems.size();
        if (hiddenItems > 0) {
            informationView.setText("Und " + hiddenItems + " weitere ohne " +
                    TextUtils.join(" oder ", Collections2.transform(filterQuery, new Function<MealType, String>() {
                @Override
                public String apply(MealType mealType) {
                    return mealType.toString();
                }
            })));
        } else {
            informationView.setText("");
        }
    }
}
