package com.blankshrimp.xjtimetablu.widget;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.blankshrimp.xjtimetablu.OthersTableViewer;
import com.blankshrimp.xjtimetablu.R;
import com.blankshrimp.xjtimetablu.util.ListDBH;
import com.blankshrimp.xjtimetablu.util.NewListDAO;
import com.blankshrimp.xjtimetablu.util.People;
import com.blankshrimp.xjtimetablu.util.PeopleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Flabbergast13 on 18/2/26.
 */

public class ListFragment extends Fragment {

    private PeopleAdapter mPeopleAdapter;
    private ListView mListView;
    private View xxView;
    private List<People> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        xxView = view;

        //read data from list.db and assemble data into people object
        ListDBH listDBH = new ListDBH(view.getContext(), "list.db", null, 1);
        SQLiteDatabase db = listDBH.getWritableDatabase();
        Cursor cursor = db.query("timetable", null, null, null, null, null,null);
        list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String Name = cursor.getString(cursor.getColumnIndex("account"));
                String remark = cursor.getString(cursor.getColumnIndex("remark"));

                People people = new People(Name, remark);
                list.add(people);
            } while (cursor.moveToNext());
        }
        cursor.close();

        //set an Adapter and apply it to listview
        mPeopleAdapter = new PeopleAdapter(view.getContext(), R.layout.list_person, list);
        mListView = (ListView) view.findViewById(R.id.people_listlist);
        mListView.setAdapter(mPeopleAdapter);
        mListView.setEmptyView(view.findViewById(R.id.listGoneImg));
        mListView.setEmptyView(view.findViewById(R.id.listGoneText1));
        mListView.setEmptyView(view.findViewById(R.id.listGoneText2));
        registerForContextMenu(mListView);

        //start activity when click on list item
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                People people = list.get(i);
                Bundle bundle = new Bundle();
                bundle.putBoolean("table_selected", true);
                bundle.putString("name", people.getName());
                Intent intent = new Intent(view.getContext(), OthersTableViewer.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }

    /**
     * refresh the fragment after adding timetables
     */
    @Override
    public void onResume() {
        super.onResume();
        flash();
    }

    @Override
    public void onCreateContextMenu (ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.add(0, 0, Menu.NONE,view.getContext().getString(R.string.setAsFavour));
        menu.add(0, 1, Menu.NONE,view.getContext().getString(R.string.setAsPrimary));
        menu.add(0, 2, Menu.NONE,view.getContext().getString(R.string.delete));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (item.getItemId() == 0) {
            //favourite
            int target = menuInfo.position;
            ListDBH listDBH = new ListDBH(xxView.getContext(), "list.db", null, 1);
            SQLiteDatabase db = listDBH.getWritableDatabase();
            ContentValues values = new ContentValues();
            Cursor cursor = db.query("timetable", null, null, null, null, null,null);
            String targetName = new String();
            int match = 0;
            if (cursor.moveToFirst()) {
                do {
                    if (target == match) {
                        targetName = cursor.getString(cursor.getColumnIndex("account"));

                    }
                    match++;
                } while (cursor.moveToNext());
            }
            cursor.close();
            values.put("favour", 2);
            db.update("timetable", values, "favour = ?", new String[] {"1"});
            values.put("favour", 1);
            db.update("timetable", values, "account = ?", new String[] {targetName});
            db.close();

        } else if (item.getItemId() == 1) {
            //primary
            int target = menuInfo.position;
            ListDBH listDBH = new ListDBH(xxView.getContext(), "list.db", null, 1);
            SQLiteDatabase db = listDBH.getWritableDatabase();
            ContentValues values = new ContentValues();
            Cursor cursor = db.query("timetable", null, null, null, null, null,null);
            String targetName = new String();
            int match = 0;
            if (cursor.moveToFirst()) {
                do {
                    if (target == match) {
                        targetName = cursor.getString(cursor.getColumnIndex("account"));

                    }
                    match++;
                } while (cursor.moveToNext());
            }
            cursor.close();
            values.put("prime", 2);
            db.update("timetable", values, "prime = ?", new String[] {"1"});
            values.put("prime", 1);
            db.update("timetable", values, "account = ?", new String[] {targetName});
            db.close();

        } else if (item.getItemId() == 2) {
            //delete
            int target = menuInfo.position;
            ListDBH listDBH = new ListDBH(xxView.getContext(), "list.db", null, 1);
            SQLiteDatabase db = listDBH.getWritableDatabase();
            Cursor cursor = db.query("timetable", null, null, null, null, null,null);
            String targetName = new String();
            int match = 0;
            if (cursor.moveToFirst()) {
                do {
                    if (target == match) {
                        targetName = cursor.getString(cursor.getColumnIndex("account"));
                    }
                    match++;
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            new NewListDAO(xxView.getContext()).deleteRecord(targetName);
            flash();
        }
        return super.onContextItemSelected(item);
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void flash () {
        //just reloading
        ListDBH listDBH = new ListDBH(xxView.getContext(), "list.db", null, 1);
        SQLiteDatabase db = listDBH.getWritableDatabase();
        Cursor cursor = db.query("timetable", null, null, null, null, null,null);
        list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String Name = cursor.getString(cursor.getColumnIndex("account"));
                String remark = cursor.getString(cursor.getColumnIndex("remark"));

                People people = new People(Name, remark);
                list.add(people);
            } while (cursor.moveToNext());
        }
        cursor.close();

        mPeopleAdapter = new PeopleAdapter(xxView.getContext(), R.layout.list_person, list);
        mListView = (ListView) xxView.findViewById(R.id.people_listlist);
        mListView.setAdapter(mPeopleAdapter);
        mListView.setEmptyView(xxView.findViewById(R.id.listGoneImg));
        mListView.setEmptyView(xxView.findViewById(R.id.listGoneText1));
        mListView.setEmptyView(xxView.findViewById(R.id.listGoneText2));
    }
}
