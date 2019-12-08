package com.example.broadcast;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.seeker.R;
import com.example.utils.L;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by ${WLX} on 2019/8/16.
 */

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {
        //屏幕唤醒
        Toast.makeText(context,"收到广播了",Toast.LENGTH_SHORT).show();
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");
        wakeLock.acquire(1000);
        //发送通知
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        String id = "channel_01";
        CharSequence name = context.getString(R.string.chanel_name);
        String description = context.getString(R.string.chanel_description);
        NotificationChannel notificationChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(notificationChannel);
        Notification notification = new NotificationCompat.Builder(context,id)
                .setContentTitle("this is content title")
                .setContentText("this is content text")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.nav_location)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(1,notification);
    }
}
