package com.blankshrimp.xjtimetablu.widget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankshrimp.xjtimetablu.R;

/**
 * Created by Flabbergast13 on 18/2/13.
 * This fragment will be displayed if there's no primary timetable
 */

public class NonePrimaryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.none_primary_fragment, container, false);
        return view;
    }
}
