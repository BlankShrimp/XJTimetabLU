package com.blankshrimp.xjtimetablu.widget;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.blankshrimp.xjtimetablu.R;

/**
 * Created by Flabbergast13 on 18/3/9.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
    }

}
