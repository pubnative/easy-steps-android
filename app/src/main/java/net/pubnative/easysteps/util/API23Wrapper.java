package net.pubnative.easysteps.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.M)
public class API23Wrapper {

    public static void requestPermission(final Activity a, final String[] permissions) {
        a.requestPermissions(permissions, 42);
    }
}
