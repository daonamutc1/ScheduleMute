package com.daonam.schedulemute.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ScheduleData extends SQLiteOpenHelper {
    public static String DBNAME = "DBNAME";
    public static String TABLENAME = "TABLENAME";
    public static ContentValues contentValues;
    public static SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    public ScheduleData(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryCreaTable = "CREATE TABLE DBNAME ( " +
                "TABLENAME Text )";
        db.execSQL(queryCreaTable);
    }

    public void createSchedule() {
        sqLiteDatabase = getWritableDatabase();
        contentValues = new ContentValues();
        /**
         * 1: 6h unMute, 23h mute
         * 2: 6h unMute, 8h mute
         * 2: 17h unMute, 23h mute
         * 3: 6h unMute, 8h mute
         * 3: 17h unMute, 23h mute
         * 4: 6h unMute, 8h mute
         * 4: 17h unMute, 23h mute
         * 5: 6h unMute, 8h mute
         * 5: 17h unMute, 23h mute
         * 6: 6h unMute, 8h mute
         * 6: 17h unMute, 23h mute
         * 7: 6h unMute, 8h mute
         * 7: 13h unMute, 23h mute
         */
        List<ScheduleOfDay> scheduleOfDays = new ArrayList<ScheduleOfDay>();
        List<Action> actionSunday = new ArrayList<Action>();
        actionSunday.add(new Action(false, 6));
        actionSunday.add(new Action(true, 23));

        List<Action> actionMonToFri = new ArrayList<Action>();
        actionMonToFri.add(new Action(false, 6));
        actionMonToFri.add(new Action(true, 8));
        actionMonToFri.add(new Action(false, 17));
        actionMonToFri.add(new Action(true, 23));

        List<Action> actionSaturday = new ArrayList<Action>();
        actionSaturday.add(new Action(false, 6));
        actionSaturday.add(new Action(true, 8));
        actionSaturday.add(new Action(false, 12));
        actionSaturday.add(new Action(true, 23));


        scheduleOfDays.add(new ScheduleOfDay(1, actionSunday));
        scheduleOfDays.add(new ScheduleOfDay(2, actionMonToFri));
        scheduleOfDays.add(new ScheduleOfDay(3, actionMonToFri));
        scheduleOfDays.add(new ScheduleOfDay(4, actionMonToFri));
        scheduleOfDays.add(new ScheduleOfDay(5, actionMonToFri));
        scheduleOfDays.add(new ScheduleOfDay(6, actionMonToFri));
        scheduleOfDays.add(new ScheduleOfDay(7, actionSaturday));

        Gson gson = new Gson();
        String inputString = gson.toJson(scheduleOfDays);
        contentValues = new ContentValues();
        contentValues.put(TABLENAME, inputString);
        sqLiteDatabase.insert(DBNAME, null, contentValues);
        closeDB();
    }

    public List<ScheduleOfDay> getListSchedule() {
        List<ScheduleOfDay> scheduleObjectList = new ArrayList<ScheduleOfDay>();
        sqLiteDatabase = getReadableDatabase();
        try {
            cursor = sqLiteDatabase.query(false, DBNAME, null, null, null
                    , null, null, null, null);
            Gson gson = new Gson();
            Type type = new TypeToken<List<ScheduleOfDay>>() {
            }.getType();
            while (cursor.moveToNext()) {
                String outputarray = cursor.getString(cursor.getColumnIndex(TABLENAME));
                List<ScheduleOfDay> finalOutputString = gson.fromJson(outputarray, type);
                scheduleObjectList.addAll(finalOutputString);
            }

        } catch (Exception e) {
            Log.d("Namlog", "Exception= " + e);
        }
        closeDB();
        return scheduleObjectList;
    }


    private void closeDB() {
        if (sqLiteDatabase != null) sqLiteDatabase.close();
        if (contentValues != null) contentValues.clear();
        if (cursor != null) cursor.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
