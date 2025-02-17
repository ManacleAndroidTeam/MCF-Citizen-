package com.citizen.fmc.service;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.citizen.fmc.utils.Constants;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * ======> Created by dheeraj-gangwar on 2018-03-12 <======
 */

public class NotificationFirebaseInstanceIDService extends FirebaseMessagingService {
    private static final String TAG = NotificationFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        String refreshedToken = String.valueOf(FirebaseMessaging.getInstance().getToken());

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);
        registrationComplete.putExtra(Constants.FIREBASE_TOKEN, refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.FIREBASE_TOKEN, token);
        editor.apply();
    }
}