package com.citizen.fmc.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.canhub.cropper.CropImage;
import com.citizen.fmc.R;
import com.citizen.fmc.utils.ApiInterface;
import com.citizen.fmc.utils.ButtonView;
import com.citizen.fmc.utils.ConnectivityReceiver;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.ipaulpro.afilechooser.utils.FileUtils;
//import com.theartofdev.edmodo.cropper.CropImage;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ReopenComplaint extends Fragment implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private View mView;
    private Activity activity;
    private SimpleDraweeView re_complaint_simple_drawee_v;
    private ImageView re_complaint_image_v;
    private TextView re_capture_text_v;
    private ImageButton re_capture_image_b;
    private EditText re_complaint_descript_edit_t;
    private TextView geo_address_text_v;
    private MapFragment mapFragment;
    private ButtonView re_submit_complaint_b;
    private SpotsDialog spotsDialog;
    private String uniqueId = "";
    private int userId = 0;
    private double userLat = 0.0;
    private double userLong = 0.0;
    private final String TAG = getClass().getSimpleName();
    private Location location;
    private GoogleApiClient mGoogleApiClient;
    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 2;
    final static int REQUEST_LOCATION = 101;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    LatLng latLng = null;
    private FragmentManager fragmentManager;
    String title = "";
    private File capturedFile = null;
    private final int IMAGE_PICK_REQUEST_CODE = 1001;
    Uri _mainUri, resultUri;
    ScrollView main_scroll_view;
    private Bitmap bitmap;
    String imagePath = "", complaintNum = "";
    private File _shrinkImageFile = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_reopen_complaint, container, false);

        viewPramas(mView);
        complaintNum = getArguments().getString(Constants.KEY_COMPLAINT_NUM);
        activity = getActivity();
        mapFragment = (MapFragment) activity.getFragmentManager().findFragmentById(R.id.map_fragment);

        userId = Utils.getUserDetails(activity).getCivilianId();
        uniqueId = Utils.generateUniqueId(userId);
        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        spotsDialog = new SpotsDialog(activity, getResources().getString(R.string.please_wait_dialog_text), R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);
        submitComplaintButtonOnClickListener();
        imageCaptureButtonOnClick();

        Utils.setToolbarTitle(activity, "Re-Open Complaint");
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

        mapFragment.getMapAsync(this);

        return mView;


    }

    public void viewPramas(View view) {

        re_complaint_simple_drawee_v = view.findViewById(R.id.re_complaint_simple_drawee_v);
        re_complaint_image_v = view.findViewById(R.id.re_complaint_image_v);
        re_capture_text_v = view.findViewById(R.id.re_capture_text_v);
        re_capture_image_b = view.findViewById(R.id.re_capture_image_b);
        re_complaint_descript_edit_t = view.findViewById(R.id.re_complaint_descript_edit_t);
        geo_address_text_v = view.findViewById(R.id.geo_address_text_v);
        re_complaint_simple_drawee_v = view.findViewById(R.id.re_complaint_simple_drawee_v);
        re_submit_complaint_b = view.findViewById(R.id.re_submit_complaint_b);
        main_scroll_view = view.findViewById(R.id.main_scroll_view);


    }


    private void imageCaptureButtonOnClick() {
        re_capture_image_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                    TedPermission.create()
                            .setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
                                    startActivityForResult(Utils.createChooserIntent(activity, getCaptureImageOutputUri()), IMAGE_PICK_REQUEST_CODE);
                                }

                                @Override
                                public void onPermissionDenied(List<String> deniedPermissions) {
                                    Utils.customToast(activity, "PERMISSION DENIED", 0);
                                }

                                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                    Utils.customToast(activity, "PERMISSION DENIED", 0);
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
                                    startActivityForResult(Utils.createChooserIntent(activity, getCaptureImageOutputUri()), IMAGE_PICK_REQUEST_CODE);
                                }

                                @Override
                                public void onPermissionDenied(List<String> deniedPermissions) {
                                    Utils.customToast(activity, "PERMISSION DENIED", 0);
                                }

                                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                    Utils.customToast(activity, "PERMISSION DENIED", 0);
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
            }
        });
    }

    private Uri getCaptureImageOutputUri() {
        capturedFile = new File(activity.getExternalCacheDir(), Constants.FILE_NAME_PNG);
        return FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", capturedFile);
    }


    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), result -> {
        if (result.isSuccessful()) {
            Bitmap cropped = BitmapFactory.decodeFile(result.getUriFilePath(getActivity(), true));
        }

        if (result.isSuccessful()) {
            Uri resultUri = result.getOriginalUri();
            // Get the File path from the Uri
            String path = FileUtils.getPath(getActivity(), resultUri);

            // Alternatively, use FileUtils.getFile(Context, Uri)
            if (path != null) {
                capturedFile = FileUtils.getFile(activity, resultUri);
                double size = Constants.getFileSize(capturedFile);
                Log.v(TAG, "file_size(MB): " + size
                        + "\n file_name: " + capturedFile.getName()
                        + "\n file_path: " + path);
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeFile(result.getUriFilePath(getActivity(), true));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        else  {
            result.getError().printStackTrace();
//            Constants.customToast(getActivity(), Constants.SOMETHING_WRONG_MSG, 2);
        }
    });

    private void launchImageCropper(Uri uri) {
        CropImageOptions cropImageOptions = new CropImageOptions();
        cropImageOptions.imageSourceIncludeGallery = false;
        cropImageOptions.imageSourceIncludeCamera = true;
        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(uri, cropImageOptions);
        cropImage.launch(cropImageContractOptions);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {
                case IMAGE_PICK_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
                        if (data != null) {
                            _mainUri = data.getData();
                        }
                        if (data == null || _mainUri == null) {
                            _mainUri = FileUtils.getUri(capturedFile);
                        }
                        if (_mainUri != null) {
                            // start cropping activity for pre-acquired image saved on the device
                            launchImageCropper(_mainUri);
                        } else {
                            Utils.showSnackBar(activity, main_scroll_view, Constants.MESSAGE_SOMETHING_WENT_WRONG, false);

                        }
                    }
                    break;

                /*case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        resultUri = result.getUri();
                        // Get the File path from the Uri
                        String path = FileUtils.getPath(activity, resultUri);
                        // Alternatively, use FileUtils.getFile(Context, Uri)
                        if (path != null) {
                            capturedFile = FileUtils.getFile(activity, resultUri);
                            double size = Utils.getFileSize(capturedFile);
                            Log.v(TAG, "file_size(MB): " + size
                                    + "\n file_name: " + capturedFile.getName()
                                    + "\n file_path: " + path);

                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), resultUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (imagePath == null) {
                                re_complaint_image_v.setImageBitmap(bitmap);
                                re_capture_text_v.setVisibility(View.GONE);
                                imagePath = FileUtils.getPath(activity, resultUri);
                            } else {
                                re_complaint_image_v.setImageBitmap(bitmap);
                                re_capture_text_v.setVisibility(View.GONE);
                                imagePath = FileUtils.getPath(activity, resultUri);
                            }
                        }
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        result.getError().printStackTrace();
                        Utils.showSnackBar(activity, main_scroll_view, Constants.MESSAGE_SOMETHING_WENT_WRONG, false);

                    }
                    break;*/

                case REQUEST_LOCATION:
                    switch (resultCode) {
                        case RESULT_OK:
                            startLocationUpdates();
                            break;

                        case RESULT_CANCELED: {
                            // The user was asked to change settings, but chose not to
                            if (getFragmentManager() != null) {
                                getFragmentManager().popBackStackImmediate();
                            }
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                    break;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void submitComplaintButtonOnClickListener() {
        re_submit_complaint_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (re_complaint_descript_edit_t.getText().toString().isEmpty()) {
                    Utils.showSnackBar(activity, main_scroll_view, "Please fill the remark !", false);
                } else {
                    if (ConnectivityReceiver.isConnected()) {
                        spotsDialog.show();
                        if (userLat == 0.0 || userLong == 0.0) {
                            Utils.showSnackBar(activity, main_scroll_view, " Lat & Long not found.. Please set mobile GPS at High Accuracy !", false);
                        } else {
                            requestToReOpenComplaint();
                        }
                    } else {
                        Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                Constants.MESSAGE_CHECK_INTERNET, false);
                    }
                }
            }
        });
    }


    private void requestToReOpenComplaint() {
        Call<JsonObject> mService;
        try {
            ApiInterface mApiInterface = Utils.getInterfaceService();
            if (capturedFile != null) {
                _shrinkImageFile = new File(capturedFile.getAbsolutePath());
                Bitmap imageBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(_shrinkImageFile.getPath()), 768, 1024, false);
                try {
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 30, new FileOutputStream(_shrinkImageFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                mService = mApiInterface.getReOpenComplaint(
                        RequestBody.create(MediaType.parse("multipart/form-data"), re_complaint_descript_edit_t.getText().toString()),
                        RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userId)),
                        RequestBody.create(MediaType.parse("multipart/form-data"), complaintNum),
                        RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userLat)),
                        RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userLong)),
                        RequestBody.create(MediaType.parse("multipart/form-data"), geo_address_text_v.getText().toString().trim()),
                        MultipartBody.Part.createFormData("image_source", _shrinkImageFile.getName(),
                                RequestBody.create(MediaType.parse("multipart/form-data"), _shrinkImageFile)));
            } else {
                mService = mApiInterface.getReOpenComplaint(
                        RequestBody.create(MediaType.parse("multipart/form-data"), re_complaint_descript_edit_t.getText().toString()),
                        RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userId)),
                        RequestBody.create(MediaType.parse("multipart/form-data"), complaintNum),
                        RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userLat)),
                        RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userLong)),
                        RequestBody.create(MediaType.parse("multipart/form-data"), geo_address_text_v.getText().toString().trim()),
                        MultipartBody.Part.createFormData("image_source", "",
                                RequestBody.create(MediaType.parse("multipart/form-data"), "")));
            }

            mService.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {
                        if (response.body() != null) {
                            JsonObject jsonObject = response.body();
                            if (jsonObject.get("response").getAsBoolean()) {
                                spotsDialog.dismiss();
                                Utils.customToast(activity, response.body().get("message").getAsString(), 1);

                                Utils.changeFragment(getFragmentManager(), new OptionListFragment());

                                Log.v(TAG, "Complaints submitted successfully !!!"
                                        + "\nComplaint No.===>" + complaintNum);
                            } else {
                                spotsDialog.dismiss();
                                String message = jsonObject.get("message").getAsString().trim();
                                Log.v(TAG, "Message===>" + message);
                                Utils.showSnackBar(activity, main_scroll_view, message, false);
                            }
                        } else {
                            spotsDialog.dismiss();
                            Utils.showSnackBar(activity, main_scroll_view, Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
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
                    Utils.showSnackBar(activity, main_scroll_view, Utils.failureMessage(t), false);
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_notification).setVisible(false);
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
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
        mapFragment.getMapAsync(this);
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
    public void onDetach() {
        super.onDetach();
        Log.v(TAG, "onDetach...");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            Log.v(TAG, "onDestroyView...");
            if (mapFragment != null) {
                activity.getFragmentManager().beginTransaction()
                        .remove(mapFragment).commit();
            }

            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.stopAutoManage((FragmentActivity) activity);
                mGoogleApiClient.disconnect();
            }
            stopLocationUpdates();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.clear();
        try {

            if (location != null) {
                userLat = location.getLatitude();
                userLong = location.getLongitude();
                latLng = new LatLng(userLat, userLong);
                title = Utils.getGeoLocationAddress(activity, userLat, userLong);
            }

            if (latLng != null) {
                geo_address_text_v.setText(title);
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .snippet((latLng.latitude) + ", " + (latLng.longitude))
                        .icon(BitmapDescriptorFactory.fromBitmap(Constants
                                .getBitmapFromVectorDrawable(activity, R.drawable.ic_placeholder))));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                marker.showInfoWindow();
            }
        } catch (NullPointerException | IllegalStateException | IllegalArgumentException e) {
            e.printStackTrace();
        }

    }
}

