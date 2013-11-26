package de.lukasniemeier.mensa.model;

import android.text.format.Time;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import de.lukasniemeier.mensa.utils.SerializableTime;
import de.lukasniemeier.mensa.utils.Utils;

/**
 * Created on 17.09.13.
 */
public class WeeklyMenu implements Serializable {

    private final Mensa mensa;
    private SerializableTime timestamp;
    private BiMap<SerializableTime, Menu> menuMap;

    public WeeklyMenu(Mensa mensa, SerializableTime timestamp) {
        this.mensa = mensa;
        this.timestamp = timestamp;
        this.menuMap = HashBiMap.create();
    }

    public Mensa getMensa() {
        return mensa;
    }

    public Time getTimestamp() {
        return timestamp.getTime();
    }

    public BiMap<SerializableTime, Menu> getMenus() {
        return menuMap;
    }

    public void addMenu(SerializableTime date, Menu menu) {
        menuMap.put(date, menu);
    }

    public Menu getMenu(SerializableTime date) {
        return menuMap.get(date);
    }

    public boolean hasMenu(SerializableTime date) {
        return menuMap.containsKey(date);
    }

    public static WeeklyMenu merge(Mensa mensa, SerializableTime timestamp, List<WeeklyMenu> menus) {
        WeeklyMenu merged = new WeeklyMenu(mensa, timestamp);
        for (WeeklyMenu weeklyMenu : menus) {
            for (Map.Entry<SerializableTime, Menu> entry : weeklyMenu.menuMap.entrySet()) {
                merged.addMenu(entry.getKey(), entry.getValue());
            }
        }
        return merged;
    }

    public boolean isOutdated() {
        SerializableTime now = Utils.now();
        return now.getYearDay() > timestamp.getYearDay() || now.getYear() > timestamp.getYear();
    }
}
