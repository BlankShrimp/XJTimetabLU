package com.blankshrimp.xjtimetablu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.blankshrimp.xjtimetablu.widget.SettingsFragment;

import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    private HeadReceiver headIconReceiver;
    private BackReceiver backImgReceiver;
    private LocalBroadcastManager headManager;
    private LocalBroadcastManager backManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        getFragmentManager().beginTransaction().replace(R.id.settingsContentLayout, new SettingsFragment()).commit();

        headManager = LocalBroadcastManager.getInstance(this);
        headIconReceiver = new HeadReceiver();
        IntentFilter headFilter = new IntentFilter();
        headFilter.addAction("com.example.broadcast.HEAD_ICON");
        headManager.registerReceiver(headIconReceiver, headFilter);

        backManager = LocalBroadcastManager.getInstance(this);
        backImgReceiver = new BackReceiver();
        IntentFilter backFilter = new IntentFilter();
        backFilter.addAction("com.example.broadcast.BACK_IMG");
        backManager.registerReceiver(backImgReceiver, backFilter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        headManager.unregisterReceiver(headIconReceiver);
        backManager.unregisterReceiver(backImgReceiver);
    }
}

class HeadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive (Context context, Intent intent) {
        Intent intent1 = new Intent("com.example.activitytest.ACTION_START");
        intent1.addCategory("com.EMPTY_ACTIVITY");
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }
}
class BackReceiver extends BroadcastReceiver {
    @Override
    public void onReceive (Context context, Intent intent) {
    }
}