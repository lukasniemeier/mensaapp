package de.lukasniemeier.mensa.parser;

import java.text.ParseException;

/**
 * Created on 18.09.13.
 */
public class WeeklyMenuParseException extends Exception {

    public WeeklyMenuParseException(String message, ParseException innerException) {
        super(message, innerException);
    }

    public WeeklyMenuParseException(String message) {
        super(message);
    }
}
