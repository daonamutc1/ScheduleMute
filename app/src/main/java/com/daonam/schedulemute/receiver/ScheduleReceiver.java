package com.daonam.schedulemute.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.daonam.schedulemute.service.ScheduleService;

public class ScheduleReceiver extends BroadcastReceiver {
    private static final String TAG = "Namlog";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        Intent myInten = new Intent(context, ScheduleService.class);
        context.startForegroundService(myInten);
    }
}

//        2021-03-06 16:48:17.635 20636-20636/com.daonam.schedulemute D/Namlog: onReceive:
//        2021-03-06 16:48:17.645 20636-20636/com.daonam.schedulemute D/Namlog: onCreate
//        2021-03-06 16:48:17.646 20636-20636/com.daonam.schedulemute D/Namlog: onStartCommand
//        2021-03-06 16:48:17.650 20636-20636/com.daonam.schedulemute E/Namlog: min= 63
//        2021-03-06 16:48:23.194 20636-20636/com.daonam.schedulemute D/Namlog: onDestroy
//        2021-03-06 17:03:52.692 20636-20636/com.daonam.schedulemute D/Namlog: onReceive:
//        Không gọi lại được service