package net.pubnative.easysteps;

import android.app.Application;

import net.pubnative.lite.sdk.HyBid;

public class EasyStepsApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HyBid.initialize(getString(R.string.pnlite_app_token), this, new HyBid.InitialisationListener() {
            @Override
            public void onInitialisationFinished(boolean initializedSuccessfully) {

            }
        });
    }
}
