package de.lukasniemeier.mensa.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.lukasniemeier.mensa.R;
import de.lukasniemeier.mensa.model.Meal;
import de.lukasniemeier.mensa.model.MealType;

public class MealAdapter extends CardAdapter<Meal> {

    public MealAdapter(Context context) {
        super(context);
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
    protected void initializeView(final View view, CardState<Meal> state, boolean hasBeenTurned) {

        final Meal meal = state.getValue();

        final View ribbonLeftView = view.findViewById(R.id.card_ribbon_left);
        final View ribbonRightView = view.findViewById(R.id.card_ribbon_right);
        final TextView nameView = (TextView) view.findViewById(R.id.card_meal_name);
        final TextView descriptionView = (TextView) view.findViewById(R.id.card_meal_description);
        final ViewGroup typeGroup = (ViewGroup) view.findViewById(R.id.card_meal_types);

        nameView.setText(meal.getName());
        typeGroup.removeAllViews();

        int oldHeight = view.getHeight();

        if (state.isTurned()) {
            ribbonLeftView.setVisibility(View.VISIBLE);
            ribbonRightView.setVisibility(View.GONE);
            nameView.setGravity(Gravity.RIGHT);

            if (meal.getAdditives().isEmpty()) {
                descriptionView.setText(getContext().getString(R.string.meal_additives_no));
            } else {
                descriptionView.setText(TextUtils.join(", ", meal.getAdditives()));
            }
            for (MealType type : meal.getTypes()) {
                ImageView typeView = new ImageView(getContext());
                typeView.setImageBitmap(type.getIcon());
                typeGroup.addView(typeView);
            }

        } else {
            ribbonLeftView.setVisibility(View.GONE);
            ribbonRightView.setVisibility(View.VISIBLE);
            nameView.setGravity(Gravity.LEFT);

            if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("settings_check_prices", true)) {
                TextView ribbonText = (TextView) ribbonRightView.findViewById(R.id.card_ribbon_right_text);
                if (meal.getName().equals("Angebot 1")) {
                    ribbonText.setText(getContext().getString(R.string.price_1_40));
                } else if (meal.getName().equals("Angebot 2")) {
                    ribbonText.setText(getContext().getString(R.string.price_2_00));
                } else if (meal.getName().equals("Angebot 3")) {
                    ribbonText.setText(getContext().getString(R.string.price_2_50));
                } else if (meal.getName().equals("Alternativ-Angebot")) {
                    ribbonText.setText(getContext().getString(R.string.price_2_50));
                }
            }

            descriptionView.setText(meal.getDescription());
        }

        if (hasBeenTurned) {
            if (oldHeight != 0) {
                int measureSpecWidth = View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY);
                int measureSpecHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                view.measure(measureSpecWidth, measureSpecHeight);
                int newHeight = view.getMeasuredHeight();
                if (oldHeight != newHeight) {
                    Animator heightAnimator = createHeightAnimator(view, oldHeight, newHeight);
                    heightAnimator.start();
                }
            }
        }
    }

    public static ValueAnimator createHeightAnimator(final View view, int start, int end) {
        final int oldLayoutParamsHeight = view.getLayoutParams().height;
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = oldLayoutParamsHeight;
                view.setLayoutParams(layoutParams);
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            }

        });
        return animator;
    }
}