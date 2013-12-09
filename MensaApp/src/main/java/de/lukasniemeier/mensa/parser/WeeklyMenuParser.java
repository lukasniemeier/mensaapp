package de.lukasniemeier.mensa.parser;

import android.content.Context;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.lukasniemeier.mensa.R;
import de.lukasniemeier.mensa.model.Meal;
import de.lukasniemeier.mensa.model.MealType;
import de.lukasniemeier.mensa.model.Mensa;
import de.lukasniemeier.mensa.model.Menu;
import de.lukasniemeier.mensa.model.WeeklyMenu;
import de.lukasniemeier.mensa.utils.SerializableTime;
import de.lukasniemeier.mensa.utils.Utils;

/**
 * Created on 17.09.13.
 */
public abstract class WeeklyMenuParser {

    public static final String SPECIAL_MEAL_NOODLE = "Nudeltheke";

    public static String mapAdditive(Context context, String additiveKey) {
        String[] additiveNames = context.getResources().getStringArray(R.array.meal_additives_name_map);
        try {
            int key = Integer.parseInt(additiveKey);
            if (key < additiveNames.length) {
                return additiveNames[key];
            }
            return additiveKey;
        } catch (NumberFormatException e) {
            return additiveKey;
        }
    }

    public static WeeklyMenuParser create(Context context, Document page, Mensa mensa) {
        if (page.select(".bill_of_fare").size() == 1) {
            return new TodaysMenuParser(context, page, mensa);
        } else {
            return new PreviewMenuParser(context, page, mensa);
        }
    }

    protected final Context context;
    protected final Document document;
    private final Mensa mensa;

    public WeeklyMenuParser(Context context, Document page, Mensa mensa) {
        this.context = context;
        this.document = page;
        this.mensa = mensa;
    }

    public WeeklyMenu parse() throws WeeklyMenuParseException {
        WeeklyMenu weeklyMenu = new WeeklyMenu(mensa, Utils.now());

        Elements menuTables = document.select(".bill_of_fare");
        for (Element menuTable : menuTables) {
            SerializableTime date = parseDate(menuTable);
            Menu menu = parseMenu(weeklyMenu, menuTable);

            weeklyMenu.addMenu(date, menu);
        }
        return weeklyMenu;
    }

    protected String getDefaultPrice(String mealName) {
        if (mealName.equals("Angebot 1")) {
           return "1,40";
        } else if (mealName.equals("Angebot 2")) {
            return "2,00";
        } else if (mealName.equals("Angebot 3")) {
            return "2,50";
        } else if (mealName.equals("Alternativ-Angebot")) {
            return "2,50";
        } else if (mealName.equals(SPECIAL_MEAL_NOODLE)) {
            return "2,00";
        }
        return "?,??";
    }

    protected void addMeal(Menu menu, String name, String description, Collection<MealType> types,
                           String price) {
        String parsedDescription = stripAdditives(description);
        if (name.startsWith(SPECIAL_MEAL_NOODLE) && parsedDescription.isEmpty()) {
            parsedDescription = context.getString(R.string.noodle_description);
        }
        if (!parsedDescription.isEmpty()) {
            menu.add(new Meal(menu, name, parsedDescription, types, parseAdditives(description),
                    context.getString(R.string.price_template, price)));
        }
    }

    private String stripAdditives(String description) {
        return description.replaceAll("\\(\\d+\\)", "");
    }

    private Collection<String> parseAdditives(String description) {
        Set<String> additives = new HashSet<String>();
        Pattern p = Pattern.compile("\\((\\d+)\\)");
        Matcher m = p.matcher(description);
        while (m.find()) {
            additives.add(mapAdditive(context, m.group(1)));
        }
        return additives;
    }

    protected Collection<MealType> parseMealTypes(Element element) {
        Collection<MealType> types = new ArrayList<MealType>();
        MealTypeParser parser = new MealTypeParser();
        for (Element imageElement : element.select("img")) {
            String title = imageElement.attr("title");
            if (title != null && !title.isEmpty()) {
                types.add(parser.parse(title));
            }
        }
        return types;
    }

    protected abstract Menu parseMenu(WeeklyMenu weeklyMenu, Element menuTable) throws WeeklyMenuParseException ;

    protected abstract SerializableTime parseDate(Element menuTable) throws WeeklyMenuParseException ;

}
