package com.citizen.fmc.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import androidx.core.app.NotificationCompat;

import android.os.Build;
import android.text.Html;
import android.util.Log;

import com.citizen.fmc.R;
import com.citizen.fmc.activity.UserNotificationActivity;
import com.citizen.fmc.database.DBHelper;
import com.citizen.fmc.utils.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class UserNotificationService extends FirebaseMessagingService {
    private static final String TAG = UserNotificationService.class.getSimpleName();
    private String messageDateTimeStamp;
    private String messageTitle;
    private String messageBody;
    private String messageImagePath;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private NotificationManager mNotificationManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                String messageId = remoteMessage.getMessageId();
                String messageFrom = remoteMessage.getFrom();
                messageDateTimeStamp = dateFormat.format(Calendar.getInstance().getTime());
                messageTitle = remoteMessage.getNotification().getTitle();
                messageBody = remoteMessage.getNotification().getBody();
                messageImagePath = remoteMessage.getNotification().getIcon();
                messageImagePath = messageImagePath != null ? Constants.IMAGE_PUBLIC_URL + messageImagePath : "";

                Log.e(TAG,
                        "MessageId: " + messageId
                                + "\nMessageFrom: " + messageFrom
                                + "\nMessageDateTime: " + messageDateTimeStamp
                                + "\nMessageTitle: " + messageTitle
                                + "\nMessageBody: " + messageBody
                                + "\nMessageImagePath: " + messageImagePath);

                int insertStatus = new DBHelper(UserNotificationService.this)
                        .insertNotificationData(messageId, messageFrom,
                                messageDateTimeStamp, messageTitle,
                                messageBody, messageImagePath);
                Log.v(TAG, "INSERT_STATUS: " + (insertStatus > 0 ?
                        "Data inserted successfully !" : Constants.MESSAGE_SOMETHING_WENT_WRONG));

                handleNotification();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat
                .Builder(UserNotificationService.this, Constants.NOTIFICATION_CHANNEL_ID);

      //  Intent intent = new Intent(UserNotificationService.this, SlidingDrawerActivity.class);
        Intent intent = new Intent(UserNotificationService.this, UserNotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.S)
        {
            resultPendingIntent =     PendingIntent.getActivity(UserNotificationService.this,
                    PendingIntent.FLAG_IMMUTABLE,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

        }
        else {
            resultPendingIntent =    PendingIntent.getActivity(UserNotificationService.this,
                    0,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

        }

        if (messageImagePath.isEmpty() || messageImagePath.equalsIgnoreCase("na")) {
            showSmallNotification(mBuilder, messageTitle, messageBody, messageDateTimeStamp, resultPendingIntent);
        } else {
            showBigNotification(getBitmapFromURL(messageImagePath),
                    mBuilder,
                    messageTitle, messageBody, messageDateTimeStamp, resultPendingIntent);
        }
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder,
                                       String title, String message, String timeStamp,
                                       PendingIntent resultPendingIntent) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);

        //Check for OREO above version:-
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            Notification notification;
            notification = mBuilder
                    .setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setStyle(inboxStyle)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(R.drawable.municipal_corporation_faridabad_logo_256x256)
                    //.setLargeIcon(bitmap)
                    .setContentText(message)
                    .build();
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(notificationChannel);
            mNotificationManager.notify(1, notification);
        }else {
            Notification notification;
            notification = mBuilder
                    .setTicker(title)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setStyle(inboxStyle)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(R.drawable.municipal_corporation_faridabad_logo_256x256)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_ndmc_logo_large))
                    .setContentText(message)
                    .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(Constants.NOTIFICATION_ID, notification);
            }
        }


    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder,
                                     String title, String message,
                                     String timeStamp, PendingIntent resultPendingIntent) {
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        //bigPictureStyle.bigPicture(bitmap);

        //Check For OREO version and above:-
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            Notification notification;
            notification = mBuilder
                    .setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setStyle(bigPictureStyle)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(R.drawable.municipal_corporation_faridabad_logo_256x256)
                    //.setLargeIcon(bitmap)
                    .setContentText(message)
                    .build();
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(notificationChannel);
            mNotificationManager.notify(1, notification);

        }else {
            Notification notification;
            notification = mBuilder
                    .setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setStyle(bigPictureStyle)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(R.drawable.municipal_corporation_faridabad_logo_256x256)
                    //.setLargeIcon(bitmap)
                    .setContentText(message)
                    .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(Constants.NOTIFICATION_ID_BIG_IMAGE, notification);
            }
        }
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

//    private void handleDataMessage(JSONObject json) {
//        Log.e(TAG, "push json: " + json.toString());
//
//        try {
//            JSONObject data = json.getJSONObject("data");
//
//            String title = data.getString("title");
//            String message = data.getString("message");
////            boolean isBackground = data.getBoolean("is_background");
////            String imageUrl = data.getString("image");
////            String timestamp = data.getString("timestamp");
////            JSONObject payload = data.getJSONObject("payload");
//
////            Log.e(TAG, "title: " + title);
//            Log.e(TAG, "message: " + message);
////            Log.e(TAG, "isBackground: " + isBackground);
////            Log.e(TAG, "payload: " + payload.toString());
////            Log.e(TAG, "imageUrl: " + imageUrl);
////            Log.e(TAG, "timestamp: " + timestamp);
//
//
//            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
//                // app is in foreground, broadcast the push message
//                Intent pushNotification = new Intent(Constants.PUSH_NOTIFICATION);
//                pushNotification.putExtra("message", message);
//                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
//
//                // play notification sound
//                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
//                notificationUtils.playNotificationSound();
//            } else {
//                // app is in background, show the notification in notification tray
//                Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
//                resultIntent.putExtra("message", message);
//
//                // check for image attachment
////                if (TextUtils.isEmpty(imageUrl)) {
////                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
////                } else {
////                    // image is present, show notification with image
////                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
////                }
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, "Json Exception: " + e.getMessage());
//        } catch (Exception e) {
//            Log.e(TAG, "Exception: " + e.getMessage());
//        }
//    }

    /**
     * Showing notification with text only
     */
//    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
//        notificationUtils = new NotificationUtils(context);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
//    }

    /**
     * Showing notification with text and image
     */
//    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
//        notificationUtils = new NotificationUtils(context);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
//    }
}