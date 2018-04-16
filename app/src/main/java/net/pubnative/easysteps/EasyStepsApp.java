package net.pubnative.easysteps;

import android.app.Application;

import net.pubnative.lite.sdk.PNLite;

public class EasyStepsApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PNLite.initialize(getString(R.string.pnlite_app_token), this);
    }
}
