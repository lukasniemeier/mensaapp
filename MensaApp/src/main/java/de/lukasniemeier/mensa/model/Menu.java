package de.lukasniemeier.mensa.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 17.09.13.
 */
public class Menu implements Serializable {

    private List<Meal> meals;

    public Menu() {
        this.meals = new ArrayList<Meal>();
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void add(Meal meal) {
        meals.add(meal);
    }
}
