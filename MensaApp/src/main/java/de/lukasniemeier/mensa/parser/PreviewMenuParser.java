package de.lukasniemeier.mensa.parser;

import android.content.Context;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.lukasniemeier.mensa.model.Menu;

/**
 * Created on 17.09.13.
 */
public class PreviewMenuParser extends WeeklyMenuParser {

    private static final DateFormat menuDateFormat = new SimpleDateFormat("d. MMMM yyyy",
            Locale.GERMANY);
    private static final Pattern menuDatePattern = Pattern.compile("[a-zA-Z]{2},\\s(.*)$");

    public PreviewMenuParser(Context context, Document page) {
        super(context, page);
    }

    @Override
    protected Menu parseMenu(Element menuTable) {
        Menu menu = new Menu();

        Elements rows = menuTable.select("tr");
        for (int i = 0; i < 4; i++) {
            addMeal(menu,
                    rows.get(1).children().get(i).text(),
                    rows.get(2).children().get(i).text(),
                    parseMealTypes(rows.get(3).children().get(i)));
        }
        return menu;
    }

    @Override
    protected Date parseDate(Element menuTable) throws WeeklyMenuParseException {
        Elements elements = menuTable.select(".date");
        if (elements.size() != 1) {
            throw new WeeklyMenuParseException("No date field found");
        }
        Element dateElement = elements.first();
        try {
            String dateText = dateElement.text();

            Matcher match = menuDatePattern.matcher(dateText);
            if (match.matches()) {
                return menuDateFormat.parse(match.group(1));
            }
            throw new WeeklyMenuParseException("Error finding date in date field");
        } catch (ParseException e) {
            throw new WeeklyMenuParseException("Error parsing date field", e);
        }
    }

}
