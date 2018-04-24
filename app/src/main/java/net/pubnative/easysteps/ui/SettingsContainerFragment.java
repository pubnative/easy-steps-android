package net.pubnative.easysteps.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;

import net.pubnative.easysteps.R;
import net.pubnative.lite.sdk.api.MRectRequestManager;
import net.pubnative.lite.sdk.api.RequestManager;
import net.pubnative.lite.sdk.models.Ad;
import net.pubnative.lite.sdk.utils.PrebidUtils;

public class SettingsContainerFragment extends Fragment implements MoPubView.BannerAdListener {
    private static final String TAG = SettingsContainerFragment.class.getSimpleName();

    private MoPubView mMRectView;

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

        mMRectView = view.findViewById(R.id.mopub_mrect);
        mMRectView.setBannerAdListener(this);
        mMRectView.setAutorefreshEnabled(false);

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

    public void loadAd() {
        RequestManager requestManager = new MRectRequestManager();
        requestManager.setZoneId(getString(R.string.pnlite_mrect_zone_id));
        requestManager.setRequestListener(new RequestManager.RequestListener() {
            @Override
            public void onRequestSuccess(Ad ad) {
                if (getActivity() != null && isResumed()) {
                    mMRectView.setAdUnitId(getString(R.string.mopub_mrect_ad_unit_id));
                    mMRectView.setKeywords(PrebidUtils.getPrebidKeywords(ad, getString(R.string.pnlite_mrect_zone_id)));
                    mMRectView.loadAd();
                }
            }

            @Override
            public void onRequestFail(Throwable throwable) {
                if (getActivity() != null && isResumed()) {
                    mMRectView.setAdUnitId(getString(R.string.mopub_mrect_ad_unit_id));
                    mMRectView.loadAd();
                }
                Log.e(TAG, throwable.getMessage());
            }
        });
        requestManager.requestAd();
    }

    @Override
    public void onBannerLoaded(MoPubView banner) {
        if (getActivity() != null && isResumed()) {
            mMRectView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
        Log.e(TAG, errorCode.toString());
    }

    @Override
    public void onBannerClicked(MoPubView banner) {
        if (getActivity() != null && isResumed()) {
            mMRectView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBannerExpanded(MoPubView banner) { }

    @Override
    public void onBannerCollapsed(MoPubView banner) { }
}
