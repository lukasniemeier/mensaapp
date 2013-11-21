package de.lukasniemeier.mensa.parser;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.Arrays;

import de.lukasniemeier.mensa.model.MealType;

/**
 * Created on 18.09.13.
 */
public class MealTypeParser {


    public MealType parse(final String title) {
        return Iterables.find(Arrays.asList(MealType.values()), new Predicate<MealType>() {
            @Override
            public boolean apply(MealType type) {
                return type.getParseString().equals(title);
            }
        }, MealType.UNKNOWN);
    }
}
