package com.example.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;

import com.example.seeker.MainActivity;
import com.example.utils.L;

import java.util.Calendar;

/**
 * Created by ${WLX} on 2019/12/2.
 */

public class Day_Night_Service extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Calendar calendar = Calendar.getInstance();
        //HOUR_OF_DAY is used for the 24-hour clock.
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        L.e("服务里", "hour----------->" + hour);
        if (hour > 6 && hour < 20) {//7点到19点算白天
            // 如果主界面不是日间模式则发通知提示更改
            if (MainActivity.currentNightMode != Configuration.UI_MODE_NIGHT_NO) {
                Intent intentDay = new Intent("com.example.seeker.DAY_NIGHT_CHANGE");
                intentDay.addCategory("com.example.seeker.DAY_NIGHT_CHANGE");
                intentDay.putExtra("day_or_night", "day");
                sendBroadcast(intentDay);
            }
        } else {
            //如果主界面不是夜间模式则发通知提示更改
            if (MainActivity.currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
                Intent intentNight = new Intent("com.example.seeker.DAY_NIGHT_CHANGE");
                intentNight.addCategory("com.example.seeker.DAY_NIGHT_CHANGE");
                intentNight.putExtra("day_or_night", "night");
                sendBroadcast(intentNight);

            }
        }
        //定时器每隔一个小时判断是否需要变换日夜模式
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int mHour = 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + mHour;
        Intent intent1 = new Intent(this, Day_Night_Service.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent1, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }
}
