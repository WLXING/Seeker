package com.example.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.example.seeker.MainActivity;
import com.example.seeker.R;

import java.io.File;

public class AlarmService extends Service {
    private static final String TAG = "AlarmService";
    private PowerManager.WakeLock wakeLock;
    private String title;
    private String previewContent;
    private int noteId;
    private String username;
    private String currentUser;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        title = bundle.getString("title");
        previewContent = bundle.getString("previewContent");
        noteId = bundle.getInt("id");
        username = bundle.getString("username");

        SendANotification();
        WakeUp();

        return super.onStartCommand(intent, flags, startId);

    }

    private void WakeUp() {
        //屏幕唤醒
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "SimpleTimer");
        wakeLock.acquire(1000);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void SendANotification() {
        //发送通知
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String id = "channel_01";
        CharSequence name = getString(R.string.chanel_name);
        String description = getString(R.string.chanel_description);
        NotificationChannel notificationChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(notificationChannel);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(this, id)
                .setContentTitle(title)
                .setContentText("主人，您有一项待办事项哦~" + previewContent)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon_user_edit_profile)
                //.setSound()//声音提醒
                .setVibrate(new long[]{0, 1000, 1000, 1000})//手机振动
                .setLights(Color.GREEN, 1000, 1000)//灯闪
                .setContentIntent(pendingIntent)//点击跳转到主界面
                .setAutoCancel(true)//会自动取消
                .build();
        notificationManager.notify(noteId, notification);
//
////                //播放音乐
//        mp = MediaPlayer.create(this, R.raw.beyond_bzyy);
//        try {
//            mp.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mp.start();
//        File file = new File(Environment.getExternalStorageDirectory(),"beyond_bzyy.mp3");
//        try {
//            mp.setDataSource(file.getPath());
//            mp.prepare();
//            mp.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }

    @Override
    public void onDestroy() {
        // wakeLock.release();

        super.onDestroy();
    }


}
