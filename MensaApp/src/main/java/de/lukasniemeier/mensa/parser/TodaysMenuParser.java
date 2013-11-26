package de.lukasniemeier.mensa.parser;

import android.content.Context;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.lukasniemeier.mensa.R;
import de.lukasniemeier.mensa.model.Mensa;
import de.lukasniemeier.mensa.model.Menu;
import de.lukasniemeier.mensa.model.WeeklyMenu;
import de.lukasniemeier.mensa.utils.SerializableTime;
import de.lukasniemeier.mensa.utils.Utils;

/**
 * Created on 17.09.13.
 */
public class TodaysMenuParser extends WeeklyMenuParser {

    private static final String priceGroup = "Studierende";

    private final Map<String, Collection<String>> alternativeNameMap;

    public TodaysMenuParser(Context context, Document page, Mensa mensa) {
        super(context, page, mensa);
        alternativeNameMap = new HashMap<String, Collection<String>>();
        alternativeNameMap.put("Veganes Angebot", Arrays.asList("Veganes Essen"));
    }

    @Override
    protected SerializableTime parseDate(Element menuTable) {
        return Utils.today();
    }

    @Override
    protected Menu parseMenu(WeeklyMenu weeklyMenu, Element menuTable) throws WeeklyMenuParseException {
        Menu menu = new Menu(weeklyMenu);

        Elements rows = menuTable.select("tr");

        for (Element nameElement : rows.select("td.head")) {
            int index = nameElement.parent().children().indexOf(nameElement);

            Element nextParent = nameElement.parent().nextElementSibling();
            if (nextParent == null) {
                throw new WeeklyMenuParseException("Expected parent for description");
            }

            Element nextNextParent = nextParent.nextElementSibling();
            if (nextParent == null) {
                throw new WeeklyMenuParseException("Expected description and meal type elements");
            }

            Element descriptionElement = getChild(index, nextParent);
            Element mealTypesElement = getChild(index, nextNextParent);

            String name = nameElement.text();
            String price = getPrice(menuTable.ownerDocument(), name);

            addMeal(menu, nameElement.text(), descriptionElement.text(), parseMealTypes(mealTypesElement), price);
        }
        return menu;
    }

    private String getPrice(Document document, String name) {
        String price = null;
        for (String alternativeName : getAlternativeNames(name)) {
            price = findPriceInDocument(document, alternativeName);
            if (price != null) {
                break;
            }
        }
        if (price == null) {
            price = getDefaultPrice(name);
        }
        return context.getString(R.string.price_template, price);
    }

    private Collection<String> getAlternativeNames(String name) {
        Collection<String> names = alternativeNameMap.get(name);
        if (names == null) {
            names = new ArrayList<String>(1);
        } else {
            names = new ArrayList<String>(names);
        }
        // always add original name
        names.add(name);
        return names;
    }

    private String findPriceInDocument(Document document, String name) {
        String paragraphRegex = String.format("%s.*" + priceGroup + ":", name);
        Elements priceElements = document.select("p:matches(" + paragraphRegex + ")");
        if (priceElements.size() == 1) {
            Pattern priceRegex = Pattern.compile(name + ".*?" + priceGroup + ": (\\d+,\\d+)", Pattern.DOTALL);
            Matcher match = priceRegex.matcher(priceElements.get(0).text());
            if (match.find()) {
                return match.group(1);
            }
        }
        return null;
    }

    private Element getChild(int index, Element parent) throws WeeklyMenuParseException {
        if (parent.children().size() <= index) {
            throw new WeeklyMenuParseException("Expected element for description or meal types");
        }
        return parent.child(index);
    }

}
