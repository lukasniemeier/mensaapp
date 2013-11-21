package de.lukasniemeier.mensa;

import android.app.Application;

/**
 * Created on 19.09.13.
 */
public class MensaApplication extends Application implements ResourceService {

    private static ResourceService resourceService;

    public static ResourceService getResourceService() {
        return resourceService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MensaApplication.resourceService = this;
    }

    @Override
    public String localizeString(int id) {
        return getString(id);
    }

}
