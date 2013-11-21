package de.lukasniemeier.mensa.model;

import android.text.format.Time;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.lukasniemeier.mensa.utils.Utils;

/**
 * Created on 17.09.13.
 */
public class Mensa {

    public static List<Mensa> database;

    public static List<Mensa> getMensas() {
        if (database == null) {
            try {
                Mensa griebnitzsee = new Mensa(
                        "Griebnitzsee", "August-Bebel-Str. 89\n14482 Potsdam",
                        "Montag - Donnerstag: 08.00 - 19.00 Uhr\nFreitag: 08.00 - 15.00 Uhr\nSamstag: 09.30 - 13.00 Uhr",
                        "Montag - Freitag: 08.00 - 15.00 Uhr",
                        new URL("http://www.studentenwerk-potsdam.de/mensa-griebnitzsee.html"));
                Mensa golm = new Mensa(
                        "Golm", "Karl-Liebknecht-Str. 24/25\n14476 Potsdam OT Golm",
                        "Montag - Donnerstag: 09.00 - 19.00 Uhr\nFreitag: 09.00 - 15.00 Uhr",
                        "Montag - Freitag: 09.00 - 15.00 Uhr",
                        new URL("http://www.studentenwerk-potsdam.de/mensa-golm.html"));
                Mensa park = new Mensa(
                        "Am Neuen Palais",
                        "Palais", "Am Neuen Palais 10 Haus 12\n14482 Potsdam",
                        "Montag - Donnerstag: 10.00 - 19.00 Uhr\nFreitag: 10.00 - 15.00 Uhr",
                        "Montag - Donnerstag: 10.00 - 15.00 Uhr\nFreitag: 10.00 - 14.30 Uhr",
                        new URL("http://www.studentenwerk-potsdam.de/mensa-am-neuen-palais.html"));
                Mensa friedrich = new Mensa(
                        "Friedrich-Ebert-Strasse","Friedrich-Ebert-Str.", "Friedrich-Ebert-Str. 4\n14467 Potsdam",
                        "Montag - Donnerstag: 08.00 - 16.00 Uhr\nFreitag: 08.00 - 15.00 Uhr",
                        "Montag - Donnerstag: 08.00 - 15.00 Uhr\nFreitag: 08.00 - 14.30 Uhr",
                        new URL("http://www.studentenwerk-potsdam.de/de/mensa-friedrich-ebert-strasse.html"));
                Mensa pappelallee = new Mensa(
                        "Pappelallee", "Kiepenheuerallee 5\n14469 Potsdam",
                        "Montag - Donnerstag: 10.30 - 15.00 Uhr\nFreitag: 10.30 - 14.30 Uhr\nSamstag: 11.00 - 14.00 Uhr",
                        "Montag - Donnerstag: 08.00 - 15.00 Uhr\nFreitag: 08.00 - 14.30 Uhr",
                        new URL("http://www.studentenwerk-potsdam.de/de/mensa-pappelallee.html"));
                Mensa brandenburg = new Mensa(
                        "Brandenburg", "Magdeburger Strasse 50\n14770 Brandenburg an der Havel",
                        "Montag - Freitag: 11.00 - 14.00 Uhr",
                        "Montag - Donnerstag: 08.00 - 15.00 Uhr\nFreitag: 08.00 - 14.30 Uhr",
                        new URL("http://www.studentenwerk-potsdam.de/de/mensa-brandenburg.html"));
                Mensa wildau = new Mensa(
                        "Wildau", "Bahnhofstr. 1\n15745 Wildau",
                        "Montag - Freitag: 09.00 - 18.00 Uhr\nSamstag: 08.00 - 13.00 Uhr",
                        "Montag - Freitag: 08.00 - 17.00 Uhr",
                        new URL("http://www.studentenwerk-potsdam.de/de/mensa-wildau.html"));
                database = Arrays.asList(griebnitzsee, golm, park,
                        friedrich, pappelallee, brandenburg, wildau);
            } catch (MalformedURLException e) {
                database = Collections.emptyList();
            }
        }
        return database;
    }

    private final String name;
    private final String shortName;
    private final String address;
    private final String openingTimes;
    private final String openingTimesOffSeason;
    private final URL detailMenuURL;

    public Mensa(String name, String address, String openingTimes, String openingTimesOffSeason, URL detailMenuURL) {
        this(name, name, address, openingTimes, openingTimesOffSeason, detailMenuURL);
    }

    public Mensa(String name, String shortName, String address, String openingTimes, String openingTimesOffSeason, URL detailMenuURL) {
        this.name = name;
        this.shortName = shortName;
        this.address = address;
        this.openingTimes = openingTimes;
        this.openingTimesOffSeason = openingTimesOffSeason;
        this.detailMenuURL = detailMenuURL;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getAddress() {
        return address;
    }

    public String getOpeningTimes() {
        Time now = Utils.now();

        Time startWinterTerm = new Time(now);
        startWinterTerm.month = 9;
        startWinterTerm.monthDay = 14;

        Time endWinterTerm = new Time(now);
        endWinterTerm.year++;
        endWinterTerm.month = 1;
        endWinterTerm.monthDay = 8;

        Time startSummerTerm = new Time(now);
        startSummerTerm.month = 3;
        startSummerTerm.monthDay = 7;

        Time endSummerTerm = new Time(now);
        endSummerTerm.month = 6;
        endSummerTerm.monthDay = 20;

        if (now.after(startWinterTerm) && now.before(endWinterTerm)) {
            return openingTimes;
        }
        if (now.after(startSummerTerm) && now.before(endSummerTerm)) {
            return openingTimes;
        }
        return openingTimesOffSeason;
    }

    public URL getDetailMenuURL() {
        return detailMenuURL;
    }

    @Override
    public String toString() {
        return "Mensa{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", openingTimes='" + openingTimes + '\'' +
                ", detailMenuURL=" + detailMenuURL +
                '}';
    }
}
