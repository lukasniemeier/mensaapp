package de.lukasniemeier.mensa;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import de.lukasniemeier.mensa.model.WeeklyMenu;
import de.lukasniemeier.mensa.parser.WeeklyMenuParseException;
import de.lukasniemeier.mensa.parser.WeeklyMenuParser;
import de.lukasniemeier.mensa.utils.Utils;

/**
 * Created on 17.09.13.
 */
public class WeeklyMenuTask extends AsyncTask<String, Integer, Pair<WeeklyMenu, Exception>> {

    public interface WeeklyMenuReceiver {
        void onWeeklyMenuSuccess(WeeklyMenu weeklyMenu);
        void onWeeklyMenuError(Exception error);
    }

    private final Context context;
    private final WeeklyMenuReceiver receiver;

    public WeeklyMenuTask(Context context, WeeklyMenuReceiver receiver) {
        this.context = context;
        this.receiver = receiver;
    }

    @Override
    protected Pair<WeeklyMenu, Exception> doInBackground(String... urls) {
        List<WeeklyMenu> menus = new ArrayList<WeeklyMenu>();
        for (String url : urls) {
            try {
                Document document = Jsoup.connect(url).get();
                WeeklyMenuParser parser = WeeklyMenuParser.create(context, document);
                menus.add(parser.parse());
            } catch (WeeklyMenuParseException wmpe) {
                Log.w(TAG, String.format(context.getString(R.string.error_menu_parse), url), wmpe);
                return new Pair<WeeklyMenu, Exception>(null, wmpe);
            } catch (Exception e) {
                Log.e(TAG, String.format(context.getString(R.string.error_menu_download), url), e);
                return new Pair<WeeklyMenu, Exception>(null, e);
            }
        }

        return new Pair<WeeklyMenu, Exception>(WeeklyMenu.merge(Utils.now(), menus), null);
    }

    @Override
    protected void onPostExecute(Pair<WeeklyMenu, Exception> result) {
        if (result.first == null) {
            receiver.onWeeklyMenuError(result.second);
        } else {
            receiver.onWeeklyMenuSuccess(result.first);
        }
    }

    private static final String TAG = WeeklyMenuTask.class.getSimpleName();
}
