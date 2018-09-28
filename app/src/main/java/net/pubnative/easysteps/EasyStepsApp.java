package net.pubnative.easysteps;

import android.app.Application;

import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;

import net.pubnative.lite.sdk.HyBid;

public class EasyStepsApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        HyBid.initialize(getString(R.string.pnlite_app_token), this);

        SdkConfiguration sdkConfiguration = new SdkConfiguration
                .Builder(getString(R.string.mopub_banner_ad_unit_id))
                .build();
        MoPub.initializeSdk(this, sdkConfiguration, new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {

            }
        });
    }
}
