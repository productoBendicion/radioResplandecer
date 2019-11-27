package com.radio.resplandecer.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.radio.resplandecer.R;
import com.radio.resplandecer.mediaPlayer.SimplePlayer;
import com.radio.resplandecer.screens.home.HomeActivity;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class ForegroundService extends Service {

    public static final String CHANNEL_ID = "vdeeForegroundChannel";
    public final static String START_STOP_KEY = "startStopKey";
    public final static String START_SERVICE_FLAG = "startService";
    public final static String STOP_SERVICE_FLAG = "stopService";

    public SimplePlayer simplePlayer;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Intent playIntent = new Intent(this, ForegroundService.class);
        playIntent.setAction(Constants.ACTION.ACTION_PLAY);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent stopIntent = new Intent(this, ForegroundService.class);
        stopIntent.setAction(Constants.ACTION.ACTION_STOP);
        PendingIntent pendingStopIntent = PendingIntent.getService(this, 0,
                stopIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Estas Escuchando")
                .setContentText("Radio Resplandecer")
                .setSmallIcon(R.drawable.resplandecer_logo_small_icon)
                .addAction(R.drawable.play_icon, "Play", pendingPlayIntent)
                .addAction(R.drawable.stop_icon, "Stop", pendingStopIntent)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_MAX)
                .build();

            startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        simplePlayer = SimplePlayer.getSimplePlayer();

        String action = intent.getAction();

        if (action == null) {
            action = "";
        }

        if (action.equals(Constants.ACTION.STOP_SERVICE)){
            stopForeground(true);
            stopSelf();
        } else if (action.equals(Constants.ACTION.ACTION_PLAY)) {
            if (simplePlayer != null && !simplePlayer.isInitialized()) {
                simplePlayer.initPlayer();
            }
        } else if (action.equals(Constants.ACTION.ACTION_STOP)) {
            if (simplePlayer != null && simplePlayer.isInitialized()) {
                simplePlayer.releasePlayer();
            }

            stopForeground(true);
            stopSelf();

        } else {
            Log.d("ForegroundSErvice", "empty");
        }


        return START_NOT_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
