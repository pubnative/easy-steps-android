package net.pubnative.easysteps;

import androidx.multidex.MultiDexApplication;

import com.google.android.gms.ads.MobileAds;

import net.pubnative.lite.sdk.HyBid;

public class EasyStepsApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, initializationStatus -> {
        });

        HyBid.initialize(getString(R.string.pnlite_app_token), this, initializedSuccessfully -> {
            HyBid.setTestMode(true);
        });
    }
}
