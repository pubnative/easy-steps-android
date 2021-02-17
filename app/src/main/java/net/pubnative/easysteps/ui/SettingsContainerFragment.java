package net.pubnative.easysteps.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import net.pubnative.easysteps.R;
import net.pubnative.lite.sdk.views.HyBidMRectAdView;

public class SettingsContainerFragment extends Fragment implements HyBidMRectAdView.Listener {
    private static final String TAG = SettingsContainerFragment.class.getSimpleName();

    private HyBidMRectAdView mMRectView;
    private PublisherAdView mGAMMRectView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_container, container, false);

        if (savedInstanceState == null) {
            Fragment newFragment = new SettingsFragment();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

            transaction.add(R.id.settings_fragment_container, newFragment);

            transaction.commit();
        }

        mMRectView = view.findViewById(R.id.hybid_mrect);
        mGAMMRectView = view.findViewById(R.id.gam_mrect);
        mGAMMRectView.setAdListener(mGAMAdListener);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        loadAd();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mMRectView.destroy();
        mGAMMRectView.setAdListener(null);
        mGAMMRectView.destroy();
    }

    public void loadAd() {
        //mMRectView.load(getString(R.string.pnlite_mrect_zone_id), this);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mGAMMRectView.loadAd(adRequest);
    }

    @Override
    public void onAdLoaded() {
        if (getActivity() != null && isResumed()) {
            mMRectView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAdLoadFailed(Throwable throwable) {
        if (getActivity() != null && isResumed()) {
            mMRectView.setVisibility(View.GONE);
        }
        Log.e(TAG, throwable.getMessage());
    }

    @Override
    public void onAdImpression() {
        Log.d(TAG, "HyBid: onAdImpression");
    }

    @Override
    public void onAdClick() {
        if (getActivity() != null && isResumed()) {
            mMRectView.setVisibility(View.GONE);
        }
        Log.d(TAG, "HyBid: onAdClick");
    }

    private final AdListener mGAMAdListener = new AdListener() {
        @Override
        public void onAdLoaded() {
            if (getActivity() != null && isResumed()) {
                mGAMMRectView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAdFailedToLoad(LoadAdError loadAdError) {
            if (getActivity() != null && isResumed()) {
                mGAMMRectView.setVisibility(View.GONE);
            }
            Log.e(TAG, loadAdError.getMessage());
        }

        @Override
        public void onAdImpression() {
            Log.d(TAG, "GAM: onAdImpression");
        }

        @Override
        public void onAdClicked() {
            if (getActivity() != null && isResumed()) {
                mGAMMRectView.setVisibility(View.GONE);
            }
            Log.d(TAG, "GAM: onAdClick");
        }
    };
}
