package com.citizen.fmc;

import android.os.StrictMode;
import androidx.multidex.MultiDexApplication;


import com.citizen.fmc.utils.ConnectivityReceiver;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;

//import io.fabric.sdk.android.Fabric;


/**
 * ======> Created by dheeraj-gangwar on 2018-01-03 <======
 */

public class UserApplication extends MultiDexApplication {

    private static UserApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        //Allowing Strict mode policy for Nougat support
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

//        Fabric.with(mInstance, new Crashlytics());

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplicationContext());

        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                .setResizeAndRotateEnabledForNetwork(true)
                .setDownsampleEnabled(true)
                .build();

        Fresco.initialize(mInstance, config);

//        Utils.setDefaultFont(this, "DEFAULT", "font/alegreya_regular.ttf");
//        Utils.setDefaultFont(this, "MONOSPACE", "font/alegreya_regular.ttf");
//        Utils.setDefaultFont(this, "SERIF", "font/alegreya_regular.ttf");
//        Utils.setDefaultFont(this, "SANS_SERIF", "font/alegreya_regular.ttf");
//        Utils.overrideFont(getApplicationContext(), "SERIF", "font/alegreya_regular.ttf");
    }

    public static synchronized UserApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
