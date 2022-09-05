package net.pubnative.easysteps;

import android.app.Application;

import net.pubnative.lite.sdk.HyBid;

public class EasyStepsApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HyBid.initialize("dde3c298b47648459f8ada4a982fa92d", this, new HyBid.InitialisationListener() {
            @Override
            public void onInitialisationFinished(boolean initializedSuccessfully) {

            }
        });
    }
}
