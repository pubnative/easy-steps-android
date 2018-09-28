package net.pubnative.easysteps.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.PermissionChecker;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;

import net.pubnative.easysteps.BuildConfig;
import net.pubnative.easysteps.R;
import net.pubnative.easysteps.SensorListener;
import net.pubnative.easysteps.util.GoogleFit;
import net.pubnative.easysteps.util.Logger;
import net.pubnative.easysteps.util.PlayServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private final static int RC_RESOLVE = 1;
    private final static int RC_LEADERBOARDS = 2;

    @Override
    protected void onCreate(final Bundle b) {
        super.onCreate(b);
        startService(new Intent(this, SensorListener.class));

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (b == null) {
            // Create new fragment and transaction
            Fragment newFragment = new OverviewFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this
            // fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.fragment_container, newFragment);

            // Commit the transaction
            transaction.commit();
        }


        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this, this, this);
        builder.addApi(Games.API, Games.GamesOptions.builder().build());
        builder.addScope(Games.SCOPE_GAMES);
        builder.addApi(Fitness.HISTORY_API);
        builder.addApi(Fitness.RECORDING_API);
        builder.addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE));

        mGoogleApiClient = builder.build();

        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= 23 && PermissionChecker
                .checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (BuildConfig.DEBUG) Logger.log("Main::onStart");
        if (getSharedPreferences("pedometer_playservices", Context.MODE_PRIVATE)
                .getBoolean("autosignin", false) && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (BuildConfig.DEBUG) Logger.log("Main::onStop");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public GoogleApiClient getGC() {
        return mGoogleApiClient;
    }

    public void beginSignIn() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    public void signOut() {
        if (mGoogleApiClient.isConnected()) {
            Games.signOut(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
        getSharedPreferences("pedometer_playservices", Context.MODE_PRIVATE).edit()
                .putBoolean("autosignin", false).apply();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStackImmediate();
        } else {
            finish();
        }
    }

    public boolean optionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStackImmediate();
                break;
            case R.id.action_settings:
                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SettingsContainerFragment()).addToBackStack(null)
                        .commit();
                break;
            case R.id.action_leaderboard:
            case R.id.action_achievements:
                if (mGoogleApiClient.isConnected()) {
                    startActivityForResult(item.getItemId() == R.id.action_achievements ?
                                    Games.Achievements.getAchievementsIntent(mGoogleApiClient) :
                                    Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient),
                            RC_LEADERBOARDS);
                } else {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                    builder2.setTitle(R.string.sign_in_necessary);
                    builder2.setMessage(R.string.please_sign_in_with_your_google_account);
                    builder2.setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    getFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, new SettingsContainerFragment())
                                            .addToBackStack(null).commit();
                                }
                            });
                    builder2.setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder2.create().show();
                }
                break;
            case R.id.action_about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.about);
                TextView tv = new TextView(this);
                tv.setPadding(40, 40, 40, 40);
                tv.setText(R.string.about_text_links);
                try {
                    tv.append(getString(R.string.about_app_version,
                            getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
                } catch (NameNotFoundException e1) {
                    // should not happen as the app is definitely installed when
                    // seeing the dialog
                    e1.printStackTrace();
                }
                tv.setMovementMethod(LinkMovementMethod.getInstance());
                builder.setView(tv);
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
                break;
        }
        return true;
    }

    @Override
    public void onConnected(final Bundle bundle) {
        PlayServices.achievementsAndLeaderboard(mGoogleApiClient, this);
        new GoogleFit.Sync(mGoogleApiClient, this).execute();
        Fitness.RecordingApi.subscribe(mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA);
        getSharedPreferences("pedometer_playservices", Context.MODE_PRIVATE).edit()
                .putBoolean("autosignin", true).apply();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            // This problem can be fixed. So let's try to fix it.
            try {
                // launch appropriate UI flow (which might, for example, be the
                // sign-in flow)
                connectionResult.startResolutionForResult(this, RC_RESOLVE);
            } catch (IntentSender.SendIntentException e) {
                // Try connecting again
                mGoogleApiClient.connect();
            }
        } else {
            if (!isFinishing() && !isDestroyed()) {
                GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0)
                        .show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == RC_RESOLVE) {
            // We're coming back from an activity that was launched to resolve a
            // connection problem. For example, the sign-in UI.
            if (resultCode == Activity.RESULT_OK && !mGoogleApiClient.isConnected() &&
                    !mGoogleApiClient.isConnecting()) {
                // Ready to try to connect again.
                mGoogleApiClient.connect();
            } else if (resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED &&
                    !mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled.
                mGoogleApiClient.disconnect();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}