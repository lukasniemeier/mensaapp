package de.lukasniemeier.mensa.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.lukasniemeier.mensa.R;

public abstract class CardAdapter<T> extends ArrayAdapter<CardState<T>> {

    private final LayoutInflater inflater;

    private final Animator animation;
    private int runningAnimations;
    private final Set<T> animationSet;
    private final Map<CardState<T>, ViewHolder<T>> holderMap;
    private ViewTreeObserver.OnPreDrawListener cardHeightEnsurer;

    public CardAdapter(Context context) {
        super(context, 0, new ArrayList<CardState<T>>());

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        runningAnimations = 0;
        animation = createAddAnimator(context);
        animationSet = new HashSet<T>();
        holderMap = new HashMap<CardState<T>, ViewHolder<T>>();
        cardHeightEnsurer = null;
    }

    public void add(T object, boolean showAnimation) {
        add(new CardState<T>(object), showAnimation);
    }

    public void add(CardState<T> object, boolean showAnimation) {
        if (showAnimation) {
            animationSet.add(object.getValue());
        }
        add(object);
    }

    public void addAll(List<? extends T> collection, boolean showAnimation) {
        if (showAnimation) {
            animationSet.addAll(collection);
        }
        addAll(Collections2.transform(collection, new Function<T, CardState<T>>() {
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
        addAll(collection);
    }

    protected abstract Animator createAddAnimator(Context context);

    protected abstract View inflateCardContentLayout(LayoutInflater inflater, ViewGroup container);

    protected abstract void initializeView(View view, T value, boolean isTurned, boolean hasBeenTurned);

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if (cardHeightEnsurer == null) {
            cardHeightEnsurer = new CardHeightChangeNotifier(parent);
            parent.getViewTreeObserver().addOnPreDrawListener(cardHeightEnsurer);
        }

        CardState<T> object = getItem(position);
        View view = convertView;

        ViewHolder<T> objectHolder = holderMap.get(object);
        if (objectHolder == null) {
            holderMap.put(object, new ViewHolder<T>(object, object.isTurned(), parent.getMeasuredWidth()));
            objectHolder = holderMap.get(object);
        }

        if (view == null) {
            view = inflater.inflate(R.layout.card_layout, parent, false);
            view.setTag(objectHolder);
            FrameLayout container = (FrameLayout) view.findViewById(R.id.card_container);
            container.addView(inflateCardContentLayout(inflater, container));
        }

        boolean hasBeenTurned = false;
        @SuppressWarnings("unchecked") ViewHolder<T> currentHolder = (ViewHolder<T>) view.getTag();
        if (objectHolder.equals(currentHolder)) {
            hasBeenTurned = objectHolder.wasTurned != object.isTurned();
        }
        objectHolder.wasTurned = object.isTurned();
        view.setTag(objectHolder);

        T value = objectHolder.state.getValue();
        if (objectHolder.maxHeight == null) {
            initializeView(view, value, false, false);
            int frontHeight = measureHeight(view, objectHolder.parentWidth);
            initializeView(view, value, true, false);
            int backHeight = measureHeight(view, objectHolder.parentWidth);
            objectHolder.maxHeight = Math.max(frontHeight, backHeight);
        }
        initializeView(view, value, objectHolder.wasTurned, hasBeenTurned);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = objectHolder.maxHeight;
        view.setLayoutParams(layoutParams);

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


    private static class ViewHolder<T> {
        public final CardState<T> state;
        public boolean wasTurned;
        public int parentWidth;
        public Integer maxHeight;

        private ViewHolder(CardState<T> state, boolean wasTurned, int parentWidth) {
            this.state = state;
            this.wasTurned = wasTurned;
            this.parentWidth = parentWidth;
            this.maxHeight = null;
        }
    }

    private static int measureHeight(View view, int width) {
        int measureSpecWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measureSpecHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(measureSpecWidth, measureSpecHeight);
        return view.getMeasuredHeight();
    }

    private class CardHeightChangeNotifier implements ViewTreeObserver.OnPreDrawListener {
        private final ViewGroup cardContainer;

        private CardHeightChangeNotifier(ViewGroup cardContainer) {
            this.cardContainer = cardContainer;
        }

        @Override
        public boolean onPreDraw() {
            if (holderMap.isEmpty()) {
                return true;
            }
            boolean parentHeightUpdated = false;
            int measuredParentWidth = cardContainer.getMeasuredWidth();

            for (ViewHolder<T> holder : holderMap.values()) {
                if (holder.parentWidth != measuredParentWidth) {
                    holder.parentWidth = measuredParentWidth;
                    holder.maxHeight = null;
                    parentHeightUpdated = true;
                }
            }
            if (parentHeightUpdated) {
                CardAdapter.this.notifyDataSetChanged();
                return false;
            }
            return true;
        }
    }
}