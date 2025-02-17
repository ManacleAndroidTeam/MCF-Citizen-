package com.citizen.fmc.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.citizen.fmc.R;
import com.citizen.fmc.utils.ApiInterface;
import com.citizen.fmc.utils.ConnectivityReceiver;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.crashlytics.android.Crashlytics;
import com.google.gson.JsonObject;

import java.util.Locale;

//import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ======> Created by dheeraj-gangwar on 2017-12-21 <======
 */

public class SplashActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
    private CoordinatorLayout coordinatorLayout;
    ApiInterface mApiService;
    private String appdownload;
    SharedPreferences data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
//        Fabric.with(this, new Crashlytics());
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        data = getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        appdownload = data.getString(Constants.APP_DOWNLOAD,"");
       // mApiService = ApiClient.getClient().create(ApiInterface.class);
        mApiService = Utils.getInterfaceService();

        if(!appdownload.equals("1"))
            getAppDownload();

        try {
            if (connectivityReceiver != null) {
                unregisterReceiver(connectivityReceiver);
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(connectivityReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Log.e(TAG, Utils.printKeyHash(SplashActivity.this));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Utils.getUserToken(SplashActivity.this) != null) {
            String language_code =  Constants.getPrefrence(SplashActivity.this,"locale");
            if (language_code.equals("") || language_code==null)
            {
                delaySplash(SlidingDrawerActivity.class);
            }
            else
            {
                setLocale(language_code);
            }

        } else {
            delaySplash(LoginActivity.class);
        }

    }

    /**
     * ====================== Method for creating delay on splash screen ======================
     */
    private void delaySplash(final Class aClass) {
        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            @Override
            public void run() {
                // This method will be executed once the timer is over
                startActivity(new Intent(SplashActivity.this, aClass));
                overridePendingTransition(R.anim.animation_enter_from_right,
                                          R.anim.animation_leave_out_to_left);
                // close this activity
                finish();
            }
        }, Constants.SPLASH_TIME_OUT_MS);
    }

    /**=================================================================================================*/


    //  For app download
    //  for officer app_type = 1;
    //  for citizen app_type = 2;

    private void getAppDownload() {
        mApiService.getDownloadCount(Constants.getDeviceId(SplashActivity.this),
                "2", Constants.getVersion(SplashActivity.this)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response != null) {
                    //  progressDialog.dismiss();
                    Constants.setPreferenceStringData(SplashActivity.this,Constants.APP_DOWNLOAD,"1");
                    JsonObject mObject = response.body();
                    if (mObject.get("response").getAsBoolean()) {
                        // JsonArray jsonArray = mObject.get("data").getAsJsonArray();
                        /*if (jsonArray.size() > 0) {
                            for (int x = 0; x < jsonArray.size(); x++) {
                                dbHelper.insertAppModuleData(jsonArray.get(x).getAsJsonObject().get("id").getAsInt(),
                                        jsonArray.get(x).getAsJsonObject().get("title_name").getAsString(),
                                        ApiClient.BASE_URL_IMAGE_PUBLIC +
                                                jsonArray.get(x).getAsJsonObject().get("icon_unicode").getAsString());
                            }

                            progressDialog.dismiss();
                        }*/
                    } else {
                        //   progressDialog.dismiss();
                        //   refreshDataDialog();
                        Constants.showAlert(coordinatorLayout, SplashActivity.this,
                                true, Constants.TITLE_SORRY,
                                Constants.MESSAGE_TRY_AGAIN_LATER);
                    }
                } else {
                    // progressDialog.dismiss();
                    //  refreshDataDialog();
                    Constants.showAlert(coordinatorLayout, SplashActivity.this,
                            true, Constants.TITLE_SORRY,
                            Constants.MESSAGE_TRY_AGAIN_LATER);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
                //  progressDialog.dismiss();
                //  refreshDataDialog();
                t.printStackTrace();
                Constants.showAlert(coordinatorLayout, SplashActivity.this,
                        true, Constants.TITLE_ALERT, Constants.failureMessage(t));
            }
        });
    }
    public void setLocale( String language_code) {
//        Resources res =  this.getResources();
        // Change locale settings in the app.
//        DisplayMetrics dm = res.getDisplayMetrics();
//        android.content.res.Configuration conf = res.getConfiguration();
//        conf.setLocale(new Locale(language_code.toLowerCase())); // API 17+ only.
// Use conf.locale = new Locale(...) if targeting lower versions
//        res.updateConfiguration(conf, dm);
        Locale myLocale = new Locale(language_code);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, SlidingDrawerActivity.class);
        overridePendingTransition(R.anim.animation_enter_from_right,
                R.anim.animation_leave_out_to_left);
        finish();
        startActivity(refresh);

    }
}
