package net.pubnative.easysteps.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import net.pubnative.easysteps.R;

abstract class SplitDialog {

    private static boolean split_active;

    public static Dialog getDialog(final Context c, final int totalSteps) {
        final Dialog d = new Dialog(c);
        d.setTitle(R.string.split_count);
        d.setContentView(R.layout.dialog_split);

        final SharedPreferences prefs =
                c.getSharedPreferences("pedometer", Context.MODE_MULTI_PROCESS);
        long split_date = prefs.getLong("split_date", -1);
        int split_steps = prefs.getInt("split_steps", totalSteps);
        ((TextView) d.findViewById(R.id.steps))
                .setText(OverviewFragment.formatter.format(totalSteps - split_steps));
        float stepsize = prefs.getFloat("stepsize_value", SettingsFragment.DEFAULT_STEP_SIZE);
        float distance = (totalSteps - split_steps) * stepsize;
        if (prefs.getString("stepsize_unit", SettingsFragment.DEFAULT_STEP_UNIT).equals("cm")) {
            distance /= 100000;
            ((TextView) d.findViewById(R.id.distanceunit)).setText("km");
        } else {
            distance /= 5280;
            ((TextView) d.findViewById(R.id.distanceunit)).setText("mi");
        }
        ((TextView) d.findViewById(R.id.distance))
                .setText(OverviewFragment.formatter.format(distance));
        ((TextView) d.findViewById(R.id.date)).setText(c.getString(R.string.since,
                java.text.DateFormat.getDateTimeInstance().format(split_date)));

        final View started = d.findViewById(R.id.started);
        final View stopped = d.findViewById(R.id.stopped);

        split_active = split_date > 0;

        started.setVisibility(split_active ? View.VISIBLE : View.GONE);
        stopped.setVisibility(split_active ? View.GONE : View.VISIBLE);

        final Button startstop = (Button) d.findViewById(R.id.start);
        startstop.setText(split_active ? R.string.stop : R.string.start);
        startstop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!split_active) {
                    prefs.edit().putLong("split_date", System.currentTimeMillis())
                            .putInt("split_steps", totalSteps).apply();
                    split_active = true;
                    d.dismiss();
                } else {
                    started.setVisibility(View.GONE);
                    stopped.setVisibility(View.VISIBLE);
                    prefs.edit().remove("split_date").remove("split_steps").apply();
                    split_active = false;
                }
                startstop.setText(split_active ? R.string.stop : R.string.start);
            }
        });

        d.findViewById(R.id.close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                d.dismiss();
            }
        });

        return d;
    }
}