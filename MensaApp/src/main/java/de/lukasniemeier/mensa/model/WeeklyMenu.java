package de.lukasniemeier.mensa.model;

import android.text.format.Time;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import de.lukasniemeier.mensa.utils.Utils;

/**
 * Created on 17.09.13.
 */
public class WeeklyMenu implements Serializable {

    private Time timestamp;
    private TreeMap<Date, Menu> menuMap;

    public WeeklyMenu(Time timestamp) {
        this.timestamp = timestamp;
        this.menuMap = new TreeMap<Date, Menu>();
    }

    public void addMenu(Date date, Menu menu) {
        menuMap.put(date, menu);
    }

    public TreeMap<Date, Menu> getMenus() {
        return menuMap;
    }

    public Menu getMenu(Date date) {
        return menuMap.get(date);
    }

    public boolean hasMenu(Date date) {
        return menuMap.containsKey(date);
    }

    public static WeeklyMenu merge(Time timestamp, List<WeeklyMenu> menus) {
        WeeklyMenu merged = new WeeklyMenu(timestamp);
        for (WeeklyMenu weeklyMenu : menus) {
            for (Map.Entry<Date, Menu> entry : weeklyMenu.menuMap.entrySet()) {
                merged.addMenu(entry.getKey(), entry.getValue());
            }
        }
        return merged;
    }

    public boolean isOutdated() {
        Time now = Utils.now();
        return now.yearDay > timestamp.yearDay || now.year > timestamp.year;
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(menuMap);
        out.writeObject(timestamp.toMillis(false));
    }

    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream in)
        throws IOException, ClassNotFoundException {
        this.menuMap = (TreeMap<Date, Menu>) in.readObject();
        long ms = (Long) in.readObject();
        this.timestamp = new Time();
        this.timestamp.set(ms);
    }
}
