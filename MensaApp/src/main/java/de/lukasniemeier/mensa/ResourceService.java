package de.lukasniemeier.mensa;

import android.content.res.Resources;

/**
 * Created on 19.09.13.
 */
public interface ResourceService {
    String localizeString(int id);
    Resources getResources();
}
