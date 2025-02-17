package com.citizen.fmc.fragment;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.citizen.fmc.R;
import com.citizen.fmc.adapter.WhatsNearMeSubModuleRecyclerAdapter;
import com.citizen.fmc.model.LocationModal;
import com.citizen.fmc.model.WhatsNearMeModel;
import com.citizen.fmc.utils.ApiInterface;
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
import com.google.android.gms.maps.MapFragment;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ======> Created by dheeraj-gangwar on 2018-04-21 <======
 */

public class WhatsNearMeSubModuleFragment extends Fragment implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    private Location location;
    private GoogleApiClient mGoogleApiClient;
    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 2;
    final static int REQUEST_LOCATION = 101;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private MapFragment mapFragment;

    private FragmentActivity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recycler_view;
    private ArrayList<WhatsNearMeModel> modelArrayList;
    private ArrayList<WhatsNearMeModel> mainDataList;
    private int SUB_MODULE_ID;
    private boolean refreshDataList = true;
    private SpotsDialog spotsDialog;
     private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private  WhatsNearMeSubModuleRecyclerAdapter adapter;
    private ImageView map_icon;
    private SearchView mSearchView;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<LocationModal> locationModalArrayList;
    private int nextPageCount = 1;
    private Boolean isLoadingData = false;
    private int currentItems;
    private int totalItems;
    private int scrollOutItems;
    private int nextPage=0;
    private int totalPage=0;
    private boolean hasMorePage;
    private ProgressBar progress_bar;
    private LinearLayout search_map_parent_view;
    private String dataMessage,hidposstion="";
    String toolbarTitle="";
    @Override
    public void onDetach() {
        super.onDetach();
        Log.v(TAG, "onDetach...");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG, "onDestroyView...");
        if(linearLayoutManager !=null){
            linearLayoutManager = null;
            nextPageCount = 0;
        }
        if (mapFragment != null) {
            activity.getFragmentManager().beginTransaction().remove(mapFragment).commit();
        }

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(activity);
            mGoogleApiClient.disconnect();
        }
        stopLocationUpdates();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.whats_near_me_list_view, container, false);
        activity = getActivity();
        getCurrentLocation();
        linearLayoutManager = new LinearLayoutManager(getActivity());
        swipeRefreshLayout = mView.findViewById(R.id.parent_layout);
        recycler_view = mView.findViewById(R.id.recycler_view);
        progress_bar = mView.findViewById(R.id.progress_bar);
        map_icon = mView.findViewById(R.id.map_icon);
        search_map_parent_view = mView.findViewById(R.id.search_map_parent_view);
        mSearchView = mView.findViewById(R.id.search_et);
        modelArrayList = new ArrayList<>();
        mainDataList = new ArrayList<>();


        //ProgressBar SetUp:-
        progress_bar.setIndeterminate(false);
        progress_bar.setVisibility(View.VISIBLE);

        spotsDialog = new SpotsDialog(activity,
                getResources().getString(R.string.please_wait_dialog_text), R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);
          toolbarTitle = getArguments().getString(Constants.KEY_TOOL_BAR_TITLE);
        Utils.setToolbarTitle(activity, toolbarTitle);

        SUB_MODULE_ID = getArguments().getInt(Constants.KEY_MODULE_ID, Constants.DEFAULT_INTEGER_VALUE);
        if ((SUB_MODULE_ID==90)||(SUB_MODULE_ID==89)){
            map_icon.setVisibility(View.GONE);
        }
        if ((ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
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

                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                getFragmentManager().popBackStackImmediate();
                            }
                        })
                        .setGotoSettingButtonText("SETTINGS")
                        .setDeniedMessage("If you reject permission,you can not use this service" +
                                "\n\nPlease turn on permissions at [Settings] > [Permission]")
                        .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.CAMERA)
                        .check();
            }else {
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

                            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                getFragmentManager().popBackStackImmediate();
                            }
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

        //Setting Adapter:-
        setListAdapter();
        getModules();

        //SearchView call to perform search:-
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchComplaint(newText);
                return false;
            }
        });

        //Map Icon Click Event:-
        map_icon.setOnClickListener(this);

        //OnScrollListener in RecyclerView to Load More Data:-
        recycler_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isLoadingData = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = linearLayoutManager.getChildCount();
                totalItems = linearLayoutManager.getItemCount();
                scrollOutItems = linearLayoutManager.findFirstVisibleItemPosition();

                if (isLoadingData && (currentItems + scrollOutItems == totalItems) && hasMorePage) {
                    isLoadingData = false;
                    progress_bar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadMoreData();
                        }
                    }, 2000);
                }
            }
        });


        return mView;
    }

    private void loadMoreData() {
        List<WhatsNearMeModel> whatNearMeData = getModules();
        adapter.addAll(whatNearMeData);
        progress_bar.setVisibility(View.GONE);

        //adapter.removeLodedSomeData();
        //adapter.addLoadingFooter();
    }

    private void addAllLatitudeAndLongitude() {
        //Adding Latitude and Longitude data to Another List:-
        locationModalArrayList = new ArrayList<>();
        locationModalArrayList.clear();
        for(int i = 0; i< mainDataList.size(); i++){
            locationModalArrayList.add(new LocationModal(mainDataList.get(i).getLatitude() ,
                    mainDataList.get(i).getLongitude() , mainDataList.get(i).getAddress()));
        }
    }

    private void getCurrentLocation() {
            currentLatitude = LocationFetch.latitude;
            currentLongitude = LocationFetch.longitude;
    }

    private void setListAdapter() {
        modelArrayList.clear();
        adapter = new WhatsNearMeSubModuleRecyclerAdapter(activity, getFragmentManager(),
                       modelArrayList, currentLatitude , currentLongitude,SUB_MODULE_ID);
        recycler_view.setLayoutManager(linearLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(adapter);





        //adapter.notifyDataSetChanged();
        //refreshDataList = false;
    }


    /* ====================== Request method to get user's all complaints ====================== */
    private List<WhatsNearMeModel> getModules() {
        modelArrayList.clear();
        ApiInterface mApiInterface = Utils.getInterfaceService();
        String userToken = Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity);
        int userId = Utils.getUserDetails(activity).getCivilianId();
        Log.v(TAG, "USER_TOKEN: " + userToken);
        Log.v(TAG, "USER_ID: " + String.valueOf(userId));

        Call<JsonObject> mService = mApiInterface.getWhatsNearMeModules(SUB_MODULE_ID, userId,
                Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity),
                Constants.HEADER_ACCEPT, nextPageCount);
        mService.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (activity != null && isAdded()) {
                        if (response.body() != null) {
                            JsonObject jsonObject = response.body();
                            dataMessage = jsonObject.get("message").getAsString();
                            if (jsonObject.get("response").getAsBoolean()) {
                                JsonArray dataJsonArray = jsonObject.getAsJsonArray("data");
                                 nextPage = jsonObject.get("nextPage").getAsInt();
                                 totalPage = jsonObject.get("lastPage").getAsInt();
                                 hasMorePage = jsonObject.get("hasMorePages").getAsBoolean();

                                 nextPageCount = nextPage;
                                if (dataJsonArray.size() > 0) {
                                    for (int i = 0; i < dataJsonArray.size(); i++) {
                                        WhatsNearMeModel model = new GsonBuilder().create()
                                                .fromJson(dataJsonArray.get(i), WhatsNearMeModel.class);
                                        modelArrayList.add(model);
                                        mainDataList.add(model);
                                    }
                                }
                                //Adding Data to Adapter and notify it also showing Search and Map Layout view:-
                                adapter.addAll(modelArrayList);
                                adapter.notifyDataSetChanged();
                                search_map_parent_view.setVisibility(View.VISIBLE);

                                //Adding Latitude and Longitude :-
                                addAllLatitudeAndLongitude();
                                Constants.customToast(getActivity() , dataMessage , 2);
                                Log.v(TAG, modelArrayList.toString());
                                progress_bar.setVisibility(View.GONE);
                            } else {
                                search_map_parent_view.setVisibility(View.GONE);
                                progress_bar.setVisibility(View.GONE);
                                getFragmentManager().popBackStackImmediate();
                                Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                        jsonObject.get("message").getAsString().trim(), false);
                            }
                        } else {
                            search_map_parent_view.setVisibility(View.GONE);
                            progress_bar.setVisibility(View.GONE);
                            Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                    Constants.MESSAGE_TRY_AGAIN_LATER, false);
                        }
                    }
                } catch (Exception e) {
                    search_map_parent_view.setVisibility(View.GONE);
                    progress_bar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                            Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (activity != null && isAdded()) {
                    search_map_parent_view.setVisibility(View.GONE);
                    progress_bar.setVisibility(View.GONE);
                    call.cancel();
                    t.printStackTrace();
                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                            Utils.failureMessage(t), false);
                }
            }
        });

        return modelArrayList;
    }
    /* ================================================================================================= */


    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .addApi(LocationServices.API)
                    .enableAutoManage(activity, this)
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
        mFusedLocationProviderClient
                .requestLocationUpdates(mLocationRequest, mLocationCallback,
                        Looper.myLooper());
    }

    protected void stopLocationUpdates() {
        if (mLocationCallback != null) {
            mFusedLocationProviderClient
                    .removeLocationUpdates(mLocationCallback);
            Log.d(TAG, "Location update stopped ..............");
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        Log.d(TAG, "Firing onLocationChanged ..............");
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

        //prepareDataList();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_notification).setVisible(false);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);

        final SearchView searchView;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                if (searchManager != null) {
                    searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
                    searchView.setQueryHint("Search here...");


                    // Get the search close button image view
                    ImageView closeButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);

                    // Set on click listener
                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            searchView.setQuery("", false);
                        }
                    });

                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            if (adapter != null) {
                                // Performing search
                                adapter.getFilter().filter(newText);
                                //complaintListViewItemClick();
                            }
                            return false;
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.map_icon) {
            changeToHeatMapFragment();
        }
    }

    private void changeToHeatMapFragment() {
        WhatsNearMeHeatMapFragment whatsNearMeHeatMapFragment = new WhatsNearMeHeatMapFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(Constants.KEY_TOOL_BAR_TITLE, Constants.MAP_VIEW);
        mBundle.putParcelableArrayList(Constants.LATITUDE_LONGITUDE_LIST ,  locationModalArrayList);
        whatsNearMeHeatMapFragment.setArguments(mBundle);
        Utils.changeFragment(getFragmentManager() , whatsNearMeHeatMapFragment);
    }

    private void searchComplaint(String character) {
        if (adapter != null) {
            if (!character.isEmpty()) {
                adapter.getFilter().filter(character);
            } else {
                // Selected filtered item is All
                adapter = new WhatsNearMeSubModuleRecyclerAdapter(activity, getFragmentManager(),
                        modelArrayList, currentLatitude , currentLongitude,SUB_MODULE_ID);
                linearLayoutManager = new LinearLayoutManager(getActivity());
                recycler_view.setLayoutManager(linearLayoutManager);
                recycler_view.setItemAnimator(new DefaultItemAnimator());
                recycler_view.setAdapter(adapter);
                refreshDataList = false;
                adapter.notifyDataSetChanged();
            }
        }
    }


//    @Override
//    public void onStart() {
//        super.onStart();
//        Log.d(TAG, "onStart fired ..............");
//        mGoogleApiClient.connect();
//    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        Log.d(TAG, "onStop fired ..............");
//        mGoogleApiClient.disconnect();
//        Log.d(TAG, "isConnected ..............: " + mGoogleApiClient.isConnected());
//    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        stopLocationUpdates();
//    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        if (mGoogleApiClient.isConnected()) {
//            startLocationUpdates();
//            Log.d(TAG, "Location update resumed ..............");
//        }
//    }


}
