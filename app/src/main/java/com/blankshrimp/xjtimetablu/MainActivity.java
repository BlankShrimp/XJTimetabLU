package com.blankshrimp.xjtimetablu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankshrimp.xjtimetablu.util.DatabaseOperater;
import com.blankshrimp.xjtimetablu.util.ListDBH;
import com.blankshrimp.xjtimetablu.util.QRCodeUtil;
import com.blankshrimp.xjtimetablu.widget.FullFragment;
import com.blankshrimp.xjtimetablu.widget.ListFragment;
import com.blankshrimp.xjtimetablu.widget.NoneFavFragment;
import com.blankshrimp.xjtimetablu.widget.NonePrimaryFragment;
import com.blankshrimp.xjtimetablu.widget.NoneScheduleFrag;
import com.blankshrimp.xjtimetablu.widget.TableFragment;

import org.joda.time.DateTime;
import org.joda.time.Weeks;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean weeklyTable = true;
    private Menu mMenu;
    private String currentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (checkPrimaryExist(MainActivity.this)) {
            Bundle bundle = new Bundle();
            bundle.putString("name", myTable(MainActivity.this));
            TableFragment tableView = new TableFragment();
            tableView.setArguments(bundle);
            replaceFragment(tableView);
        } else {
            NonePrimaryFragment nonePrimaryFragment = new NonePrimaryFragment();
            replaceFragment(nonePrimaryFragment);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(navigationView.getMenu().getItem(0).getItemId());
        setTitle(this.getString(R.string.PrimaryTable) + getWeeks());
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.view_weekly) {
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

        } else if (id == R.id.addByLogin) {
            Intent intent = new Intent("com.example.activitytest.ACTION_START");
            intent.addCategory("com.LOGIN_ACTIVITY");
            startActivity(intent);

        } else if (id == R.id.generate_qr) {
            final Dialog dialog = new Dialog(MainActivity.this, R.style.edit_AlertDialog_style);
            dialog.setContentView(R.layout.qr_code_image);
            ImageView imageView = (ImageView) dialog.findViewById(R.id.qrCodeImageView);
            Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(originDataForQRCode(new DatabaseOperater().getDataForFull(currentName, MainActivity.this)), 1000, 1000);
            imageView.setImageBitmap(bitmap);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.dismissQR), Toast.LENGTH_SHORT).show();
        } else if (id == R.id.addByScan) {
            Intent intent = new Intent("com.example.activitytest.ACTION_START");
            intent.addCategory("com.SCAN_ACTIVITY");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_primary) {
            setTitle(this.getString(R.string.PrimaryTable) + getWeeks());
            if (checkPrimaryExist(MainActivity.this)) {
                if (weeklyTable) {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", myTable(MainActivity.this));
                    TableFragment tableView = new TableFragment();
                    tableView.setArguments(bundle);
                    replaceFragment(tableView);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", myTable(MainActivity.this));
                    FullFragment tableView = new FullFragment();
                    tableView.setArguments(bundle);
                    replaceFragment(tableView);
                }
                mMenu.clear();
                getMenuInflater().inflate(R.menu.main, mMenu);
            } else {
                NonePrimaryFragment nonePrimaryFragment = new NonePrimaryFragment();
                replaceFragment(nonePrimaryFragment);
                mMenu.clear();
            }

        } else if (id == R.id.nav_special) {
            setTitle(this.getString(R.string.SpecialConcern) + getWeeks());
            if (checkFavExist(MainActivity.this)) {
                if (weeklyTable) {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", favTable(MainActivity.this));
                    TableFragment tableView = new TableFragment();
                    tableView.setArguments(bundle);
                    replaceFragment(tableView);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", favTable(MainActivity.this));
                    FullFragment tableView = new FullFragment();
                    tableView.setArguments(bundle);
                    replaceFragment(tableView);

                }
                mMenu.clear();
                getMenuInflater().inflate(R.menu.main, mMenu);
            } else {
                NoneFavFragment noneFavFragment = new NoneFavFragment();
                replaceFragment(noneFavFragment);
                mMenu.clear();
            }

        } else if (id == R.id.nav_tablelist) {
            setTitle(R.string.TableList);
            ListFragment tableListFragment = new ListFragment();
            replaceFragment(tableListFragment);
            mMenu.clear();
            getMenuInflater().inflate(R.menu.table_list_menu, mMenu);

        } else if (id == R.id.nav_schedule) {
            setTitle(R.string.Schedule);
            NoneScheduleFrag tableView = new NoneScheduleFrag();
            replaceFragment(tableView);
            mMenu.clear();

        } else if (id == R.id.nav_settings) {
            //setTitle(R.string.settings);

        } else if (id == R.id.nav_about) {
            //setTitle(R.string.about);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean checkPrimaryExist(Context context) {
        ListDBH listDBH = new ListDBH(context, "list.db", null, 1);
        SQLiteDatabase db = listDBH.getWritableDatabase();
        Cursor cursor = db.query("timetable", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (1 == cursor.getInt(cursor.getColumnIndex("prime")))
                    return true;
            } while (cursor.moveToNext());
        }
        cursor.close();

        return false;
    }

    private boolean checkFavExist(Context context) {
        ListDBH listDBH = new ListDBH(context, "list.db", null, 1);
        SQLiteDatabase db = listDBH.getWritableDatabase();
        Cursor cursor = db.query("timetable", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (1 == cursor.getInt(cursor.getColumnIndex("favour")))
                    return true;
            } while (cursor.moveToNext());
        }
        cursor.close();

        return false;
    }

    private String myTable(Context context) {
        String name = new String();
        ListDBH listDBH = new ListDBH(context, "list.db", null, 1);
        SQLiteDatabase db = listDBH.getWritableDatabase();
        Cursor cursor = db.query("timetable", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (1 == cursor.getInt(cursor.getColumnIndex("prime")))
                    name = cursor.getString(cursor.getColumnIndex("account"));
            } while (cursor.moveToNext());
        }
        cursor.close();

        currentName = name;
        return name;
    }

    private String favTable(Context context) {
        String name = new String();
        ListDBH listDBH = new ListDBH(context, "list.db", null, 1);
        SQLiteDatabase db = listDBH.getWritableDatabase();
        Cursor cursor = db.query("timetable", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (1 == cursor.getInt(cursor.getColumnIndex("favour")))
                    name = cursor.getString(cursor.getColumnIndex("account"));
            } while (cursor.moveToNext());
        }
        cursor.close();

        currentName = name;
        return name;
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.body_container, fragment);
        transaction.commit();

    }

    public String getWeeks() {
        DateTime dateTime = new DateTime();
        DateTime dt = new DateTime(2018, 2, 19, 0, 0, 0, 0);
        dateTime = dateTime.dayOfWeek().withMinimumValue();
        String weeks = "" + Weeks.weeksBetween(dt, dateTime).getWeeks();
        String results = null;

        DateTime now = new DateTime();
        DateTime wh = new DateTime(2018, 2, 26, 0, 0, 0, 0);
        DateTime sh = new DateTime(2018, 6, 4, 0, 0, 0, 0);
        if (now.isBefore(wh))
            results = " " + MainActivity.this.getString(R.string.winterHoliday);
        else if (now.isAfter(sh))
            results = " " + MainActivity.this.getString(R.string.summerHoliday);
        else {
            String locale = Locale.getDefault().getLanguage();
            if (locale.equals("zh")) {
                results = " " + MainActivity.this.getString(R.string.di) + weeks + MainActivity.this.getString(R.string.zhou);
            } else {
                results = " " + MainActivity.this.getString(R.string.week) + " " + weeks;
            }
        }

        return results;
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
