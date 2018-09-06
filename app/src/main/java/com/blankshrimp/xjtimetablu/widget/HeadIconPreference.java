package com.blankshrimp.xjtimetablu.widget;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;

/**
 * Created by Flabbergast13 on 18/3/12.
 */

public class HeadIconPreference extends Preference {

    private static final String keyHeadIcon = "headIcon";
    private Context context;

    public HeadIconPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        setKey(keyHeadIcon);
    }

    @Override
    protected void onClick() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent("com.example.broadcast.HEAD_ICON");
        localBroadcastManager.sendBroadcast(intent);
    }
}
