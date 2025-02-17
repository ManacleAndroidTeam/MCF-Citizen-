package com.citizen.fmc.service;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


import com.citizen.fmc.R;
import com.citizen.fmc.UserApplication;
import com.citizen.fmc.activity.SlidingDrawerActivity;

import java.util.Objects;
import java.util.Random;

public class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String title = intent.getStringExtra("inputTitle");
        String message = intent.getStringExtra("inputMessage");
        //Send Notification
        sendNotification(message, title, 0);
        //do heavy work on a background thread


        //stopSelf();

        return START_NOT_STICKY;
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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void sendNotification(String messageBody, String title, int zoneType) {
        try {
            Random r = new Random();
            final int NOTIFICATION_ID = 101;//r.nextInt(10000);
            Intent intent = new Intent(this, SlidingDrawerActivity.class);

            intent.putExtra("isComingFromNotification", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(SlidingDrawerActivity.class);
            stackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent;
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.S)
            {
                pendingIntent= stackBuilder.getPendingIntent(FLAG_IMMUTABLE, PendingIntent.FLAG_UPDATE_CURRENT);

            }
            else {
                pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            }

            String channelId = Objects.requireNonNull(this).getString(R.string.default_notification_channel_id);
            //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            Uri defaultSoundUri = Uri.parse("android.resource://" + UserApplication.getInstance().getPackageName() + "/" + R.raw.siren_noise);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    //.setContent(contentView)
                    .setVibrate(new long[]{1000, 1000})
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setSound(defaultSoundUri)
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    AudioAttributes.Builder attrs = new AudioAttributes.Builder();
                    attrs.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC);
                    attrs.setUsage(AudioAttributes.USAGE_MEDIA);
                    //Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel notificationChannel = new NotificationChannel(channelId, "Channel human readable title", importance);
                    notificationChannel.enableLights(true);
                    notificationChannel.setLightColor(Color.BLUE);
                    notificationChannel.enableVibration(true);
                    notificationChannel.setSound(defaultSoundUri, attrs.build());
                    //notificationChannel.setShowBadge(true);
                    notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                    notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));
                    notificationBuilder.setChannelId(channelId);
                    Objects.requireNonNull(notificationManager).createNotificationChannel(notificationChannel);
                } catch (Exception ex) {
                    ex.getMessage();
                }
            }

            Objects.requireNonNull(notificationManager).notify(NOTIFICATION_ID, notificationBuilder.build());

            startForeground(1, notificationBuilder.build());
        } catch (Exception e) {
            e.getMessage();
        }
    }
}