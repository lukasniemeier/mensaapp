package de.lukasniemeier.mensa.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import de.lukasniemeier.mensa.MensaApplication;
import de.lukasniemeier.mensa.R;
import de.lukasniemeier.mensa.ResourceService;

/**
 * Created on 18.09.13.
 */
public enum MealType {
    UNKNOWN(R.string.meal_type_unknown, R.drawable.unknown, ""),
    FISH(R.string.meal_type_fish, R.drawable.fish, "mit Fisch"),
    PORK(R.string.meal_type_pork, R.drawable.pork, "mit Schweinefleisch"),
    CHICKEN(R.string.meal_type_chicken, R.drawable.chicken, "mit Geflügelfleisch"),
    BEEF(R.string.meal_type_beef, R.drawable.beef, "mit Rindfleisch"),
    VEGAN(R.string.meal_type_vegan, R.drawable.vegan, "vegan"),
    OVOLACTOVEGETABIL(R.string.meal_type_ovolactovegetabil, R.drawable.lacto, "ovo-lacto-vegetabil"),
    MENSAVITAL(R.string.meal_type_mensavital, R.drawable.vital, "mensaVital");

    private String name;
    private Bitmap icon;
    private String parseString;

    private MealType(int nameId, int iconId, String displayName) {
        ResourceService service = MensaApplication.getResourceService();
        this.name = service.localizeString(nameId);
        this.icon = BitmapFactory.decodeResource(service.getResources(), iconId);
        this.parseString = displayName;
    }

    public String getParseString() {
        return parseString;
    }

    public String getName() {
        return name;
    }

    public Bitmap getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return getName();
    }
}
