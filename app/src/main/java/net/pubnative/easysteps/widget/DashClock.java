package net.pubnative.easysteps.widget;

import android.content.Intent;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import net.pubnative.easysteps.Database;
import net.pubnative.easysteps.R;
import net.pubnative.easysteps.ui.MainActivity;
import net.pubnative.easysteps.ui.OverviewFragment;
import net.pubnative.easysteps.util.Util;

/**
 * Class for providing a DashClock (https://code.google.com/p/dashclock)
 * extension
 */
public class DashClock extends DashClockExtension {

    @Override
    protected void onUpdateData(int reason) {
        ExtensionData data = new ExtensionData();
        Database db = Database.getInstance(this);
        int steps = Math.max(db.getCurrentSteps() + db.getSteps(Util.getToday()), 0);
        data.visible(true).status(OverviewFragment.formatter.format(steps))
                .icon(R.drawable.ic_dashclock)
                .clickIntent(new Intent(DashClock.this, MainActivity.class));
        db.close();
        publishUpdate(data);
    }

}
