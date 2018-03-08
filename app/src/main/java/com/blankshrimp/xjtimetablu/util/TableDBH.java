package com.blankshrimp.xjtimetablu.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Flabbergast13 on 18/2/25.
 */

public class TableDBH extends SQLiteOpenHelper {

    public static final String CREATE_TABLE = "create table timetable (" +
            "id integer primary key autoincrement, " +
            "dayoftheweek integer, " +
            "startime integer, " +
            "endtime integer, " +
            "modulecode text, " +
            "moduletype text, " +
            "class text, " +
            "moduleleader text, " +
            "location text, " +
            "week text)";

    private Context mContext;

    public TableDBH (Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {

    }
}