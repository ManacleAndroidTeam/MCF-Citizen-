package com.citizen.fmc.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.citizen.fmc.UserApplication;


/**
 * ======> Created by dheeraj-gangwar on 2018-01-24 <======
 */

public class ConnectivityReceiver extends BroadcastReceiver {
    public static ConnectivityReceiverListener connectivityReceiverListener;
    private String TAG = getClass().getSimpleName();

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        try {
            if (arg1.getAction() != null && arg1.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Log.d(TAG, "Network Receiver invoked...");

                ConnectivityManager cm = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = null;
                if (cm != null) {
                    activeNetwork = cm.getActiveNetworkInfo();
                }
                boolean isConnected = activeNetwork != null
                        && activeNetwork.isConnectedOrConnecting();

                if (connectivityReceiverListener != null) {
                    connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) UserApplication.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }


    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}