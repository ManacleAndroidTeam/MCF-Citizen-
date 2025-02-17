package com.citizen.fmc.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.citizen.fmc.R;
import com.citizen.fmc.adapter.NearByTreeListAdapter;
import com.citizen.fmc.model.NearByTreeListModel;
import com.citizen.fmc.utils.ApiInterface;
import com.citizen.fmc.utils.ConnectivityReceiver;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.LocationFetch;
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

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearByTreeFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener,
        LocationListener, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{
    private ListView nearbyTreeListView;
    public static List<NearByTreeListModel> nearbyTreeModels;
    private Activity activity;
    private String TAG = getClass().getSimpleName();
    private SpotsDialog spotsDialog;
    private double userLat = 0.0;
    private double userLong = 0.0;
    private Location mLocation;
    private GoogleApiClient mGoogleApiClient;
    final static int REQUEST_LOCATION = 101;
    private LocationRequest mLocationRequest = new LocationRequest();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private static final long INTERVAL = 1000 * 3;
    private static final long FASTEST_INTERVAL = 1000 * 2;
    private LocationFetch locationFetch = new LocationFetch();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.layout_list_view, container, false);

        activity = getActivity();
        nearbyTreeListView = mView.findViewById(R.id.list_view);
        spotsDialog = new SpotsDialog(activity,
                getResources().getString(R.string.please_wait_dialog_text),
                R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);

        spotsDialog.show();
        if(!gpsEnabledCheck()){
            LocationFetch.buildAlertMessageNoGps(getActivity());
        }
       /* Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                startLocationUpdates();
            }
        });*/
        startLocationUpdates();


       /* if (mLocation!=null)
        {
            userLat = mLocation.getLatitude();
            userLong = mLocation.getLongitude();
        }*/


//        listViewItemClick();

        return mView;
    }

    private boolean gpsEnabledCheck() {
        return (((LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER));
    }

    private void getCurrentAddress() {
        if(((LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locationFetch.getLocationFromNetwork(getActivity());
        }else if(((LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationFetch.getLocationFromGPS(getActivity());
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void prepareDataList() {
        nearbyTreeModels = new ArrayList<>();
        nearbyTreeModels.clear();
        getHelpLineNumbers();
    }

   /* private void setListViewAdapter() {
        NearByTreeListAdapter helpLineListItemAdapter = new NearByTreeListAdapter(activity, nearbyTreeModels);
        nearbyTreeListView.setAdapter(helpLineListItemAdapter);
        helpLineListItemAdapter.notifyDataSetChanged();
    }*/

    private void setListViewAdapter() {
        NearByTreeListAdapter helpLineListItemAdapter = new NearByTreeListAdapter(activity, nearbyTreeModels);
        nearbyTreeListView.setAdapter(helpLineListItemAdapter);
        helpLineListItemAdapter.notifyDataSetChanged();
    }

    /*private void listViewItemClick() {
        nearbyTreeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(Intent.ACTION_DIAL)
                        .setData(Uri.parse("tel:" + helpLineModels.get(position).getContactNumber())));
            }
        });
    }*/

    private void getHelpLineNumbers() {


        ApiInterface mApiInterface = Utils.getTreeService();

//        String userToken = Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity);
        String userToken = "FE18E136-5004-42B7-8A9F-F0EEBC69716B";
        int userId = Utils.getUserDetails(activity).getCivilianId();
        Log.v(TAG, "USER_TOKEN: " + userToken);
        Log.v(TAG, "USER_ID: " + String.valueOf(userId));
        String creds = "nitin,hello";
        String projectid = "20";
        Call<List<NearByTreeListModel>> mService = mApiInterface.getTreeData(userLat, userLong, creds, projectid, userToken, Constants.HEADER_ACCEPT);
        mService.enqueue(new Callback<List<NearByTreeListModel>>() {
            @Override
            public void onResponse(Call<List<NearByTreeListModel>> call, Response<List<NearByTreeListModel>> response) {
                try {
                    if (activity != null && isAdded()) {
                        nearbyTreeModels = response.body();
                        Log.d("list", nearbyTreeModels.toString());
                        spotsDialog.dismiss();
                        if (nearbyTreeModels.size() > 0)
                        {
                            setListViewAdapter();
                    }
                    else
                    {
                     spotsDialog.dismiss();
                     Utils.showSnackBar(activity,activity.findViewById(android.R.id.content),Constants.MESSAGE_NO_DATA_FOUND,false);
                    }
                                Log.v(TAG, nearbyTreeModels.toString());

                            } else {
                                spotsDialog.dismiss();
                            }
                } catch (Exception e) {
                    spotsDialog.dismiss();
                    e.printStackTrace();
                    Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                }
            }

            @Override
            public void onFailure(Call<List<NearByTreeListModel>> call, Throwable t) {
                spotsDialog.dismiss();
                if (activity != null && isAdded()) {
                    spotsDialog.dismiss();
                    call.cancel();
                    t.printStackTrace();
                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content), Utils.failureMessage(t), false);
                }
            }
        });
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                    Constants.MESSAGE_INTERNET_CONNECTED, true);
        } else {
            Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                    Constants.MESSAGE_INTERNET_NOT_CONNECTED, false);
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            prepareDataList();
        }

        Log.i(TAG, "GoogleApiClient connected");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY));

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
                        // All mLocation settings are satisfied. The client can initialize mLocation
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
    public void onLocationChanged(Location location) {
        mLocation = location;
        Log.d(TAG, "Firing onLocationChanged ..............");
        if (mLocation != null) {
            Log.d(TAG, "LAT: " + mLocation.getLatitude() + ",\nLONG: " + mLocation.getLongitude());

            userLat=mLocation.getLatitude();
            userLong=mLocation.getLongitude();
            /*userLat=28.6304;
            userLong=77.2177;*/

        }
    }

    protected void startLocationUpdates() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
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

        // Create the mLocation request to start receiving updates
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Create LocationSettingsRequest object using mLocation request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether mLocation settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(activity);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

}
