package de.lukasniemeier.mensa.utils;

import android.text.format.Time;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * Created on 26.11.13.
 */


public class SerializableTime implements Comparable<SerializableTime>, Serializable {

    private static Time toTime(Date date) {
        Time time = new Time();
        time.set(date.getTime());
        return time;
    }

    private Time timestamp;

    public SerializableTime(Time timestamp) {
        this.timestamp = timestamp;
    }

    public SerializableTime(Date date) {
        this(toTime(date));
    }

    public Time getTime() {
        return timestamp;
    }

    private void writeObject(java.io.ObjectOutputStream out)
            throws IOException {
        out.writeObject(timestamp.toMillis(false));
    }

    private void readObject(java.io.ObjectInputStream in)
            throws IOException, ClassNotFoundException {
        long ms = (Long) in.readObject();
        this.timestamp = new Time();
        this.timestamp.set(ms);
    }

    public long toMillis() {
        return timestamp.toMillis(false);
    }

    public int getYearDay() {
        return timestamp.yearDay;
    }

    public int getYear() {
        return timestamp.year;
    }

    public Date toDate() {
        return new Date(toMillis());
    }

    @Override
    public int compareTo(SerializableTime other) {
        return Time.compare(this.getTime(), other.getTime());
    }
}
