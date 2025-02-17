package com.citizen.fmc.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.cardview.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.adapter.CommentsAdapter;
import com.citizen.fmc.model.CommentsModel;
import com.citizen.fmc.model.ComplaintStatusModel;
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
import com.google.android.gms.maps.MapFragment;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.ipaulpro.afilechooser.utils.FileUtils;
//import com.theartofdev.edmodo.cropper.CropImage;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;

import java.io.File;
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

/**
 * ======> Created by dheeraj-gangwar on 2018-02-07 <======
 */

@SuppressLint("SetTextI18n")
public class ComplaintCommentsFragment extends Fragment implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = getClass().getSimpleName();
    private Location location;
    private GoogleApiClient mGoogleApiClient;
    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 2;
    final static int REQUEST_LOCATION = 101;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private MapFragment mapFragment;
    private double userLat = 0.0;
    private double userLong = 0.0;

    private Activity activity;
    private List<CommentsModel> allCommentsList;
    private List<ComplaintStatusModel> compStatusList;
    private String compNum;
    private ListView allCommentsLV;
    private LinearLayout noCommentsLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextInputLayout addCommentIL;
    private TextInputEditText addCommentET;
    private ImageView sendCommentIV;
    private ImageView addIV;
    private ImageView newCommentIV;
    private Button removeImageButton;
    private CardView commentImageCardView;
    private SpotsDialog spotsDialog;
    private ApiInterface mApiInterface;
    private final int IMAGE_PICK_REQUEST_CODE = 1001;
    private File capturedFile = null;
    // variable to track event time
    private long mLastClickTime = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (this.getArguments() != null) {
            compNum = this.getArguments().getString(Constants.KEY_COMPLAINT_NUM);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v(TAG, "onDetach...");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_complaint_comments, container, false);
        swipeRefreshLayout = mView.findViewById(R.id.parent_layout);
        allCommentsLV = mView.findViewById(R.id.list_view);
        noCommentsLayout = mView.findViewById(R.id.layout_no_comments);
        TextView durationTextView = mView.findViewById(R.id.complaint_duration_text_view);
        TextView complaintNumTextView = mView.findViewById(R.id.complaint_num_text_view);
        addCommentIL = mView.findViewById(R.id.add_comment_input_layout);
        addIV = mView.findViewById(R.id.add_image_view);
        addCommentET = mView.findViewById(R.id.add_comment_edit_text);
        sendCommentIV = mView.findViewById(R.id.add_comment_image_view);
        removeImageButton = mView.findViewById(R.id.remove_comment_image_button);
        newCommentIV = mView.findViewById(R.id.new_comment_image_view);
        commentImageCardView = mView.findViewById(R.id.new_comment_image_card_view);

        swipeRefreshLayout.setBackgroundColor(getResources().getColor(R.color.colorBackground));

        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_refresh_color_scheme));

        activity = getActivity();
        Utils.setToolbarTitle(activity, "Comments");

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

                            /*@Override
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

                        /*    @Override
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

        String complaintNum = getArguments().getString(Constants.KEY_COMPLAINT_NUM);

        long days = getArguments().getLong(Constants.KEY_COMPLAINT_DAYS, Constants.DEFAULT_INTEGER_VALUE);
        long hours = getArguments().getLong(Constants.KEY_COMPLAINT_HOURS, Constants.DEFAULT_INTEGER_VALUE);
        long minutes = getArguments().getLong(Constants.KEY_COMPLAINT_MINUTES, Constants.DEFAULT_INTEGER_VALUE);


        complaintNumTextView.setText("#" + complaintNum);
        durationTextView.setText(Utils.getComplaintDuration(days, hours, minutes));

        mApiInterface = Utils.getInterfaceService();

        spotsDialog = new SpotsDialog(activity,
                getResources().getString(R.string.please_wait_dialog_text),
                R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareAllCompDataList();
            }
        });

        prepareAllCompDataList();

        addCommentEditTextInitialize();

        imageCaptureButtonOnClick();

        removeImageButtonOnClick();

        return mView;
    }

    /* ====================== Adding elements to List ====================== */
    private void prepareAllCompDataList() {
        if (!spotsDialog.isShowing()) {
            spotsDialog.show();
        }
        allCommentsLV.setAdapter(null);
        allCommentsList = new ArrayList<>();
        compStatusList = new ArrayList<>();

        getComplaintAllComments();
    }
    /* ================================================================================================= */


    /* ====================== Setting all complaints adapter to ListView ====================== */
    private void setAllCompListViewAdapter() {
        allCommentsLV.setAdapter(null);
        CommentsAdapter adapter = new CommentsAdapter(activity, allCommentsList, /*compStatusList,*/ getFragmentManager());
        allCommentsLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        complaintListViewItemClick(allCommentsList);
    }
    /* ================================================================================================= */


    /* ====================== Select Complaint Categories onClickListener ====================== */
    private void imageCaptureButtonOnClick() {
        addIV.setOnClickListener(new View.OnClickListener() {
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

                             /*   @Override
                                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                    Utils.customToast(activity, "PERMISSION DENIED", 0);
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
                                    startActivityForResult(Utils.createChooserIntent(activity, getCaptureImageOutputUri()), IMAGE_PICK_REQUEST_CODE);
                                }

                                @Override
                                public void onPermissionDenied(List<String> deniedPermissions) {
                                    Utils.customToast(activity, "PERMISSION DENIED", 0);

                                }

                               /* @Override
                                public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                    Utils.customToast(activity, "PERMISSION DENIED", 0);
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
            }
        });
    }
    /* ================================================================================================= */


    /* ====================== Remove comment image button onClickListener ====================== */
    private void removeImageButtonOnClick() {
        removeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCommentIV.setImageDrawable(null);
                commentImageCardView.setVisibility(View.GONE);
            }
        });
    }
    /* ================================================================================================= */


    private void addCommentEditTextInitialize() {
        addCommentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    addCommentIL.setErrorEnabled(true);
                    addCommentIL.setError("Write a comment first !");
                } else {
                    addCommentIL.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendCommentIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addCommentET.getText().toString().trim().isEmpty()) {
                    // Preventing multiple clicks, using threshold of 1 second
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    if (ConnectivityReceiver.isConnected()) {
                        spotsDialog.show();
                        addComplaintComments();
                    } else {
                        Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                Constants.MESSAGE_CHECK_INTERNET, true);
                    }
                } else {
                    addCommentIL.setErrorEnabled(true);
                    addCommentIL.setError("Write a comment first !");
                }
            }
        });
    }

    /* ====================== ListView onItemClickListener ====================== */
    private void complaintListViewItemClick(final List<CommentsModel> list) {
        allCommentsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                NewComplaintFragment complaintFragment = new NewComplaintFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString(Constants.KEY_COMPLAINT_PAGE_TYPE, Constants.VALUE_SHOW_COMPLAINT);
//                bundle.putSerializable(Constants.KEY_COMPLAINT_DATA, list.get(position));
//                complaintFragment.setArguments(bundle);
//                Utils.addFragment(getFragmentManager(), complaintFragment);
            }
        });
    }
    /* ================================================================================================= */


    /* ====================== Request method to get user's all complaints ====================== */
    private void getComplaintAllComments() {
        Log.v(TAG, Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity));
        Call<JsonObject> mService = mApiInterface.getComplaintComments(
                Utils.getUserDetails(activity).getCivilianId(),
                compNum,
                Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity),
                Constants.HEADER_ACCEPT);
        mService.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (activity != null && isAdded()) {
                        if (response.body() != null) {
                            JsonObject jsonObject = response.body();
                            if (jsonObject.get("response").getAsBoolean()) {
                                // Adding all complaints to list
                                JsonArray allCompJsonArray = jsonObject.getAsJsonArray("data");
                                if (allCompJsonArray.size() > 0) {
                                    for (int i = 0; i < allCompJsonArray.size(); i++) {
                                        CommentsModel allComplaintsModel = new GsonBuilder().create()
                                                .fromJson(allCompJsonArray.get(i), CommentsModel.class);
                                        allCommentsList.add(allComplaintsModel);
                                    }
                                }

                                // Adding complaint status to list
//                                JsonArray compStatusJsonArray = jsonObject.getAsJsonArray("complaint_status");
//                                if (compStatusJsonArray.size() > 0) {
//                                    for (int i = 0; i < compStatusJsonArray.size(); i++) {
//                                        ComplaintStatusModel statusModel = new GsonBuilder().create()
//                                                .fromJson(compStatusJsonArray.get(i), ComplaintStatusModel.class);
//                                        compStatusList.add(statusModel);
//                                    }
//                                }

                                if (allCommentsList.isEmpty()) {
                                    allCommentsLV.setVisibility(View.GONE);
                                    noCommentsLayout.setVisibility(View.VISIBLE);
                                } else {
                                    noCommentsLayout.setVisibility(View.GONE);
                                    allCommentsLV.setVisibility(View.VISIBLE);
                                    setAllCompListViewAdapter();
                                }

                                Log.v(TAG, allCommentsList.toString());
                                Log.v(TAG, compStatusList.toString());
                                spotsDialog.dismiss();
                                swipeRefreshLayout.setRefreshing(false);
                            } else {
                                allCommentsLV.setVisibility(View.GONE);
                                noCommentsLayout.setVisibility(View.VISIBLE);
                                spotsDialog.dismiss();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        } else {
                            spotsDialog.dismiss();
                            swipeRefreshLayout.setRefreshing(false);
                            Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                    Constants.MESSAGE_NO_DATA_FOUND, false);
                        }
                    }
                } catch (Exception e) {
                    spotsDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                    e.printStackTrace();
                    Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (activity != null && isAdded()) {
                    spotsDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                    call.cancel();
                    t.printStackTrace();
                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content), Utils.failureMessage(t), false);
                }
            }
        });
    }
    /* ================================================================================================= */


    /* ====================== Request method to get user's all complaints ====================== */
    private void addComplaintComments() {
        // Getting Coordinates and GeoLocation Address
        double userLat = location != null ? location.getLatitude() : 0.0;
        double userLong = location != null ? location.getLongitude() : 0.0;
        String userGeoAddress = location != null ? Utils.getGeoLocationAddress(activity, userLat, userLong) : "N/A";

        Log.v(TAG, "User Location Info===>\nLAT: " + userLat + ",\nLONG: " + userLong + ",\nGEO-ADDRESS: " + userGeoAddress);

        MultipartBody.Part imageFile;
        if (commentImageCardView.getVisibility() == View.VISIBLE) {
            imageFile = MultipartBody.Part.createFormData("image_source", capturedFile.getName(),
                    RequestBody.create(MediaType.parse("multipart/form-data"), capturedFile));
        } else {
            imageFile = MultipartBody.Part.createFormData("image_source", "");
        }

        Log.v(TAG, Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity));

        Call<JsonObject> mService = mApiInterface.addComplaintComment(
                imageFile,
                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userLat)),
                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userLong)),
                RequestBody.create(MediaType.parse("multipart/form-data"), userGeoAddress),
                RequestBody.create(MediaType.parse("multipart/form-data"), addCommentET.getText().toString().trim()),
                Utils.getUserDetails(activity).getCivilianId(),
                compNum,
                Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity),
                Constants.HEADER_ACCEPT);
        mService.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (activity != null && isAdded()) {
                        if (response.body() != null) {
                            JsonObject jsonObject = response.body();
                            if (jsonObject.get("response").getAsBoolean()) {
                                commentImageCardView.setVisibility(View.GONE);
                                addCommentET.setText("");
                                addCommentIL.setErrorEnabled(false);
                                prepareAllCompDataList();
                            } else {
                                spotsDialog.dismiss();
                                swipeRefreshLayout.setRefreshing(false);
                                Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                        jsonObject.get("message").getAsString().trim(), false);
                            }
                        } else {
                            spotsDialog.dismiss();
                            swipeRefreshLayout.setRefreshing(false);
                            Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                    Constants.MESSAGE_NO_DATA_FOUND, false);
                        }
                    }
                } catch (Exception e) {
                    spotsDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                    e.printStackTrace();
                    Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (activity != null && isAdded()) {
                    spotsDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                    call.cancel();
                    t.printStackTrace();
                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content), Utils.failureMessage(t), false);
                }
            }
        });
    }
    /* ================================================================================================= */

    public Intent createChooserIntent() {
        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = activity.getPackageManager();

        // Create the ACTION_GET_CONTENT Intent
//        Intent getContentIntent = FileUtils.createGetContentIntent();
//        FileUtils.createGetContentIntent()
//
//        Intent intent = Intent.createChooser(getContentIntent, "Select a file");
//        startActivityForResult(intent, 89);

        // collect all camera intents
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                activity.grantUriPermission(res.activityInfo.packageName, outputFileUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // Create the ACTION_GET_CONTENT Intent
//        Intent getContentIntent = FileUtils.createGetContentIntent();
//        // For images only
//        getContentIntent.setType("image/*");
//        List<ResolveInfo> listContent = packageManager.queryIntentActivities(getContentIntent, 0);
//        for (ResolveInfo res : listContent) {
//            Intent intent = new Intent(getContentIntent);
//            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//            intent.setPackage(res.activityInfo.packageName);
//            allIntents.add(intent);
//        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        try {
            for (Intent intent : allIntents) {
                if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                    mainIntent = intent;
                    break;
                }
            }
            allIntents.remove(mainIntent);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }


    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        capturedFile = new File(activity.getExternalCacheDir(), Constants.FILE_NAME_PNG);
        return FileProvider.getUriForFile(activity,
                activity.getPackageName() + ".provider", capturedFile);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_notification).setVisible(false);
    }

    private final ActivityResultLauncher<CropImageContractOptions> cropImageLauncher =
            registerForActivityResult(new CropImageContract(), result -> {
                if (result.isSuccessful()) {
                    // Successfully cropped
                    Uri resultUri = result.getUriContent();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), resultUri);

                        newCommentIV.setImageBitmap(bitmap);
                        commentImageCardView.setVisibility(View.VISIBLE);

                        // Optionally, save file or perform further processing
//                        String path = FileUtils.getPath(this, resultUri);
//                        Log.v(TAG, "File path: " + path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Error occurred
                    Exception error = result.getError();
                    if (error != null) {
                        error.printStackTrace();
                    }
                }
            });

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

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            Bitmap bitmap;
            switch (requestCode) {
                case IMAGE_PICK_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
                        final Uri _mainUri;
                        if (data != null) {
                            _mainUri = data.getData();
                        } else {
                            _mainUri = FileUtils.getUri(capturedFile);
                        }

                        if (_mainUri != null) {
                            // start cropping activity for pre-acquired image saved on the device
                           launchImageCropper(_mainUri);
                        } else {
                            Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                    Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
                        }
                    }
                    break;

/*                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri resultUri = result.getUriContent();

                        // Get the File path from the Uri
                        String path = FileUtils.getPath(activity, resultUri);

                        // Alternatively, use FileUtils.getFile(Context, Uri)
                        if (path != null) {
                            capturedFile = FileUtils.getFile(activity, resultUri);
                            double size = Utils.getFileSize(capturedFile);
                            Log.v(TAG, "file_size(MB): " + size
                                    + "\n file_name: " + capturedFile.getName()
                                    + "\n file_path: " + path);

                            bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), resultUri);
//                                bitmap = ImageHelper.rotateImageIfRequired(bitmap, capturedFile.getAbsolutePath());
//                                bitmap = ImageHelper.getResizedBitmap(bitmap, 500);
                            newCommentIV.setImageBitmap(bitmap);
                            commentImageCardView.setVisibility(View.VISIBLE);
                        }
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        result.getError().printStackTrace();
                        Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
                    }
                    break;*/

                case REQUEST_LOCATION:
                    switch (resultCode) {
                        case RESULT_OK:
                            startLocationUpdates();
                            break;

                        case RESULT_CANCELED: {
                            // The user was asked to change settings, but chose not to
                            getFragmentManager().popBackStackImmediate();
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
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
//        if (mGoogleApiClient.isConnected()) {
//            startLocationUpdates();
//        }

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
