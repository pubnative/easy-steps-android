package net.pubnative.easysteps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.pubnative.easysteps.util.Logger;

public class AppUpdatedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (BuildConfig.DEBUG)
            Logger.log("app updated");
        context.startService(new Intent(context, SensorListener.class));
    }

}