package com.blankshrimp.xjtimetablu;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.blankshrimp.xjtimetablu.util.NewListDAO;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScanActivity extends AppCompatActivity {

    private AutoCompleteTextView name;
    private AutoCompleteTextView remark;
    private String captured = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.scanToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (AutoCompleteTextView) findViewById(R.id.name);
        Button button = (Button) findViewById(R.id.scan);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean cancel = false;
                name.setError(null);
                View focusView = null;
                String nameCapture = name.getText().toString();
                if (TextUtils.isEmpty(nameCapture)) {
                    name.setError(getString(R.string.error_empty));
                    focusView = name;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {
                    ActivityCompat.requestPermissions(ScanActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                    new IntentIntegrator(ScanActivity.this).setOrientationLocked(true).initiateScan();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //重写ToolBar返回按钮的行为，防止重新打开父Activity重走生命周期方法
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null) {
            if(intentResult.getContents() == null) {
            } else {
                captured = intentResult.getContents();
                name = (AutoCompleteTextView) findViewById(R.id.name);
                remark = (AutoCompleteTextView) findViewById(R.id.remark);
                try {
                    NewListDAO newListDAO = new NewListDAO(ScanActivity.this);
                    //注意，这里需要很多步骤
                    //首先，captured是指纹，所以需要拿着指纹从服务器获得数据
                    //然后，需要将获得的数据分别组装成数据表和weekformat
                    //newListDAO.register(new DataNormalizer().dataNormalize(captured), name, remark, );
                    finish();
                    Toast.makeText(this,getText(R.string.scan_succeed),Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ScanActivity.this);
                    dialog.setTitle(getString(R.string.error_title));
                    dialog.setMessage(getString(R.string.error_content));
                    dialog.setCancelable(true);
                    dialog.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
                }
            }
        } else {
            super.onActivityResult(requestCode,resultCode,data);
        }
    }

    private List<List<Map<String, String>>> register(String input, String name) {
        List<List<Map<String, String>>> carrier = new ArrayList<>();
        List<Map<String, String>> mon = new ArrayList<>();
        List<Map<String, String>> tue = new ArrayList<>();
        List<Map<String, String>> wed = new ArrayList<>();
        List<Map<String, String>> thu = new ArrayList<>();
        List<Map<String, String>> fri = new ArrayList<>();
        List<Map<String, String>> sat = new ArrayList<>();
        List<Map<String, String>> sun = new ArrayList<>();
            String[] first = input.split("!");
            for (int i = 0; i < 7; i++) {
                if (!first[i].equals("_")) {
                    String[] second = first[i].split("#");
                    for (int j = 0; j < second.length; j++) {
                        String[] third = second[j].split("@");
                        Map<String, String> map = new HashMap<>();
                        map.put("startime", third[0]);
                        map.put("weeks", third[1]);
                        map.put("class", third[2]);
                        map.put("location", third[3]);
                        map.put("type", third[4]);
                        map.put("endtime", third[5]);
                        map.put("code", third[6]);
                        map.put("leader", third[7]);

                        if (i == 0) {
                            mon.add(map);
                        } else if (i == 1) {
                            tue.add(map);
                        } else if (i == 2) {
                            wed.add(map);
                        } else if (i == 3) {
                            thu.add(map);
                        } else if (i == 4) {
                            fri.add(map);
                        } else if (i == 5) {
                            sat.add(map);
                        } else if (i == 6) {
                            sun.add(map);
                        }
                    }
                }

                carrier.add(mon);
                carrier.add(tue);
                carrier.add(wed);
                carrier.add(thu);
                carrier.add(fri);
                carrier.add(sat);
                carrier.add(sun);

            }

            return carrier;
    }

}
