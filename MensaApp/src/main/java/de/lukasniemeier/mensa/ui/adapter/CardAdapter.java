package de.lukasniemeier.mensa.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.lukasniemeier.mensa.R;

public abstract class CardAdapter<T> extends ArrayAdapter<CardState<T>> {

    private LayoutInflater inflater;

    private Animator animation;
    private int runningAnimations;
    private Set<T> animationSet;

    public CardAdapter(Context context) {
        super(context, 0, new ArrayList<CardState<T>>());

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        runningAnimations = 0;
        animation = createAddAnimator(context);
        animationSet = new HashSet<T>();
    }

    @Override
    public void add(CardState<T> object) {
        add(object, true);
    }

    @Override
    public void addAll(Collection<? extends CardState<T>> collection) {
        addAll(collection, true);
    }

    public void add(T object, boolean showAnimation) {
        add(new CardState<T>(object), showAnimation);
    }

    public void add(CardState<T> object, boolean showAnimation) {
        if (showAnimation) {
            animationSet.add(object.getValue());
        }
        super.add(object);
    }

    public void addAll(List<? extends T> collection, boolean showAnimation) {
        if (showAnimation) {
            animationSet.addAll(collection);
        }
        super.addAll(Collections2.transform(collection, new Function<T, CardState<T>>() {
            @Override
            public CardState<T> apply(T value) {
                return new CardState<T>(value);
            }
        }));
    }

    public void addAll(Collection<? extends CardState<T>> collection, boolean showAnimation) {
        if (showAnimation) {
            animationSet.addAll(Collections2.transform(collection, new Function<CardState<T>, T>() {
                @Override
                public T apply(CardState<T> state) {
                    return state.getValue();
                }
            }));
        }
        super.addAll(collection);
    }

    protected abstract Animator createAddAnimator(Context context);

    protected abstract View inflateCardContentLayout(LayoutInflater inflater, ViewGroup container);

    protected abstract void initializeView(View view, CardState<T> state, boolean hasBeenTurned);

    private static class ViewHolder<T> {
        public CardState<T> state;
        public boolean wasTurned;

        private ViewHolder(CardState<T> state, boolean wasTurned) {
            this.state = state;
            this.wasTurned = wasTurned;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CardState<T> object = getItem(position);
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.card_layout, parent, false);
            view.setTag(new ViewHolder<T>(object, object.isTurned()));
            FrameLayout container = (FrameLayout) view.findViewById(R.id.card_container);
            container.addView(inflateCardContentLayout(inflater, container));
        }

        boolean hasBeenTurned = false;
        @SuppressWarnings("unchecked") ViewHolder<T> holder = (ViewHolder<T>) view.getTag();
        if (holder.state.equals(object)) {
            hasBeenTurned = holder.wasTurned != object.isTurned();
        }
        view.setTag(new ViewHolder<T>(object, object.isTurned()));
        initializeView(view, object, hasBeenTurned);

        animateAdd(object.getValue(), view);

        return view;
    }

    private void animateAdd(T object, View view) {
        if (animationSet.contains(object)) {
            view.setAlpha(0.0f);
            Animator currentAnimation = animation.clone();
            currentAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    runningAnimations++;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    runningAnimations--;
                }
            });
            currentAnimation.setStartDelay(250 * runningAnimations);
            currentAnimation.setTarget(view);
            currentAnimation.start();
            animationSet.remove(object);
        }
    }
}