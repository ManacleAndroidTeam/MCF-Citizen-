package com.citizen.fmc.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.citizen.fmc.R;
 import com.citizen.fmc.utils.ApiInterface;
import com.citizen.fmc.utils.ConnectivityReceiver;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
 import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DandDChooseTypeFragment extends Fragment implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private View mView;
    private final String TAG = getClass().getSimpleName();
    private Activity activity;
    private AppBarLayout appBarLayout;
    private SpotsDialog spotsDialog;
    TextView epc_code_tv;
    TextView address_tv;
    LinearLayout seggragate_ll;
    LinearLayout non_seggragate_ll;
    private int userId;
    LinearLayout locked_ll;
    LinearLayout feedback_ll;
    private String PAGE_TYPE = "";
    private String statusTitle;
    private Location location;
    private GoogleApiClient mGoogleApiClient;
    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 2;
    final static int REQUEST_LOCATION = 101;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    double userLat=0.0;
    double userLong=0.0;
    String lat="0.0";
    String lng="0.0";
    String order_id="";
    String value="";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (this.getArguments() != null) {
            /**
             */
            PAGE_TYPE = this.getArguments().getString(Constants.KEY_COMPLAINT_PAGE_TYPE);
            statusTitle = this.getArguments().getString(Constants.KEY_STATUS_TITLE);


        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_d_and_d_selecttype, container, false);
        activity = getActivity();
        appBarLayout = mView.findViewById(R.id.app_bar_layout);
        epc_code_tv=mView.findViewById(R.id.epc_code_tv);
        seggragate_ll=mView.findViewById(R.id.seggragate_ll);
        non_seggragate_ll=mView.findViewById(R.id.non_seggragate_ll);
        locked_ll=mView.findViewById(R.id.locked_ll);
        feedback_ll=mView.findViewById(R.id.feedback_ll);
        address_tv=mView.findViewById(R.id.address_tv);
        Utils.setToolbarTitle(activity, "Waste Type Collected");

        userId = Utils.getUserDetails(activity).getCivilianId();

        spotsDialog = new SpotsDialog(activity, getResources().getString(R.string.please_wait_dialog_text), R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);
            value = getArguments().getString("result1");
        value=value.replace("/","");
//        getLatlngFRomEPCCode(value.trim());
        epc_code_tv.setText(value);

        if ((ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                TedPermission.create()
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                startLocationUpdates();
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {
                                getFragmentManager().popBackStackImmediate();
                            }

                           /* @Override
                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                getFragmentManager().popBackStackImmediate();
                            }*/

                        })
                        .setGotoSettingButtonText("SETTINGS")
                        .setDeniedMessage("If you reject permission,you can not use this service" +
                                "\n\nPlease turn on permissions at [Settings] > [Permission]")
                        .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.CAMERA)
                        .check();
            } else {
                TedPermission.create()
                        .setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                startLocationUpdates();
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {
                                getFragmentManager().popBackStackImmediate();
                            }

                     /*
                      @Override
                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                getFragmentManager().popBackStackImmediate();
                            }*/

                        })
                        .setGotoSettingButtonText("SETTINGS")
                        .setDeniedMessage("If you reject permission,you can not use this service" +
                                "\n\nPlease turn on permissions at [Settings] > [Permission]")
                        .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }

        } else {
            startLocationUpdates();
        }

        LinearLayout complaint_ll=mView.findViewById(R.id.complaint_ll);

        complaint_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DandDSelectComplaintTypeFragment ldf = new DandDSelectComplaintTypeFragment ();
                Bundle args = new Bundle();
                args.putString("result1", value);
                ldf.setArguments(args);

//Inflate the fragment
                getFragmentManager().beginTransaction().replace(R.id.main_sliding_container, ldf).addToBackStack("").commit();
            }
        });

        feedback_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DandDFeedbackFragment ldf = new DandDFeedbackFragment ();
                Bundle args = new Bundle();
                args.putString("result1", value);
                ldf.setArguments(args);

//Inflate the fragment
                getFragmentManager().beginTransaction().replace(R.id.main_sliding_container, ldf).addToBackStack("").commit();
            }
        });

        locked_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityReceiver.isConnected()) {
                    spotsDialog.show();

                    ApiInterface mApiInterface = Utils.getInterfaceService();

                        Call<JsonObject> mService = mApiInterface.submitWastage(
                                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userId)),
                                RequestBody.create(MediaType.parse("multipart/form-data"), epc_code_tv.getText().toString().trim()),
                                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Constants.APP_TYPE)),
                                RequestBody.create(MediaType.parse("multipart/form-data"), "3"),
                                RequestBody.create(MediaType.parse("multipart/form-data"), ""),
                                RequestBody.create(MediaType.parse("multipart/form-data"), ""),
                                RequestBody.create(MediaType.parse("multipart/form-data"), ""),
                                RequestBody.create(MediaType.parse("multipart/form-data"), ""),

                                MultipartBody.Part.createFormData("image_source",""));

                        mService.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                try {
                                    if (response.body() != null) {
                                        JsonObject jsonObject = response.body();
                                        if (jsonObject.get("response").getAsBoolean()) {
                                            spotsDialog.dismiss();
                                            order_id=jsonObject.get("order_id").getAsString();

                                            int is_mobile_available=jsonObject.get("mobile_data_status").getAsInt();
                                            if(is_mobile_available==1) {
                                                showCheckMobileDialog("Message","1");
                                            }else {
                                                Constants.customToast(getActivity(), "Data submitted successfully", 1);
                                                showDialog("House Locked");
                                            }
                                        } else {
                                            spotsDialog.dismiss();
                                            String message = jsonObject.get("message").getAsString().trim();
                                            Constants.customToast(getActivity(),message,0);
                                        }
                                    } else {
                                        spotsDialog.dismiss();
                                        Constants.customToast(getActivity(),Constants.MESSAGE_SOMETHING_WENT_WRONG,1);

                                    }
                                } catch (Exception e) {
                                    spotsDialog.dismiss();
                                    e.printStackTrace();
                                    Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                spotsDialog.dismiss();
                                call.cancel();
                                t.printStackTrace();
                                Utils.customToast(activity, Utils.failureMessage(t), 0);

                            }
                        });


                }
                else{

                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                            Constants.MESSAGE_CHECK_INTERNET, false);

                }

            }
        });

        seggragate_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityReceiver.isConnected()) {
                    spotsDialog.show();

                    ApiInterface mApiInterface = Utils.getInterfaceService();

                    Call<JsonObject> mService = mApiInterface.submitWastage(
                            RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userId)),
                            RequestBody.create(MediaType.parse("multipart/form-data"), epc_code_tv.getText().toString().trim()),
                            RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Constants.APP_TYPE)),
                            RequestBody.create(MediaType.parse("multipart/form-data"), "1"),
                            RequestBody.create(MediaType.parse("multipart/form-data"), ""),
                            RequestBody.create(MediaType.parse("multipart/form-data"), ""),
                            RequestBody.create(MediaType.parse("multipart/form-data"), ""),
                            RequestBody.create(MediaType.parse("multipart/form-data"), ""),

                            MultipartBody.Part.createFormData("image_source",""));



                    mService.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            try {
                                if (response.body() != null) {
                                    JsonObject jsonObject = response.body();
                                    if (jsonObject.get("response").getAsBoolean()) {
                                        spotsDialog.dismiss();

                                        order_id=jsonObject.get("order_id").getAsString();

                                        int is_mobile_available=jsonObject.get("mobile_data_status").getAsInt();
                                        if(is_mobile_available==1) {
                                            showCheckMobileDialog("Message","1");
                                        }else {


                                            Constants.customToast(getActivity(), "Data submitted successfully", 1);
                                            showDialog("Segragate");
                                        }
                                    } else {
                                        spotsDialog.dismiss();
                                        String message = jsonObject.get("message").getAsString().trim();
                                        Constants.customToast(getActivity(),message,0);
                                    }
                                } else {
                                    spotsDialog.dismiss();
                                    Constants.customToast(getActivity(),Constants.MESSAGE_SOMETHING_WENT_WRONG,1);

                                }
                            } catch (Exception e) {
                                spotsDialog.dismiss();
                                e.printStackTrace();
                                Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            spotsDialog.dismiss();
                            call.cancel();
                            t.printStackTrace();
                            Utils.customToast(activity, Utils.failureMessage(t), 0);

                        }
                    });


                }
                else{

                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                            Constants.MESSAGE_CHECK_INTERNET, false);

                }

            }
        });


        non_seggragate_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityReceiver.isConnected()) {
                    spotsDialog.show();
                         ApiInterface mApiInterface = Utils.getInterfaceService();

                        Call<JsonObject> mService = mApiInterface.submitWastage(
                                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userId)),
                                RequestBody.create(MediaType.parse("multipart/form-data"), epc_code_tv.getText().toString().trim()),
                                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Constants.APP_TYPE)),
                                RequestBody.create(MediaType.parse("multipart/form-data"), "2"),
                                RequestBody.create(MediaType.parse("multipart/form-data"), ""),
                                RequestBody.create(MediaType.parse("multipart/form-data"), ""),
                                RequestBody.create(MediaType.parse("multipart/form-data"), ""),
                                RequestBody.create(MediaType.parse("multipart/form-data"), ""),
                                MultipartBody.Part.createFormData("image_source",""));



                        mService.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                try {
                                    if (response.body() != null) {
                                        JsonObject jsonObject = response.body();
                                        if (jsonObject.get("response").getAsBoolean()) {
                                            spotsDialog.dismiss();
                                            order_id=jsonObject.get("order_id").getAsString();

                                            int is_mobile_available=jsonObject.get("mobile_data_status").getAsInt();
                                            if(is_mobile_available==1) {
                                                showCheckMobileDialog("Message","2");
                                            }else {


                                                Constants.customToast(getActivity(), "Data submitted successfully", 1);
                                                showDialog("Non-Segregate");
                                            }
                                        } else {
                                            spotsDialog.dismiss();
                                            String message = jsonObject.get("message").getAsString().trim();
                                            Constants.customToast(getActivity(),message,1);
                                        }
                                    } else {
                                        spotsDialog.dismiss();
                                        Constants.customToast(getActivity(),Constants.MESSAGE_SOMETHING_WENT_WRONG,1);

                                    }
                                } catch (Exception e) {
                                    spotsDialog.dismiss();
                                    e.printStackTrace();
                                    Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                spotsDialog.dismiss();
                                call.cancel();
                                t.printStackTrace();
                                Utils.customToast(activity, Utils.failureMessage(t), 0);

                            }
                        });


                }else{

                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                            Constants.MESSAGE_CHECK_INTERNET, false);

                }

            }
        });

        return mView;
    }
    private void showCheckMobileDialog(String text, final String status) {
        final Dialog dialog = new Dialog(activity, R.style.dialogSlideAnimation);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_check_mobile);
        dialog.setCancelable(false);

        TextView titleText = dialog.findViewById(R.id.dialog_title);
        final TextView dialogMessage = dialog.findViewById(R.id.dialog_title_message);
        TextView dialogInfoMessage = dialog.findViewById(R.id.dialog_info_message);
        ImageView titleIcon = dialog.findViewById(R.id.dialog_title_icon);
        Button cancelButton, OKButton;
        cancelButton = dialog.findViewById(R.id.cancel_button);
        OKButton = dialog.findViewById(R.id.ok_button);

        dialogInfoMessage.setVisibility(View.GONE);

        titleIcon.setImageResource(R.drawable.ic_information);
        titleText.setText(R.string.Information);
//        dialogMessage.setText(text+"!");
        dialogInfoMessage.setText(Html.fromHtml("Mobile No."));

        OKButton.setText(R.string.ok);
        cancelButton.setText(R.string.cancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getFragmentManager().popBackStackImmediate();
            }
        });

        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityReceiver.isConnected()) {
                    spotsDialog.show();
                    ApiInterface mApiInterface = Utils.getInterfaceService();
                    String mobile_no=dialogMessage.getText().toString();
                    if(mobile_no.isEmpty()||mobile_no.length()!=10){
                        Constants.customToast(activity,"Please fill correct mobile no.",2);
                    }else {

                        Call<JsonObject> mService = mApiInterface.submitMessageForNotificaton(
                                RequestBody.create(MediaType.parse("multipart/form-data"), epc_code_tv.getText().toString()),
                                RequestBody.create(MediaType.parse("multipart/form-data"),  order_id),
                                RequestBody.create(MediaType.parse("multipart/form-data"), mobile_no),
                                RequestBody.create(MediaType.parse("multipart/form-data"), status)
                        );


                        mService.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                try {
                                    if (response.body() != null) {
                                        JsonObject jsonObject = response.body();
                                        if (jsonObject.get("response").getAsBoolean()) {
                                            spotsDialog.dismiss();
                                            dialog.dismiss();
                                            showDialog("Message");


                                        } else {
                                            spotsDialog.dismiss();
                                            String message = jsonObject.get("message").getAsString().trim();
                                            Constants.customToast(getActivity(), message, 1);
                                        }
                                    } else {
                                        spotsDialog.dismiss();
                                        Constants.customToast(getActivity(), Constants.MESSAGE_SOMETHING_WENT_WRONG, 1);

                                    }
                                } catch (Exception e) {
                                    spotsDialog.dismiss();
                                    e.printStackTrace();
                                    Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                spotsDialog.dismiss();
                                call.cancel();
                                t.printStackTrace();
                                Utils.customToast(activity, Utils.failureMessage(t), 0);

                            }
                        });
                    }

                }else{

                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                            Constants.MESSAGE_CHECK_INTERNET, false);

                }

            }
        });
        dialog.show();

    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.v(TAG, "onDetach...");
    }
    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .addApi(LocationServices.API)
                    .enableAutoManage((FragmentActivity) activity, this)
                    .addConnectionCallbacks(this)
                    .useDefaultAccount()
                    .addOnConnectionFailedListener(this)
                    .build();

            mGoogleApiClient.connect();

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    // do work here
                    onLocationChanged(locationResult.getLastLocation());
                }
            };
        }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        // Create the location request to start receiving updates
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(activity);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        LocationServices.getFusedLocationProviderClient(activity)
                .requestLocationUpdates(mLocationRequest, mLocationCallback,
                        Looper.myLooper());
    }


    protected void stopLocationUpdates() {
        if (mLocationCallback != null) {
            LocationServices.getFusedLocationProviderClient(activity)
                    .removeLocationUpdates(mLocationCallback);
            Log.d(TAG, "Location update stopped ..............");
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        Log.d(TAG, "Firing onLocationChanged ..............");
        if (location != null) {
            userLat = location.getLatitude();
            userLong = location.getLongitude();
            getLatlngFRomEPCCode(epc_code_tv.getText().toString());
         //            latLng = new LatLng(userLat, userLong);
//            title = Utils.getGeoLocationAddress(activity, userLat, userLong);
        }

//        mapFragment.getMapAsync(this);
    }



    private void checkDistanceDialog() {
        final Dialog dialog = new Dialog(activity, R.style.dialogSlideAnimation);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_logout);
        dialog.setCancelable(false);

        if(dialog.isShowing()){
            dialog.dismiss();
        }

        TextView titleText = dialog.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialog.findViewById(R.id.dialog_title_message);
        TextView dialogInfoMessage = dialog.findViewById(R.id.dialog_info_message);
        ImageView titleIcon = dialog.findViewById(R.id.dialog_title_icon);
        Button cancelButton, OKButton;
        cancelButton = dialog.findViewById(R.id.cancel_button);
        OKButton = dialog.findViewById(R.id.ok_button);

        cancelButton.setVisibility(View.GONE);
        dialogInfoMessage.setVisibility(View.VISIBLE);

        titleIcon.setImageResource(R.drawable.ic_information);
        titleText.setText(R.string.Information);
        dialogMessage.setText("Not near House");
        dialogInfoMessage.setText(Html.fromHtml("Your distance is more than 100 metres from this  house."));

        OKButton.setText(R.string.ok);
        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getFragmentManager().popBackStackImmediate();
            }
        });

        dialog.show();

     }

    private void showDialog(String text) {
        final Dialog dialog = new Dialog(activity, R.style.dialogSlideAnimation);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_logout);
        dialog.setCancelable(false);

        TextView titleText = dialog.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialog.findViewById(R.id.dialog_title_message);
        TextView dialogInfoMessage = dialog.findViewById(R.id.dialog_info_message);
        ImageView titleIcon = dialog.findViewById(R.id.dialog_title_icon);
        Button cancelButton, OKButton;
        cancelButton = dialog.findViewById(R.id.cancel_button);
        OKButton = dialog.findViewById(R.id.ok_button);

        cancelButton.setVisibility(View.GONE);
        dialogInfoMessage.setVisibility(View.VISIBLE);

        titleIcon.setImageResource(R.drawable.ic_information);
        titleText.setText(R.string.Information);
        dialogMessage.setText(text);
        dialogInfoMessage.setText(Html.fromHtml("Thank you for clean FMC."));

        OKButton.setText(R.string.ok);
        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getFragmentManager().popBackStackImmediate();
            }
        });

        dialog.show();

    }




    public double getDistance(double lat1, double lng1,double lat2 , double lng2){

        lng1 = Math.toRadians(lng1);
        lng2 = Math.toRadians(lng2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double dlng = lng2 - lng1;
        double dlat = lat2 - lat1;

        double a = Math.pow(Math.sin(dlat/2),2)
                +Math.cos(lat1)*Math.cos(lat2)
                *Math.pow(Math.sin(dlng/2),2);

        double c = 2*Math.asin(Math.sqrt(a));
        double r =6371;
        return(c*r);



    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }

        Log.i(TAG, "GoogleApiClient connected");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
//                    final LocationSettingsStates = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(activity,
                                    REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.stopAutoManage((FragmentActivity) activity);
                mGoogleApiClient.disconnect();
            }
            stopLocationUpdates();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void getLatlngFRomEPCCode(String epc_code) {

        ApiInterface mApiInterface = Utils.getInterfaceService();
        String userToken = Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity);
        int userId = Utils.getUserDetails(activity).getCivilianId();
        Log.v(TAG, "USER_TOKEN: " + userToken);
        Log.v(TAG, "USER_ID: " + String.valueOf(userId));

        Call<JsonObject> mService = mApiInterface.getLatlngEPCCode(
                epc_code);
        mService.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (activity != null && isAdded()) {
                        if (response.body() != null) {
                            JsonObject jsonObject = response.body();

                            if (jsonObject.get("response").getAsBoolean()) {
                                JsonArray return_details = jsonObject.getAsJsonArray("return_details");

                                if (return_details.size() > 0) {
                                    // Adding an extra item to status list for remove filter, i.e., All
                                     for (int i = 0; i < return_details.size(); i++) {

                                           lat=return_details.get(i).getAsJsonObject().get("lat").getAsString();
                                           lng=return_details.get(i).getAsJsonObject().get("lng").getAsString();

                                         String address=Utils.getGeoLocationAddress(activity,Double.valueOf(lat) ,Double.valueOf(lng));
                                         address_tv.setText(address);


                                     }
                                    Double dist_in = getDistance(Double.valueOf(userLat), Double.valueOf(userLong), Double.valueOf(lat), Double.valueOf(lng));
                                    if(dist_in>500){
                                        checkDistanceDialog();
                                    }

                                }else{
                                    showDialog("This is not valid NDDN Code");

                                }



                            } else {
//                                progress_bar.setVisibility(View.GONE);
                                Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                        "Not getting any data", false);
                                showDialog("This is not valid NDDN Code");
                            }
                        } else {
//                            progress_bar.setVisibility(View.GONE);
                            Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                    Constants.MESSAGE_TRY_AGAIN_LATER, false);
                        }
                    }
                } catch (Exception e) {
//                    progress_bar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                            Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (activity != null && isAdded()) {
//                    progress_bar.setVisibility(View.GONE);
                    call.cancel();
                    t.printStackTrace();
                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                            Utils.failureMessage(t), false);
                }
            }
        });

     }

}
