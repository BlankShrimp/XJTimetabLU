package com.blankshrimp.xjtimetablu;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankshrimp.xjtimetablu.util.DatabaseOperater;
import com.blankshrimp.xjtimetablu.util.QRCodeUtil;
import com.blankshrimp.xjtimetablu.widget.FullFragment;
import com.blankshrimp.xjtimetablu.widget.TableFragment;

import java.util.List;
import java.util.Map;

public class OthersTableViewer extends AppCompatActivity {

    private Menu mMenu;
    private boolean weeklyTable = true;
    private String currentName = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_table_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.loginToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        TableFragment table_view_fragment = new TableFragment();
        table_view_fragment.setArguments(bundle);
        currentName = bundle.getString("name").toString();
        setTitle(currentName);
        replaceFragment(table_view_fragment);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.view_weekly).setChecked(weeklyTable);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //重写ToolBar返回按钮的行为，防止重新打开父Activity重走生命周期方法
            case android.R.id.home:
                finish();
                return true;
            case R.id.view_weekly:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    weeklyTable = true;
                    Bundle bundle = new Bundle();
                    bundle.putString("name", currentName);
                    TableFragment tableView = new TableFragment();
                    tableView.setArguments(bundle);
                    replaceFragment(tableView);
                } else {
                    item.setChecked(false);
                    weeklyTable = false;
                    Bundle bundle = new Bundle();
                    bundle.putString("name", currentName);
                    FullFragment tableView = new FullFragment();
                    tableView.setArguments(bundle);
                    replaceFragment(tableView);
                }
                return true;
            case R.id.generate_qr:
                final Dialog dialog = new Dialog(OthersTableViewer.this, R.style.edit_AlertDialog_style);
                dialog.setContentView(R.layout.qr_code_image);
                ImageView imageView = (ImageView) dialog.findViewById(R.id.qrCodeImageView);
                Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(originDataForQRCode(new DatabaseOperater().getDataForFull(currentName, OthersTableViewer.this)), 960, 960);
                imageView.setImageBitmap(bitmap);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                Toast.makeText(OthersTableViewer.this, OthersTableViewer.this.getString(R.string.dismissQR), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.others_content, fragment);
        transaction.commit();

    }

    private String originDataForQRCode (List<List<Map<String, String>>> input) {
        String result = new String();
        for (int i = 0; i < input.size(); i++) {
            if (input.get(i).size() == 0) {
                result += "_";
            } else {
                for (int j = 0; j < input.get(i).size(); j++) {
                    Map<String, String> map = input.get(i).get(j);
                    result += map.get("startime") + "@" + map.get("weeks") + "@" + map.get("class")
                            + "@" + map.get("location") + "@" + map.get("type") + "@" +
                            map.get("endtime") + "@" + map.get("code") + "@" + map.get("leader") + "#";
                }
            }
            result += "!";
        }

        return result;
    }
}
