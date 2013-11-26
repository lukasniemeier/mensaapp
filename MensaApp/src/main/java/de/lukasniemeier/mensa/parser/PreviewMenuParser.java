package de.lukasniemeier.mensa.parser;

import android.content.Context;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.lukasniemeier.mensa.model.Mensa;
import de.lukasniemeier.mensa.model.Menu;
import de.lukasniemeier.mensa.model.WeeklyMenu;
import de.lukasniemeier.mensa.utils.SerializableTime;

/**
 * Created on 17.09.13.
 */
public class PreviewMenuParser extends WeeklyMenuParser {

    private static final DateFormat menuDateFormat = new SimpleDateFormat("d. MMMM yyyy",
            Locale.GERMANY);
    private static final Pattern menuDatePattern = Pattern.compile("[a-zA-Z]{2},\\s(.*)$");

    public PreviewMenuParser(Context context, Document page, Mensa mensa) {
        super(context, page, mensa);
    }

    @Override
    protected Menu parseMenu(WeeklyMenu weeklyMenu, Element menuTable) {
        Menu menu = new Menu(weeklyMenu);

        Elements rows = menuTable.select("tr");
        for (int i = 0; i < 4; i++) {
            String name = rows.get(1).children().get(i).text();
            addMeal(menu,
                    name,
                    rows.get(2).children().get(i).text(),
                    parseMealTypes(rows.get(3).children().get(i)),
                    getDefaultPrice(name));
        }
        return menu;
    }

    @Override
    protected SerializableTime parseDate(Element menuTable) throws WeeklyMenuParseException {
        Elements elements = menuTable.select(".date");
        if (elements.size() != 1) {
            throw new WeeklyMenuParseException("No date field found");
        }
        Element dateElement = elements.first();
        try {
            String dateText = dateElement.text();

            Matcher match = menuDatePattern.matcher(dateText);
            if (match.matches()) {
                return new SerializableTime(menuDateFormat.parse(match.group(1)));
            }
            throw new WeeklyMenuParseException("Error finding date in date field");
        } catch (ParseException e) {
            throw new WeeklyMenuParseException("Error parsing date field", e);
        }
    }

}
