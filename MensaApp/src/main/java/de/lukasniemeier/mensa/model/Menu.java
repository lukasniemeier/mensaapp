package de.lukasniemeier.mensa.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.lukasniemeier.mensa.utils.SerializableTime;

/**
 * Created on 17.09.13.
 */
public class Menu implements Serializable {

    private final WeeklyMenu weeklyMenu;
    private final List<Meal> meals;

    public Menu(WeeklyMenu weeklyMenu) {
        this.weeklyMenu = weeklyMenu;
        this.meals = new ArrayList<Meal>();
    }

    public WeeklyMenu getWeeklyMenu() {
        return weeklyMenu;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void add(Meal meal) {
        meals.add(meal);
    }

    public SerializableTime getMenuDay() {
        return weeklyMenu.getMenus().inverse().get(this);
    }
}
