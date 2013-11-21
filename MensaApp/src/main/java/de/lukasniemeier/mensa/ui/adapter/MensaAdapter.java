package de.lukasniemeier.mensa.ui.adapter;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import de.lukasniemeier.mensa.R;
import de.lukasniemeier.mensa.model.Mensa;

public class MensaAdapter extends CardAdapter<Mensa> {

    public static class MensaImageMapper {

        private static MensaImageMapper instance;

        public static MensaImageMapper getInstance(Context context) {
            if (instance == null) {
                instance = new MensaImageMapper(context);
            }
            return instance;
        }

        private Map<String, Bitmap> bitmapMap;
        private Bitmap unknownBitmap;

        private MensaImageMapper(Context context) {
            bitmapMap = new HashMap<String, Bitmap>();
            bitmapMap.put("Griebnitzsee", BitmapFactory.decodeResource(context.getResources(), R.drawable.griebnitzsee));
            bitmapMap.put("Golm", BitmapFactory.decodeResource(context.getResources(), R.drawable.golm));
            bitmapMap.put("Am Neuen Palais", BitmapFactory.decodeResource(context.getResources(), R.drawable.palais));
            bitmapMap.put("Pappelallee", BitmapFactory.decodeResource(context.getResources(), R.drawable.pappel));
            bitmapMap.put("Friedrich-Ebert-Strasse", BitmapFactory.decodeResource(context.getResources(), R.drawable.friedrich));
            bitmapMap.put("Brandenburg", BitmapFactory.decodeResource(context.getResources(), R.drawable.brandenburg));
            bitmapMap.put("Wildau", BitmapFactory.decodeResource(context.getResources(), R.drawable.wildau));

            unknownBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.griebnitzsee);
        }

        public Bitmap getImage(Mensa mensa) {
            String key = mensa.getName();
            if (bitmapMap.containsKey(key)) {
                return bitmapMap.get(key);
            } else {
                return unknownBitmap;
            }
        }
    }

    public MensaAdapter(Context context) {
        super(context);
    }

    @Override
    protected Animator createAddAnimator(Context context) {
        return AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_in);
    }

    @Override
    protected View inflateCardContentLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.card_content_mensa, container, false);
    }

    @Override
    protected void initializeView(View view, Mensa mensa, boolean isTurned, boolean hasBeenTurned) {
        TextView nameView = (TextView) view.findViewById(R.id.card_mensa_name);
        nameView.setText(mensa.getName());

        TextView openingTimesView = (TextView) view.findViewById(R.id.card_mensa_opening_times);
        openingTimesView.setText(mensa.getOpeningTimes());

        TextView addressView = (TextView) view.findViewById(R.id.card_mensa_address);
        addressView.setText(mensa.getAddress());

        ImageView imageView = (ImageView) view.findViewById(R.id.card_mensa_image);
        imageView.setImageBitmap(MensaImageMapper.getInstance(getContext()).getImage(mensa));
    }
}