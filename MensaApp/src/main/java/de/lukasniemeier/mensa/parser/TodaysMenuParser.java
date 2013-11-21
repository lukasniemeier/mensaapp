package de.lukasniemeier.mensa.parser;

import android.content.Context;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.lukasniemeier.mensa.model.Menu;
import de.lukasniemeier.mensa.utils.Utils;

/**
 * Created on 17.09.13.
 */
public class TodaysMenuParser extends WeeklyMenuParser {

    private static final DateFormat menuDateFormat = new SimpleDateFormat("c, d. MMMM yyyy");

    public TodaysMenuParser(Context context, Document page) {
        super(context, page);
    }

    @Override
    protected Menu parseMenu(Element menuTable) throws WeeklyMenuParseException {
        Menu menu = new Menu();

        Elements rows = menuTable.select("tr");
        int mealCount = rows.get(0).children().size();
        for (int i = 0; i < mealCount; i++) {
            addMeal(menu,
                    rows.get(0).children().get(i).text(),
                    rows.get(1).children().get(i).text(),
                    parseMealTypes(rows.get(2).children().get(i)));
        }
        return menu;
    }

    @Override
    protected Date parseDate(Element menuTable) {
        return Utils.today();
    }

}
