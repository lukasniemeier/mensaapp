package de.lukasniemeier.mensa.model;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created on 17.09.13.
 */
public class Meal implements Serializable {

    private final String name;
    private final String description;
    private final Collection<MealType> types;
    private final Collection<String> additives;

    public Meal(String name, String description,
                Collection<MealType> types, Collection<String> additives) {
        this.name = name;
        this.description = description;
        this.types = types;
        this.additives = additives;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Collection<MealType> getTypes() {
        return types;
    }

    public Collection<String> getAdditives() {
        return additives;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", types='" + types + '\'' +
                ", additives='" + additives + '\'' +
                '}';
    }
}
