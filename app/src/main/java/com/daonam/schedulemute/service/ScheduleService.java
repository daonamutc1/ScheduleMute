package com.daonam.schedulemute.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.daonam.schedulemute.data.Action;
import com.daonam.schedulemute.data.ScheduleData;
import com.daonam.schedulemute.data.ScheduleOfDay;
import com.daonam.schedulemute.receiver.ScheduleReceiver;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScheduleService extends Service {
    private static final String TAG = "Namlog";
    private static final String channelID = "ChannelID";
    ScheduleData sqlHelper;
    List<ScheduleOfDay> scheduleObjectList = new ArrayList<ScheduleOfDay>();
    int currentDayOfWeek, currentHour, nextHour = -1, nextDay = 0;
    boolean isMute = true;

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, new Notification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sqlHelper = new ScheduleData(this);
        Calendar calendar = Calendar.getInstance();
        currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        nextDay = currentDayOfWeek;
        currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        scheduleObjectList.addAll(sqlHelper.getListSchedule());

        ScheduleOfDay scheduleObject = scheduleObjectList.get(currentDayOfWeek - 1);

        List<Action> actions = scheduleObject.getActions();
        doBackgroundWork(this, actions);

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
        setUpBrocasReceiver(nextHour, nextDay);

        Log.d(TAG, "onStartCommand run all");
        return START_STICKY;
    }

    private void setUpBrocasReceiver(int nextHour, int nextDay) {
        Intent myIntent = new Intent(this, ScheduleReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();

        Log.d(TAG, "nextHour= " + nextHour);
        Log.d(TAG, "nextDay= " + nextDay);
        calendar.set(Calendar.DAY_OF_WEEK, nextDay);
        calendar.set(Calendar.HOUR_OF_DAY, nextHour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopSelf();
    }

    private void doBackgroundWork(Context context, List<Action> actions) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runService(context, actions);
            }
        }).start();
    }

    private void runService(Context context, List<Action> actions) {
        for (int j = 0; j < actions.size(); j++) {
            if (currentHour == actions.get(j).getStart()) {
                isMute = actions.get(j).isMute();
                break;
            }
        }
        if (isMute) {
            mutePhone(context);
        } else {
            unMute(context);
        }

    }

    private void mutePhone(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_VIBRATE) {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
    }

    private void unMute(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
        }
    }
}
