package net.pubnative.easysteps.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.pubnative.easysteps.R;

public class SettingsContainerFragment extends Fragment {
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

        return view;
    }
}
