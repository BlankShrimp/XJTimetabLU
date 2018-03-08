package com.blankshrimp.xjtimetablu.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Flabbergast13 on 18/2/25.
 */

public class ListDBH extends SQLiteOpenHelper {


    //type 0 stands for account, type 1 stands for QR code
    //favour 1 stands for favourite, 2 stands for others
    //primary 1 stands for primary, 2 stands for others
    public static final String CREATE_TABLE = "create table timetable (" +
            "id integer primary key autoincrement, " +
            "account text, " +
            "favour integer, " +
            "prime integer, " +
            "remark text, " +
            "weekformat text)";

    private Context mContext;

    public ListDBH(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
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
