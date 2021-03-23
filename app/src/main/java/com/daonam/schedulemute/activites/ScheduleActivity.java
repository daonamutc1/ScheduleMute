package com.daonam.schedulemute.activites;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.daonam.schedulemute.R;
import com.daonam.schedulemute.data.Action;
import com.daonam.schedulemute.data.ScheduleData;
import com.daonam.schedulemute.data.ScheduleOfDay;
import com.daonam.schedulemute.receiver.ScheduleReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {
    private String TAG = "Namlog";
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Calendar calendar;
    ScheduleData sqlHelper;
    List<ScheduleOfDay> scheduleObjectList = new ArrayList<ScheduleOfDay>();

    /**
     * Lịch làm việc: t2 đến thứ 6 làm 8h đến 17h. Thứ 7 từ 8h đến 12h
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sqlHelper = new ScheduleData(this);
        scheduleObjectList.addAll(sqlHelper.getListSchedule());
        if (scheduleObjectList.size() == 0) {
            createShedule();
            scheduleObjectList.addAll(sqlHelper.getListSchedule());
        }
        startReceiver();
    }

    private void createShedule() {
        sqlHelper = new ScheduleData(this);
        sqlHelper.createSchedule();
    }

    private void startReceiver() {
        calendar = Calendar.getInstance();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        int nextHour = -1;
        int nextDay = currentDayOfWeek;

        ScheduleOfDay scheduleObject = scheduleObjectList.get(currentDayOfWeek - 1);
        List<Action> actions = scheduleObject.getActions();
        for (int j = 0; j < actions.size(); j++) {
            if (currentHour < actions.get(j).getStart()) {
                nextHour = actions.get(j).getStart();
                break;
            }
        }
        if (nextHour == -1) {
            if (currentDayOfWeek == 7) {
                nextHour = scheduleObjectList.get(0).getActions().get(0).getStart();
                nextDay = 1;
            } else {
                nextDay = nextDay + 1;
                nextHour = scheduleObjectList.get(currentDayOfWeek).getActions().get(0).getStart();
            }
        }

        Log.d(TAG, "nextHour= " + nextHour);
        Log.d(TAG, "nextDay= " + nextDay);
        calendar.set(Calendar.DAY_OF_WEEK, nextDay);
        calendar.set(Calendar.HOUR_OF_DAY, nextHour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Intent intent = new Intent(this, ScheduleReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(ScheduleActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }
}