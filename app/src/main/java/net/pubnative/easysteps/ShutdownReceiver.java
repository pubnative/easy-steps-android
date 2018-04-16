package net.pubnative.easysteps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.pubnative.easysteps.util.Logger;
import net.pubnative.easysteps.util.Util;

public class ShutdownReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (BuildConfig.DEBUG) Logger.log("shutting down");

        context.startService(new Intent(context, SensorListener.class));

        // if the user used a root script for shutdown, the DEVICE_SHUTDOWN
        // broadcast might not be send. Therefore, the app will check this
        // setting on the next boot and displays an error message if it's not
        // set to true
        context.getSharedPreferences("pedometer", Context.MODE_PRIVATE).edit()
                .putBoolean("correctShutdown", true).commit();

        Database db = Database.getInstance(context);
        // if it's already a new day, add the temp. steps to the last one
        if (db.getSteps(Util.getToday()) == Integer.MIN_VALUE) {
            int steps = db.getCurrentSteps();
            int pauseDifference = steps -
                    context.getSharedPreferences("pedometer", Context.MODE_PRIVATE)
                            .getInt("pauseCount", steps);
            db.insertNewDay(Util.getToday(), steps - pauseDifference);
            if (pauseDifference > 0) {
                // update pauseCount for the new day
                context.getSharedPreferences("pedometer", Context.MODE_PRIVATE).edit()
                        .putInt("pauseCount", steps).commit();
            }
        } else {
            db.addToLastEntry(db.getCurrentSteps());
        }
        // current steps will be reset on boot @see BootReceiver
        db.close();
    }

}