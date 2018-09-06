package com.blankshrimp.xjtimetablu.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Flabbergast13 on 18/9/3.
 */

public class NewListDBH extends SQLiteOpenHelper {

    private static final String CREATE_DB = "create table timetable (" +
            "fingerprint text primary key, " +
            "content text, " +
            "weekformat text, " +
            "name text, " +
            "remark text)";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "list.db";

    public NewListDBH(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }
}
