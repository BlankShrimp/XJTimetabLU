package com.blankshrimp.xjtimetablu.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.blankshrimp.xjtimetablu.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Flabbergast13 on 18/2/25.
 */

public class DatabaseOperater {

    public DatabaseOperater() {
    }

    public List<List<Map<String, String>>> getDataForWeekly(String id, Context context) {
        List<List<Map<String, String>>> result = new ArrayList<>();

        ListDBH listDBH = new ListDBH(context, "list.db", null, 1);
        SQLiteDatabase getFileName = listDBH.getWritableDatabase();
        Cursor getFileNameCursor = getFileName.query("timetable", new String[]{"weekformat"}, "account=?", new String[]{id}, null, null, null);

        getFileNameCursor.moveToFirst();
        String[] weekFormat = getFileNameCursor.getString(getFileNameCursor.getColumnIndex("weekformat")).split("-");
        getFileNameCursor.close();
        getFileName.close();

        id = addDash(id);
        id += ".db";
        TableDBH tableDBH = new TableDBH(context, id, null, 1);
        SQLiteDatabase getTableData = tableDBH.getWritableDatabase();
        Cursor getTableDataCursor = getTableData.query("timetable", null, null, null, null, null, null, null);
        int currentDay = 0;
        List<Map<String, String>> list = new ArrayList<>();
        if (getTableDataCursor.moveToFirst()) {
            do {
                Map<String, String> map = new HashMap<>();
                map.put("code", getTableDataCursor.getString(getTableDataCursor.getColumnIndex("modulecode")));
                map.put("leader", getTableDataCursor.getString(getTableDataCursor.getColumnIndex("moduleleader")));
                map.put("weeks", getTableDataCursor.getString(getTableDataCursor.getColumnIndex("week")));
                map.put("startime", String.valueOf(getTableDataCursor.getInt(getTableDataCursor.getColumnIndex("startime"))));
                map.put("endtime", String.valueOf(getTableDataCursor.getInt(getTableDataCursor.getColumnIndex("endtime"))));
                map.put("location", getTableDataCursor.getString(getTableDataCursor.getColumnIndex("location")));
                map.put("type", getTableDataCursor.getString(getTableDataCursor.getColumnIndex("moduletype")));
                map.put("class", getTableDataCursor.getString(getTableDataCursor.getColumnIndex("class")));

                int today = Integer.parseInt(getTableDataCursor.getString(getTableDataCursor.getColumnIndex("dayoftheweek")));
                int actualDayOfTheWeek = Integer.parseInt(weekFormat[today]);
                if (actualDayOfTheWeek == currentDay) {
                    list.add(map);
                } else {
                    if (list == null) {
                        result.add(list);
                    }
                    result.add(list);
                    list = new ArrayList<>();
                    list.add(map);
                    currentDay++;
                }

            } while (getTableDataCursor.moveToNext());
        }
        result.add(list);
        while (7> result.size()) {
            list = new ArrayList<>();
            result.add(list);
        }
        getTableDataCursor.close();
        getTableData.close();

        return result;
    }

    public List<List<Map<String, String>>> getDataForFull(String id, Context context) {
        String size = id;
        List<List<Map<String, String>>> result = new ArrayList<>();
        id = addDash(id);
        id += ".db";
        TableDBH tableDBH = new TableDBH(context, id, null, 1);
        SQLiteDatabase getTableData = tableDBH.getWritableDatabase();
        Cursor getTableDataCursor = getTableData.query("timetable", null, null, null, null, null, null, null);
        int currentDay = 0;
        List<Map<String, String>> list = new ArrayList<>();
        if (getTableDataCursor.moveToFirst()) {
            do {
                Map<String, String> map = new HashMap<>();
                map.put("code", getTableDataCursor.getString(getTableDataCursor.getColumnIndex("modulecode")));
                map.put("leader", getTableDataCursor.getString(getTableDataCursor.getColumnIndex("moduleleader")));
                map.put("weeks", getTableDataCursor.getString(getTableDataCursor.getColumnIndex("week")));
                map.put("startime", String.valueOf(getTableDataCursor.getInt(getTableDataCursor.getColumnIndex("startime"))));
                map.put("endtime", String.valueOf(getTableDataCursor.getInt(getTableDataCursor.getColumnIndex("endtime"))));
                map.put("location", getTableDataCursor.getString(getTableDataCursor.getColumnIndex("location")));
                map.put("type", getTableDataCursor.getString(getTableDataCursor.getColumnIndex("moduletype")));
                map.put("class", getTableDataCursor.getString(getTableDataCursor.getColumnIndex("class")));

                int today = Integer.parseInt(getTableDataCursor.getString(getTableDataCursor.getColumnIndex("dayoftheweek")));
                if (today == currentDay) {
                    list.add(map);
                } else {
                    if (list == null) {
                        result.add(list);
                    }
                    result.add(list);
                    list = new ArrayList<>();
                    list.add(map);
                    currentDay++;
                }
            } while (getTableDataCursor.moveToNext());
        }
        result.add(list);
        while (getWeekLengthList(size, context).size()> result.size()) {
            list = new ArrayList<>();
            result.add(list);
        }
        getTableDataCursor.close();
        getTableData.close();

        return result;
    }

    public void register(Context context, List<List<Map<String, String>>> content, String id, String remark, List<Integer> weekFormat) {
        String weekFormatString = new String();
        for (int i = 0; i < weekFormat.size(); i++)
            weekFormatString += "" + weekFormat.get(i) + "-";
        ListDBH listDBH = new ListDBH(context, "list.db", null, 1);
        SQLiteDatabase writeToList = listDBH.getWritableDatabase();
        ContentValues listValues = new ContentValues();
        listValues.put("account", id);
        listValues.put("favour", 2);
        listValues.put("prime", 2);
        listValues.put("remark", remark);
        listValues.put("weekformat", weekFormatString);
        writeToList.insert("timetable", null, listValues);
        writeToList.close();

        id = addDash(id);
        id += ".db";
        TableDBH tableDBH = new TableDBH(context, id, null, 1);
        SQLiteDatabase writeToTable = tableDBH.getWritableDatabase();
        for (int wk = 0; wk < content.size(); wk++) {
            for (int clas = 0; clas < content.get(wk).size(); clas++) {
                Map<String, String> map = content.get(wk).get(clas);
                ContentValues classValues = new ContentValues();
                classValues.put("dayoftheweek", wk);
                classValues.put("startime", Integer.parseInt(map.get("startime")));
                classValues.put("endtime", Integer.parseInt(map.get("endtime")));
                classValues.put("modulecode", map.get("code"));
                classValues.put("moduletype", map.get("type"));
                classValues.put("class", map.get("class"));
                classValues.put("moduleleader", map.get("leader"));
                classValues.put("location", map.get("location"));
                classValues.put("week", map.get("weeks"));
                writeToTable.insert("timetable", null, classValues);
            }
        }
        writeToTable.close();
    }

    public void delete(Context context, String id) {
        ListDBH listDBH = new ListDBH(context, "list.db", null, 1);
        SQLiteDatabase db = listDBH.getWritableDatabase();
        db.delete("timetable", "account = ?", new String[] {id});
        id = addDash(id);
        id += ".db";
        context.deleteDatabase(id);
        Toast.makeText(context, context.getString(R.string.deleteSucceed), Toast.LENGTH_SHORT);
    }

    private List<Integer> getWeekLengthList(String id, Context context) {
        ListDBH listDBH = new ListDBH(context, "list.db", null, 1);
        SQLiteDatabase getFileName = listDBH.getWritableDatabase();
        Cursor getFileNameCursor = getFileName.query("timetable", new String[]{"weekformat"}, "account=?", new String[]{id}, null, null, null);

        getFileNameCursor.moveToFirst();
        String[] weekFormat = getFileNameCursor.getString(getFileNameCursor.getColumnIndex("weekformat")).split("-");
        getFileNameCursor.close();
        getFileName.close();
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < weekFormat.length; i++) {
            list.add(Integer.parseInt(weekFormat[i]));
        }
        return list;
    }

    public int[] getWeekLength(String id, Context context) {
        ListDBH listDBH = new ListDBH(context, "list.db", null, 1);
        SQLiteDatabase getFileName = listDBH.getWritableDatabase();
        Cursor getFileNameCursor = getFileName.query("timetable", new String[]{"weekformat"}, "account=?", new String[]{id}, null, null, null);

        getFileNameCursor.moveToFirst();
        String[] weekFormat = getFileNameCursor.getString(getFileNameCursor.getColumnIndex("weekformat")).split("-");
        getFileNameCursor.close();
        getFileName.close();
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < weekFormat.length; i++) {
            list.add(Integer.parseInt(weekFormat[i]));
        }

        int[] result = new int[7];
        int current = 0;
        int sum = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == current) {
                sum ++;
            } else {
                result[current] = sum;
                sum = 1;
                current ++;
            }
        }
        result[current] = sum;

        return result;
    }

    private String addDash(String id) {
        id = id.replaceAll("\\W", "_");
        return id;
    }
}
