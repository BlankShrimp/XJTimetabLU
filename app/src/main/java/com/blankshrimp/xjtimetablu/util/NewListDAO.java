package com.blankshrimp.xjtimetablu.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * Created by Flabbergast13 on 18/9/3.
 */

public class NewListDAO {

    private Context context;
    private NewListDBH newListDBH;

    public NewListDAO(Context context) {
        this.context = context;
        newListDBH = new NewListDBH(context);
    }

    //Operation on timetable table
    void insertItem(String fingerprint, String content, String weekformat, String name, String remark) {
        SQLiteDatabase db = newListDBH.getWritableDatabase();
        db.beginTransaction();

        ContentValues contentValues = new ContentValues();
        contentValues.put("fingerprint", fingerprint);
        contentValues.put("content", content);
        contentValues.put("weekformat", weekformat);
        contentValues.put("name", name);
        contentValues.put("remark", remark);
        db.insert("timetable", null, contentValues);
        db.close();
    }

    private void dropItem(String fingerprint) {
        SQLiteDatabase db = newListDBH.getWritableDatabase();
        db.delete("timetable", "fingerprint=?", new String[] {fingerprint});
        db.close();
    }

    String queryWeekformat(String fingerprint) {
        String result = new String();
        SQLiteDatabase db = newListDBH.getReadableDatabase();
        Cursor cursor = db.query("timetable", new String[]{"weekformat"}, "fingerprint=?", new String[] {fingerprint}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                result = cursor.getString(cursor.getColumnIndex("weekformat"));
            } while (cursor.moveToNext());
        }
        db.close();
        return result;
    }

    public String queryContent(String fingerprint) {
        String result = new String();
        SQLiteDatabase db = newListDBH.getReadableDatabase();
        Cursor cursor = db.query("timetable", new String[]{"content"}, "fingerprint=?", new String[] {fingerprint}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                result = cursor.getString(cursor.getColumnIndex("content"));
            } while (cursor.moveToNext());
        }
        db.close();
        return result;
    }

    public String queryName(String fingerprint) {
        String result = new String();
        SQLiteDatabase db = newListDBH.getReadableDatabase();
        Cursor cursor = db.query("timetable", new String[]{"name"}, "fingerprint=?", new String[] {fingerprint}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                result = cursor.getString(cursor.getColumnIndex("name"));
            } while (cursor.moveToNext());
        }
        db.close();
        return result;
    }

    public String queryRemark(String fingerprint) {
        String result = new String();
        SQLiteDatabase db = newListDBH.getReadableDatabase();
        Cursor cursor = db.query("timetable", new String[]{"remark"}, "fingerprint=?", new String[] {fingerprint}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                result = cursor.getString(cursor.getColumnIndex("remark"));
            } while (cursor.moveToNext());
        }
        db.close();
        return result;
    }

    public List<People> queryListOfPeople() {
        SQLiteDatabase db = newListDBH.getReadableDatabase();
        Cursor cursor = db.query("timetable", null, null, null, null, null,null);
        List<People> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String Name = cursor.getString(cursor.getColumnIndex("account"));
                String remark = cursor.getString(cursor.getColumnIndex("remark"));

                People people = new People(Name, remark);
                list.add(people);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public String returnFingerprint(int id) {
        String result = new String();
        SQLiteDatabase db = newListDBH.getReadableDatabase();
        Cursor cursor = db.query("timetable", new String[]{"fingerprint"}, null, null, null, null, null);
        cursor.moveToPosition(id);
        result = cursor.getString(cursor.getColumnIndex("fingerprint"));
        return result;
    }

    //Operation on each person's table
    void createNewTable(String fingerprint) {
        SQLiteDatabase db = newListDBH.getWritableDatabase();
        String sql = "create table " + fingerprint+ " (" +
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
        db.execSQL(sql);
        db.close();
    }

    private void dropTable(String fingerprint) {
        SQLiteDatabase db = newListDBH.getWritableDatabase();
        String sql = "drop table " + fingerprint;
        db.execSQL(sql);
        db.close();
    }

    public List<List<Map<String, String>>> queryLocalTable(String fingerprint) {
        List<List<Map<String, String>>> result = new ArrayList<>();
        String[] weekFormat = queryWeekformat(fingerprint).split("-");
        SQLiteDatabase db = newListDBH.getReadableDatabase();
        Cursor cursor = db.query(fingerprint, null, null, null, null, null, null, null);
        int currentDay = 0;
        List<Map<String, String>> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Map<String, String> map = new HashMap<>();
                map.put("code", cursor.getString(cursor.getColumnIndex("modulecode")));
                map.put("leader", cursor.getString(cursor.getColumnIndex("moduleleader")));
                map.put("weeks", cursor.getString(cursor.getColumnIndex("week")));
                map.put("startime", String.valueOf(cursor.getInt(cursor.getColumnIndex("startime"))));
                map.put("endtime", String.valueOf(cursor.getInt(cursor.getColumnIndex("endtime"))));
                map.put("location", cursor.getString(cursor.getColumnIndex("location")));
                map.put("type", cursor.getString(cursor.getColumnIndex("moduletype")));
                map.put("class", cursor.getString(cursor.getColumnIndex("class")));

                int today = Integer.parseInt(cursor.getString(cursor.getColumnIndex("dayoftheweek")));
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

            } while (cursor.moveToNext());
        }
        result.add(list);
        while (7> result.size()) {
            list = new ArrayList<>();
            result.add(list);
        }
        cursor.close();
        db.close();
        return result;
    }

    public List<List<Map<String, String>>> queryGeneralTable(String fingerprint) {
        List<List<Map<String, String>>> result = new ArrayList<>();
        SQLiteDatabase db = newListDBH.getReadableDatabase();
        Cursor cursor = db.query(fingerprint, null, null, null, null, null, null, null);
        int currentDay = 0;
        List<Map<String, String>> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Map<String, String> map = new HashMap<>();
                map.put("code", cursor.getString(cursor.getColumnIndex("modulecode")));
                map.put("leader", cursor.getString(cursor.getColumnIndex("moduleleader")));
                map.put("weeks", cursor.getString(cursor.getColumnIndex("week")));
                map.put("startime", String.valueOf(cursor.getInt(cursor.getColumnIndex("startime"))));
                map.put("endtime", String.valueOf(cursor.getInt(cursor.getColumnIndex("endtime"))));
                map.put("location", cursor.getString(cursor.getColumnIndex("location")));
                map.put("type", cursor.getString(cursor.getColumnIndex("moduletype")));
                map.put("class", cursor.getString(cursor.getColumnIndex("class")));

                int today = Integer.parseInt(cursor.getString(cursor.getColumnIndex("dayoftheweek")));
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
            } while (cursor.moveToNext());
        }
        result.add(list);

        String[] weekFormat = queryWeekformat(fingerprint).split("-");
        while (weekFormat.length> result.size()) {
            list = new ArrayList<>();
            result.add(list);
        }
        cursor.close();
        db.close();
        return result;
    }

    private String plainContent(List<List<Map<String, String>>> input) {
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

    public int[] getWeekLength(String id, Context context) {
        String[] weekFormat = queryWeekformat(id).split("-");
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

    //Func exposed to activities
    public void register(List<List<Map<String, String>>> content, String name, String remark, List<Integer> weekFormat) {

        String tokenString = System.currentTimeMillis() + "de" + new Random().nextInt(99999);
        String fingerprint = "";
        try {
            MessageDigest mDigest = MessageDigest.getInstance("md5");
            byte[] md5 = mDigest.digest(tokenString.getBytes());
            fingerprint = android.util.Base64.encodeToString(md5, android.util.Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        String weekFormatString = new String();
        for (int i = 0; i < weekFormat.size(); i++)
            weekFormatString += "" + weekFormat.get(i) + "-";
        insertItem(fingerprint, plainContent(content), weekFormatString, name, remark);

        createNewTable(fingerprint);
        SQLiteDatabase db = newListDBH.getWritableDatabase();
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
                db.insert(fingerprint, null, classValues);
            }
        }
        db.close();

    }

    public void deleteRecord(String fingerprint) {
        dropTable(fingerprint);
        dropItem(fingerprint);
    }
}