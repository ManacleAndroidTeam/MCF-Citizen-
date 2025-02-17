package com.citizen.fmc.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.citizen.fmc.activity.CustomCameraXActivity;
import com.citizen.fmc.interface_.GetOnItemClick;
import com.google.android.material.appbar.AppBarLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.citizen.fmc.R;
import com.citizen.fmc.activity.SlidingDrawerActivity;
import com.citizen.fmc.adapter.CompCatExpListAdapter;
import com.citizen.fmc.model.AllComplaintsModel;
import com.citizen.fmc.model.ComplaintCategoryModel;
import com.citizen.fmc.model.ViewAllComplaintsModel;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
//import static android.os.Build.VERSION_CODES.R;

import static com.facebook.FacebookSdk.getApplicationContext;

import org.apache.commons.io.output.ByteArrayOutputStream;

/**
 * ======> Created by dheeraj-gangwar on 2018-01-11 <======
 */

@SuppressLint("SetTextI18n, ClickableViewAccessibility")
public class NewComplaintFragment extends Fragment implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback, GetOnItemClick {
    private final String TAG = getClass().getSimpleName();
    private Location location;
    private GoogleApiClient mGoogleApiClient;
    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 2;
    final static int REQUEST_LOCATION = 101;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private MapFragment mapFragment;
    LatLng latLng = null;
    String title = "";
    String imagePath = "";
    private ImageView compIV;
    private TextView imageCaptureTV;
    private TextView compCatTV;
    private EditText compDescET;
    private EditText compAddET;
    private TextView geoAddressTextView;
    private ArrayList<ComplaintCategoryModel> compCatList;
    private ArrayList<ComplaintCatModel> airPollutionComCatlist;
    private Activity activity;
    private boolean refreshCategoryList = true;
    private ExpandableListView expLV;
    private RecyclerView apCatLv;
    private Dialog compCatDialog;
    private ImageButton captureCompIB;
    private File capturedFile = null;
    private File _shrinkImageFile = null;
    private String subDeptId = "0";
    private int deptId;
    private double userLat = 0.0;
    private double userLong = 0.0;
    private ButtonView submitCompBtn, vote_button;
    private View mView;
    private String PAGE_TYPE = "";
    private AllComplaintsModel complaintsModel;
    private ViewAllComplaintsModel allComplaintsModel;
    private AppBarLayout appBarLayout;
    private TextView mandatoryFieldsTextView;
    private TextView beforeTextView;
    private TextView afterTextView;
    private TextView durationTextView;
    private TextView complaintNumTextView;
    private TextView complaintStatusTextView;
    private ScrollView parentLayout;
    private TextView compCatTitleTV;
    private TextView no_data_text_view;
    private CardView header_cardView;
    private SimpleDraweeView compSDV;
    private RelativeLayout compImageLayout;
    private final int IMAGE_PICK_REQUEST_CODE = 1001;
    private SpotsDialog spotsDialog;
    private String uniqueId;
    private int userId;
    private String statusTitle;
    private String statusColorCode;
    private String strmodelId;
    private Bitmap bitmap;
    Uri _mainUri, resultUri;
    File image1;
    private TextView count_status_text_view, totalCount_text_view;
    private int nextPageCount ;
    boolean is_geofence=false;
    boolean isCameraMandatory = false;
    String module_id = "";
    EditText landmark_et;
    LinearLayout landmark_ll;
    TextView complaint_dept_name;
    String air_pollution_status="";
    String image_path = "";
    LinearLayout before_LL;
    LinearLayout after_LL;
    CardView complaintImageCardView;
    TextView complaintImageTv;
    private ImageView beforeIV,afterIV;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (this.getArguments() != null) {
            /**
             * Value of {@link Constants.KEY_COMPLAINT_PAGE_TYPE} initialized from {@link ViewMyComplaintsFragment}
             */
            PAGE_TYPE = this.getArguments().getString(Constants.KEY_COMPLAINT_PAGE_TYPE);
            statusTitle = this.getArguments().getString(Constants.KEY_STATUS_TITLE);
            statusColorCode = this.getArguments().getString(Constants.KEY_STATUS_COLOR_CODE);
            strmodelId = this.getArguments().getString(Constants.KEY_MODULE_ID);
            nextPageCount = this.getArguments().getInt("nextcount");

            if (Constants.KEY_MODULE_NAME.equals(strmodelId)) {
                allComplaintsModel = (ViewAllComplaintsModel) getArguments().getSerializable(Constants.KEY_COMPLAINT_DATA);

            } else {
                complaintsModel = (AllComplaintsModel) getArguments().getSerializable(Constants.KEY_COMPLAINT_DATA);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {

            mView = inflater.inflate(R.layout.fragment_new_complaint, container, false);
            activity = getActivity();
            appBarLayout = mView.findViewById(R.id.app_bar_layout);
            mandatoryFieldsTextView = mView.findViewById(R.id.mandatory_fields_text_view);
            durationTextView = mView.findViewById(R.id.complaint_duration_text_view);
            complaintNumTextView = mView.findViewById(R.id.complaint_num_text_view);
            complaintStatusTextView = mView.findViewById(R.id.comp_status_text_view);
            parentLayout = mView.findViewById(R.id.main_scroll_view);
            compImageLayout = mView.findViewById(R.id.complaint_image_view_layout);
            compIV = mView.findViewById(R.id.complaint_image_view);
            compSDV = mView.findViewById(R.id.complaint_simple_drawee_view);
            captureCompIB = mView.findViewById(R.id.capture_image_button);
            imageCaptureTV = mView.findViewById(R.id.image_capture_text_view);
            compCatTitleTV = mView.findViewById(R.id.complaint_cat_title_text_view);
            compCatTV = mView.findViewById(R.id.complaint_category_text_view);
            compDescET = mView.findViewById(R.id.complaint_description_edit_text);
            compAddET = mView.findViewById(R.id.complaint_address_edit_text);
            submitCompBtn = mView.findViewById(R.id.submit_complaint_button);
            vote_button = mView.findViewById(R.id.vote_button);
            count_status_text_view = mView.findViewById(R.id.count_status_text_view);
            totalCount_text_view = mView.findViewById(R.id.totalCount_text_view);


            beforeTextView = mView.findViewById(R.id.before_textView);
            afterTextView = mView.findViewById(R.id.after_textView);
            before_LL = mView.findViewById(R.id.before_linearLayout);
            beforeIV = mView.findViewById(R.id.before_image);
            afterIV = mView.findViewById(R.id.after_image);
            after_LL = mView.findViewById(R.id.after_linearLayout);
            complaintImageCardView = mView.findViewById(R.id.complaint_image_cardView);
            complaintImageTv = mView.findViewById(R.id.complaint_image_TV);

            module_id = Constants.getPrefrence(requireContext(),"complaint_module_id");
            landmark_ll = mView.findViewById(R.id.landmark_ll);
            landmark_et = mView.findViewById(R.id.landmark_edit_text);
            complaint_dept_name = mView.findViewById(R.id.complaint_dept_name);
            if (module_id.equals("68")){
                landmark_ll.setVisibility(View.VISIBLE);
                air_pollution_status = "1";
                imageCaptureTV.setText("Capture an Image");
            } else {
                landmark_ll.setVisibility(View.GONE);
            }

            geoAddressTextView = mView.findViewById(R.id.geo_address_text_view);
            mapFragment = (MapFragment) activity.getFragmentManager().findFragmentById(R.id.map_fragment);


            userId = Utils.getUserDetails(activity).getCivilianId();
            compCatList = new ArrayList<>();
            airPollutionComCatlist = new ArrayList<>();
            uniqueId = Utils.generateUniqueId(userId);

            spotsDialog = new SpotsDialog(activity, getResources().getString(R.string.please_wait_dialog_text), R.style.CustomSpotsDialogStyle);
            spotsDialog.setCancelable(false);
            //  getStatus();

            Constants.setPreferenceStringData(getActivity(), "inOrOut_method", "false");

            /**
             * Showing complaint details, checking value {@link PAGE_TYPE},
             * if {@link PAGE_TYPE}={@link Constants.KEY_COMPLAINT_PAGE_TYPE},
             * then it shows complaint details otherwise it show fields to create new complaint.
             */
            if (!PAGE_TYPE.equals(Constants.VALUE_SHOW_COMPLAINT)) {
                Utils.setToolbarTitle(activity, "New Complaint");
                if ((ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        || ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R){
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
                compSDV.setVisibility(View.GONE);
                compImageLayout.setVisibility(View.VISIBLE);
                mapFragment.getMapAsync(this);
            } else {
                if (Constants.KEY_MODULE_NAME.equals(strmodelId)) {
                    getComplaintDetails();
                } else {
                    showComplaintDetails();
                }

            }
            //  getStatus();
            prepareDataList();
            complaintCategoriesOnClick(this);
            getVoteOfComplaint();
//            imageCaptureButtonOnClick();

            // Set up the permission and camera logic
            setupImageCaptureButton();

            startLocationUpdates();
            submitComplaintButtonOnClickListener();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapOnTouchEventListener();

        return mView;
    }

    private void getComplaintDetails() {
//        Utils.setToolbarColor(activity, statusColorCode);
        Utils.setToolbarTitle(activity, "Details");
        mandatoryFieldsTextView.setVisibility(View.GONE);
        count_status_text_view.setVisibility(View.GONE);
        appBarLayout.setVisibility(View.VISIBLE);

        if (statusTitle.equals("Closed")) {
            count_status_text_view.setVisibility(View.GONE);
            totalCount_text_view.setVisibility(View.GONE);
            vote_button.setVisibility(View.GONE);
        } else {
            if (allComplaintsModel.isUpVotesDone()) {
                vote_button.setVisibility(View.GONE);
                count_status_text_view.setVisibility(View.VISIBLE);
                totalCount_text_view.setVisibility(View.GONE);
                count_status_text_view.setText("Vote Count: " + allComplaintsModel.getUpVotesCount().toString());
            } else {
                vote_button.setVisibility(View.VISIBLE);
                if (allComplaintsModel.getUpVotesCount() == 0) {
                    totalCount_text_view.setVisibility(View.GONE);
                } else {
                    totalCount_text_view.setVisibility(View.VISIBLE);
                    totalCount_text_view.setText("Vote Count: " + allComplaintsModel.getUpVotesCount().toString());
                }
            }
        }


        complaintNumTextView.setText("#" + allComplaintsModel.getComplaintNo());
        durationTextView.setText(Utils.getComplaintDuration(allComplaintsModel.getDurationDays(),
                allComplaintsModel.getDurationHours(), allComplaintsModel.getDurationMinutes()));

        complaintStatusTextView.setText(statusTitle);
        complaintStatusTextView.setBackgroundColor(Color.parseColor(statusColorCode));
        compImageLayout.setVisibility(View.GONE);
        captureCompIB.setVisibility(View.GONE);
        imageCaptureTV.setVisibility(View.GONE);
        compCatTitleTV.setText("Complaint Category");
        compCatTV.setEnabled(false);
        compCatTV.setText(allComplaintsModel.getComplaintType());
        // for showing complete description
        compDescET.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        compDescET.setFocusable(false);
        compDescET.setText(allComplaintsModel.getCompDescription());

        // for showing complete address
        compAddET.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        compAddET.setFocusable(false);


        geoAddressTextView.setText(allComplaintsModel.getGeoAddress());
        submitCompBtn.setVisibility(View.GONE);
        compImageLayout.setVisibility(View.GONE);
        compSDV.setVisibility(View.VISIBLE);
        final String imagePath = Constants.IMAGE_URL + allComplaintsModel.getComplaintImage();
        afterIV.setImageURI(Uri.parse(imagePath));
        afterIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZoomableFragment zoomableFragment = new ZoomableFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString(Constants.KEY_IMAGE_PATH, imagePath);
                zoomableFragment.setArguments(mBundle);
                Utils.addFragment(getFragmentManager(), zoomableFragment);
            }
        });
        Log.v(TAG, imagePath);
        mapFragment.getMapAsync(this);
    }

    /* ====================== for showing complaint details [re-usability of this fragment] ====================== */
    private void showComplaintDetails() {
//        Utils.setToolbarColor(activity, statusColorCode);
        Utils.setToolbarTitle(activity, "Details");
        mandatoryFieldsTextView.setVisibility(View.GONE);
        count_status_text_view.setVisibility(View.GONE);
        appBarLayout.setVisibility(View.VISIBLE);

        if (statusTitle.equals("Closed")) {
            count_status_text_view.setVisibility(View.GONE);
            totalCount_text_view.setVisibility(View.GONE);
            vote_button.setVisibility(View.GONE);
        } else {
            if (!module_id.equals("68")) {
                if (complaintsModel.isUpVotesDone()) {
                    vote_button.setVisibility(View.GONE);
                    count_status_text_view.setVisibility(View.VISIBLE);
                    totalCount_text_view.setVisibility(View.GONE);
                    count_status_text_view.setText("Vote Count: " + complaintsModel.getUpVotesCount().toString());
                } else {
                    vote_button.setVisibility(View.VISIBLE);
                    if (complaintsModel.getUpVotesCount() == 0) {
                        totalCount_text_view.setVisibility(View.GONE);
                    } else {
                        totalCount_text_view.setVisibility(View.VISIBLE);
                        totalCount_text_view.setText("Vote Count: " + complaintsModel.getUpVotesCount().toString());
                    }
                }
            } else {
                vote_button.setVisibility(View.GONE);
                count_status_text_view.setVisibility(View.GONE);
                totalCount_text_view.setVisibility(View.GONE);
                landmark_et.setEnabled(false);
            }
        }

        complaintNumTextView.setText("#" + complaintsModel.getCompNum());
        durationTextView.setText(Utils.getComplaintDuration(complaintsModel.getDays(),
                complaintsModel.getHours(), complaintsModel.getMinutes()));
        complaintStatusTextView.setText(statusTitle);
        complaintStatusTextView.setBackgroundColor(Color.parseColor(statusColorCode));
        compImageLayout.setVisibility(View.GONE);
        captureCompIB.setVisibility(View.GONE);
        imageCaptureTV.setVisibility(View.GONE);
        compCatTitleTV.setText("Complaint Category");
        compCatTV.setEnabled(false);
        compCatTV.setText(complaintsModel.getCompType());
        // for showing complete description
        compDescET.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        compDescET.setFocusable(false);
        compDescET.setText(complaintsModel.getCompDesc());

        // for showing complete address
        compAddET.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        compAddET.setFocusable(false);
        compAddET.setText(complaintsModel.getCompAdd());
        geoAddressTextView.setText(complaintsModel.getCompGeoAdd());
        submitCompBtn.setVisibility(View.GONE);
        compImageLayout.setVisibility(View.GONE);
//        compSDV.setVisibility(View.VISIBLE);
        final String imagePath = Constants.IMAGE_URL + complaintsModel.getCompImageURL();
        final String firstImagePath = Constants.IMAGE_URL + complaintsModel.getFirstImageComplaint();

        if (imagePath.equals(firstImagePath)) {
            complaintImageTv.setVisibility(View.VISIBLE);
            before_LL.setVisibility(View.GONE);
            after_LL.setVisibility(View.GONE);
            compSDV.setVisibility(View.VISIBLE);

            Glide.with(compSDV)
                    .load(firstImagePath)
                    .error(R.drawable.ic_error)
                    .fitCenter()
                    .into(compSDV);
        } else {
            before_LL.setVisibility(View.VISIBLE);
            after_LL.setVisibility(View.VISIBLE);
            complaintImageTv.setVisibility(View.GONE);
            complaintImageCardView.setVisibility(View.GONE);
        }

       Glide.with(beforeIV)
                .load(firstImagePath)
                .error(R.drawable.ic_error)
                .into(beforeIV);

        Glide.with(afterIV)
                .load(imagePath)
                .error(R.drawable.ic_error)
                .into(afterIV);

        // beforeIV.setImageURI(Uri.parse(imagePath));

        compSDV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZoomableFragment zoomableFragment = new ZoomableFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString(Constants.KEY_IMAGE_PATH, firstImagePath);
                zoomableFragment.setArguments(mBundle);
                Utils.addFragment(getFragmentManager(), zoomableFragment);
            }
        });

        beforeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZoomableFragment zoomableFragment = new ZoomableFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString(Constants.KEY_IMAGE_PATH, firstImagePath);
                zoomableFragment.setArguments(mBundle);
                Utils.addFragment(getFragmentManager(), zoomableFragment);
            }
        });

        afterIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZoomableFragment zoomableFragment = new ZoomableFragment();
                Bundle mBundle = new Bundle();
                mBundle.putString(Constants.KEY_IMAGE_PATH, imagePath);
                zoomableFragment.setArguments(mBundle);
                Utils.addFragment(getFragmentManager(), zoomableFragment);
            }
        });
        Log.v(TAG, imagePath);
        mapFragment.getMapAsync(this);
    }
    /* ================================================================================================= */


    /* ====================== Submit complaint button ====================== */
    private void submitComplaintButtonOnClickListener() {
        submitCompBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (compIV.getDrawable() == null) {
                    Utils.showSnackBar(activity, parentLayout, "Complaint Image is compulsory !", false);
                } else if (compCatTV.getText().toString().isEmpty()) {
                    Utils.showSnackBar(activity, parentLayout, "Category must be filled !", false);
                } else if (compDescET.getText().toString().isEmpty()) {
                    Utils.showSnackBar(activity, parentLayout, "Complaint Description must be filled !", false);
                } else if (compAddET.getText().toString().isEmpty()) {
                    Utils.showSnackBar(activity, parentLayout, "Complaint Address must be filled !", false);
                } else {
                    if (ConnectivityReceiver.isConnected()) {
                        spotsDialog.show();
                        if (userLat == 0.0 || userLong == 0.0) {
                            Utils.showSnackBar(activity, parentLayout, " Lat & Long not found.. Please set mobile GPS at High Accuracy !", false);
                        } else {
                            requestToSubmitComplaint();
                        }
                    } else {
                        Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                Constants.MESSAGE_CHECK_INTERNET, false);
                    }
                }
            }
        });
    }

    /* ====================== Complaint Categories onClickListener ====================== */
    private void complaintCategoriesOnClick(final GetOnItemClick getOnItemClick) {
        compCatTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createComplaintCategoryDialog(getOnItemClick);
            }
        });
    }
    /* ================================================================================================= */

    private void getVoteOfComplaint() {
        vote_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getupVotesComplaints();

            }
        });
    }

    /* ====================== Select Complaint Categories onClickListener ====================== */
   //New code
    private void setupImageCaptureButton() {
        captureCompIB.setOnClickListener(v -> {
            // Show the dialog to choose image source
            final CharSequence[] options = {"Choose from Gallery", "Camera", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Select Image!")
                    .setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Choose from Gallery")) {
                                // Open gallery
                                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, 101);

                            } else if (options[item].equals("Camera")) {
                                try {
                                    Intent in = new Intent(getContext(), CustomCameraXActivity.class);
                                    in.putExtra("changeCamera","both");
                                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivityForResult(in, 100);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } else if (options[item].equals("Cancel")) {
                                dialog.dismiss();
                            }
                        }
                    });
            builder.show();
        });
    }

    private void prepareDataList() {
        compCatList = new ArrayList<>();
        compCatList.clear();
        airPollutionComCatlist = new ArrayList<>();
        airPollutionComCatlist.clear();
        getComplaintCategories();
    }

    /* ====================== Request method to get complaint categories ====================== */
    private void getComplaintCategories() {
        ApiInterface mApiInterface = Utils.getInterfaceService();
        Log.v(TAG, Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity));
        Call<JsonObject> mService = null;
        if (module_id.equals("68")){
            mService = mApiInterface.getComplaintAirPollution(Constants.HEADER_TOKEN_BEARER
                            + Utils.getUserToken(activity),
                    Constants.HEADER_ACCEPT);
        } else {
            mService = mApiInterface.getComplaintCategories(Constants.HEADER_TOKEN_BEARER
                            + Utils.getUserToken(activity),
                    Constants.HEADER_ACCEPT);
        }
        String token=Constants.HEADER_TOKEN_BEARER+Utils.getUserToken(activity);
        mService.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (activity != null && isAdded()) {
                        if (response.body() != null) {
                            JsonObject jsonObject = response.body();
                            if (jsonObject.get("response").getAsBoolean()) {
                                JsonArray departmentJsonArray = jsonObject.getAsJsonArray("data");
                                if (departmentJsonArray.size() > 0) {
                                    for (int i = 0; i < departmentJsonArray.size(); i++) {
                                        if (module_id.equals("69")){
                                            String dept_id= departmentJsonArray.get(i).getAsJsonObject().get("dept_id").isJsonNull() ? "" : departmentJsonArray.get(i).getAsJsonObject().get("dept_id").getAsString();
                                            if (dept_id.equals("70")){
                                                ComplaintCategoryModel categoryModel = new GsonBuilder().create()
                                                        .fromJson(departmentJsonArray.get(i), ComplaintCategoryModel.class);
                                                compCatList.add(categoryModel);
                                            }
                                        }else {
                                            ComplaintCategoryModel categoryModel = new GsonBuilder().create()
                                                    .fromJson(departmentJsonArray.get(i), ComplaintCategoryModel.class);
                                            compCatList.add(categoryModel);
                                        }
                                    }
                                    Log.v(TAG, compCatList.toString());
                                    spotsDialog.dismiss();
                                }
                                if (module_id.equals("68") || !module_id.equals("69")) {
                                    JsonArray airPolCatArray = jsonObject.getAsJsonArray("comp_type_another");
                                    if (airPolCatArray.size() > 0) {
                                        for (int i = 0; i < airPolCatArray.size(); i++) {
                                            String id = airPolCatArray.get(i).getAsJsonObject().get("id").isJsonNull() ? "" : airPolCatArray.get(i).getAsJsonObject().get("id").getAsString();
                                            String name = airPolCatArray.get(i).getAsJsonObject().get("name").isJsonNull() ? "" : airPolCatArray.get(i).getAsJsonObject().get("name").getAsString();
                                            String icon_unicode = airPolCatArray.get(i).getAsJsonObject().get("icon_unicode").isJsonNull() ? "" : airPolCatArray.get(i).getAsJsonObject().get("icon_unicode").getAsString();
                                            String dept_name = airPolCatArray.get(i).getAsJsonObject().get("dept_name").isJsonNull() ? "" : airPolCatArray.get(i).getAsJsonObject().get("dept_name").getAsString();
                                            String dept_id = airPolCatArray.get(i).getAsJsonObject().get("dept_id").isJsonNull() ? "" : airPolCatArray.get(i).getAsJsonObject().get("dept_id").getAsString();
                                            ComplaintCatModel complaintCatModel = new ComplaintCatModel(id, name, icon_unicode, dept_name, dept_id);
                                            airPollutionComCatlist.add(complaintCatModel);
                                        }
                                    }
                                }
                            } else {
                                refreshCategoryList = true;
                                spotsDialog.dismiss();
                                Utils.showSnackBar(activity, parentLayout,
                                        jsonObject.get("message").getAsString().trim(), false);
                            }
                        } else {
                            refreshCategoryList = true;
                            spotsDialog.dismiss();
                            Utils.showSnackBar(activity, parentLayout,
                                    Constants.MESSAGE_NO_DATA_FOUND, false);
                        }
                    }
                } catch (Exception e) {
                    refreshCategoryList = true;
                    spotsDialog.dismiss();
                    e.printStackTrace();
                    Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (activity != null && isAdded()) {
                    spotsDialog.dismiss();
                    refreshCategoryList = true;
                    call.cancel();
                    t.printStackTrace();
                    Utils.showSnackBar(activity, parentLayout, Utils.failureMessage(t), false);
                }
            }
        });
    }
    private void getCameraType() {
        ApiInterface mApiInterface = Utils.getInterfaceService();
        int userId = Utils.getUserDetails(activity).getCivilianId();
        Log.v(TAG, Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity));
        Call<JsonObject> mService = mApiInterface.getCameraType(Constants.HEADER_TOKEN_BEARER
                        + Utils.getUserToken(activity),
                Constants.HEADER_ACCEPT,userId);
        String token=Constants.HEADER_TOKEN_BEARER+Utils.getUserToken(activity);
        mService.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    String message = "";
                    if (activity != null && isAdded()) {
                        if (response.body() != null) {
                            JsonObject jsonObject = response.body();
                            if (jsonObject.get("response").getAsBoolean()) {
                                isCameraMandatory = true;
                                spotsDialog.dismiss();
                            } else {

                            }
                        } else {
                            refreshCategoryList = true;
                            spotsDialog.dismiss();
                            Utils.showSnackBar(activity, parentLayout,
                                    Constants.MESSAGE_NO_DATA_FOUND, false);
                        }
                    }
                } catch (Exception e) {
                    refreshCategoryList = true;
                    spotsDialog.dismiss();
                    e.printStackTrace();
                    Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (activity != null && isAdded()) {
                    spotsDialog.dismiss();
                    refreshCategoryList = true;
                    call.cancel();
                    t.printStackTrace();
                    Utils.showSnackBar(activity, parentLayout, Utils.failureMessage(t), false);
                }
            }
        });
    }

    private void getComplaintLimit() {
        ApiInterface mApiInterface = Utils.getInterfaceService();
        int userId = Utils.getUserDetails(activity).getCivilianId();
        Log.v(TAG, Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity));
        Call<JsonObject> mService = mApiInterface.getComplaintCount(Constants.HEADER_TOKEN_BEARER
                        + Utils.getUserToken(activity),
                Constants.HEADER_ACCEPT,userId);
        String token=Constants.HEADER_TOKEN_BEARER+Utils.getUserToken(activity);
        mService.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    String message = "";
                    if (activity != null && isAdded()) {
                        if (response.body() != null) {
                            JsonObject jsonObject = response.body();
                            if (jsonObject.get("response").getAsBoolean()) {
                                spotsDialog.dismiss();
                            } else {

                                message =   jsonObject.get("message").getAsString().trim();

                                spotsDialog.dismiss();

                                showLimitDialoge(message);

                            }
                        } else {
                            refreshCategoryList = true;
                            spotsDialog.dismiss();
                            Utils.showSnackBar(activity, parentLayout,
                                    Constants.MESSAGE_NO_DATA_FOUND, false);
                        }
                    }
                } catch (Exception e) {
                    refreshCategoryList = true;
                    spotsDialog.dismiss();
                    e.printStackTrace();
                    Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (activity != null && isAdded()) {
                    spotsDialog.dismiss();
                    refreshCategoryList = true;
                    call.cancel();
                    t.printStackTrace();
                    Utils.showSnackBar(activity, parentLayout, Utils.failureMessage(t), false);
                }
            }
        });
    }


    public void showLimitDialoge(String message){
        final Dialog countDialoge = new Dialog(getContext());
        countDialoge.setContentView(R.layout.limit_dialogue);
        countDialoge.setCancelable(false);
        countDialoge.setCanceledOnTouchOutside(false);
        TextView dialog_title_message =countDialoge.findViewById(R.id.dialog_title_message);
        TextView dialog_title =countDialoge.findViewById(R.id.dialog_title);
        ImageView imageView = countDialoge.findViewById(R.id.dialog_title_icon);





        Button ok_button =countDialoge.findViewById(R.id.ok_button);
        ok_button.setText("OK");
        dialog_title.setText("Alert");
        dialog_title_message.setText(message);


//        cancel_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                countDialoge.dismiss();
//            }
//        });

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                countDialoge.dismiss();

                getFragmentManager().popBackStackImmediate();

            }
        });

        countDialoge.show();

    }
    /* ================================================================================================= */

    private void getupVotesComplaints() {
        spotsDialog.show();
        String user_id = "";
        if (Constants.KEY_MODULE_NAME.equals(strmodelId)) {
            user_id = allComplaintsModel.getComplaintNo();
        } else {

            user_id = complaintsModel.getCompNum();
        }

        ApiInterface mApiInterface = Utils.getInterfaceService();
        int userId = Utils.getUserDetails(activity).getCivilianId();
        Log.v(TAG, Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity));
        Call<JsonObject> mService = mApiInterface.getupVotesComplaints(Constants.HEADER_TOKEN_BEARER
                        + Utils.getUserToken(activity),
                Constants.HEADER_ACCEPT, user_id, userId);
        mService.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (activity != null && isAdded()) {
                        if (response.body() != null) {
                            JsonObject jsonObject = response.body();
                            if (jsonObject.get("response").getAsBoolean()) {
                                spotsDialog.dismiss();
                                Constants.customToast(activity, jsonObject.get("message").getAsString(), 1);
                                vote_button.setEnabled(false);
                            } else {
                                refreshCategoryList = true;
                                spotsDialog.dismiss();
                                Utils.showSnackBar(activity, parentLayout,
                                        jsonObject.get("message").getAsString().trim(), false);
                            }
                        } else {
                            refreshCategoryList = true;
                            spotsDialog.dismiss();
                            Utils.showSnackBar(activity, parentLayout,
                                    Constants.MESSAGE_NO_DATA_FOUND, false);
                        }
                    }
                } catch (Exception e) {
                    refreshCategoryList = true;
                    spotsDialog.dismiss();
                    e.printStackTrace();
                    Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (activity != null && isAdded()) {
                    spotsDialog.dismiss();
                    refreshCategoryList = true;
                    call.cancel();
                    t.printStackTrace();
                    Utils.showSnackBar(activity, parentLayout, Utils.failureMessage(t), false);
                }
            }
        });
    }


    /* ====================== Creating compCatDialog for showing complaint categories ====================== */
    private void createComplaintCategoryDialog(GetOnItemClick getOnItemClick) {
        compCatDialog = new Dialog(activity, R.style.dialogSlideAnimation);
        compCatDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        compCatDialog.setContentView(R.layout.layout_comp_cat_dialog);
        compCatDialog.setCancelable(false);

        expLV = compCatDialog.findViewById(R.id.expandable_list_view);
        apCatLv = compCatDialog.findViewById(R.id.air_pollution_cat_ll);
        no_data_text_view = compCatDialog.findViewById(R.id.no_data_tv);
        header_cardView = compCatDialog.findViewById(R.id.header_cardView);

        if (module_id.equals("68")){
            expLV.setVisibility(View.GONE);
            apCatLv.setVisibility(View.VISIBLE);
            setAirPollutionCatViewAdapter(getOnItemClick);
        } else if (!module_id.equals("69")){
            expLV.setVisibility(View.GONE);
            apCatLv.setVisibility(View.VISIBLE);
            setAirPollutionCatViewAdapter(getOnItemClick);
        }else {
            expLV.setVisibility(View.VISIBLE);
            apCatLv.setVisibility(View.GONE);
            expLV.setAdapter(new CompCatExpListAdapter());
            setCompCatListViewAdapter();
        }

//        expLV.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                if (compCatList.get(groupPosition).getSubCategoryModelList().isEmpty()) {
//                    compCatTV.setText(compCatList.get(groupPosition).getDepartmentName());
//                    deptId = compCatList.get(groupPosition).getDepartmentId();
//                    compCatDialog.dismiss();
//                }
//                return false;
//            }
//        });
        expLV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                compCatTV.setText(compCatList.get(groupPosition).getDepartmentName()
                        + " - " + compCatList.get(groupPosition).getSubCategoryModelList().get(childPosition).getSubCategoryName());
                deptId = compCatList.get(groupPosition).getDepartmentId();
                subDeptId = compCatList.get(groupPosition).getSubCategoryModelList().get(childPosition).getSubCategoryId();
                compCatDialog.dismiss();
                return false;
            }
        });

        compCatDialog.findViewById(R.id.cancel_dialog_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compCatDialog.dismiss();
            }
        });

        compCatDialog.show();
    }
    /* ================================================================================================= */
    private void setAirPollutionCatViewAdapter(GetOnItemClick getOnItemClick) {
        if (airPollutionComCatlist.isEmpty()){
            no_data_text_view.setVisibility(View.VISIBLE);
            header_cardView.setVisibility(View.GONE);
            apCatLv.setVisibility(View.GONE);
        } else {
            AirPolCompCatListAdapter adapter = new AirPolCompCatListAdapter(activity,airPollutionComCatlist,getOnItemClick);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
            apCatLv.setLayoutManager(linearLayoutManager);
            apCatLv.setAdapter(adapter);
            refreshCategoryList = false;
        }
    }
    /* ================================================================================================= */
    @Override
    public void onGetPosItemclick(int pos) {
        compCatTV.setText(airPollutionComCatlist.get(pos).getName());
        deptId = Integer.parseInt(airPollutionComCatlist.get(pos).getDept_id());
        subDeptId = airPollutionComCatlist.get(pos).getId();
        complaint_dept_name.setVisibility(View.VISIBLE);
        complaint_dept_name.setText(airPollutionComCatlist.get(pos).getDept_name());
        compCatDialog.dismiss();
    }
    /* ====================== Setting adapter to Exp. ListView ====================== */
    private void setCompCatListViewAdapter() {
        if (compCatList.isEmpty()) {
            no_data_text_view.setVisibility(View.VISIBLE);
            expLV.setVisibility(View.GONE);
            header_cardView.setVisibility(View.GONE);
        } else {

            CompCatExpListAdapter adapter = new CompCatExpListAdapter(activity, compCatList);
            expLV.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            refreshCategoryList = false;
        }
    }
    /* ================================================================================================= */


    private void mapOnTouchEventListener() {
        mView.findViewById(R.id.transparent_image).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        parentLayout.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        parentLayout.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        parentLayout.requestDisallowInterceptTouchEvent(true);
                        return false;
                    default:
                        return true;
                }
            }
        });
    }

    private Intent createChooserIntent(Uri uri) {
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
                activity.grantUriPermission(res.activityInfo.packageName, outputFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
//        capturedFile = new File(activity.getExternalCacheDir(), Constants.FILE_NAME_PNG);
//        for one plus device
        capturedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+Constants.FILE_NAME_JPG);
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
        if (requestCode == 100) {
            Bundle bundle = data.getExtras();
            image_path = bundle.getString("MCF_Citizen_Image");
            Log.d("IMageCapture", image_path);
            compIV.setImageURI(Uri.parse(image_path));
            imageCaptureTV.setVisibility(View.GONE);
            File imgFile = new File(image_path);
            if (imgFile.exists()) {
                image1 = imgFile;
            }
        }else if (requestCode == 101) {
            Bitmap bm = null;
            if (data != null) {
                if (Build.VERSION.SDK_INT < 29) {
                    try {
                        bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        bm = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContext().getContentResolver(), data.getData()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if (bm != null) {
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                }

                File filepath = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "BaseProductAllImage");
                if (!filepath.exists())
                    filepath.mkdirs();
                String unique_id = Constants.generateUniqueId(Utils.getUserDetails(activity).getCivilianId());

                File file = new File(filepath, "/" + unique_id + ".png");
                image1 = file;
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(file);
                    if (bm != null) {
                        bm.compress(Bitmap.CompressFormat.PNG, 10, outputStream);
                    }
                    image_path = filepath.getAbsolutePath() + File.separator
                            + unique_id + ".png";

                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                compIV.setImageBitmap(bm);
                imageCaptureTV.setVisibility(View.GONE);
            }
        }

        try {
                switch (requestCode) {
                case IMAGE_PICK_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
                        if (data != null) {
                            _mainUri = data.getData();
                        }
                        if (data == null || _mainUri == null) {
                            _mainUri = FileUtils.getUri(image1);
                        }
                        if (_mainUri != null) {
                            // start cropping activity for pre-acquired image saved on the device
                            if (!module_id.equals("68")) {
                                launchImageCropper(_mainUri);
                            } else {
                                String path = FileUtils.getPath(activity, _mainUri);
                                if (path != null) {
                                    image1 = FileUtils.getFile(activity, _mainUri);
                                    double size = Utils.getFileSize(image1);
                                    Log.v(TAG, "file_size(MB): " + size
                                            + "\n file_name: " + image1.getName()
                                            + "\n file_path: " + path);

                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), _mainUri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if (imagePath == null) {
                                        compIV.setImageBitmap(bitmap);
                                        imageCaptureTV.setVisibility(View.GONE);
                                        imagePath = FileUtils.getPath(activity, _mainUri);
                                    } else {
                                        compIV.setImageBitmap(bitmap);
                                        imageCaptureTV.setVisibility(View.GONE);
                                        imagePath = FileUtils.getPath(activity, _mainUri);
                                    }
                                }
                            }
                        } else {
                            Utils.showSnackBar(activity, parentLayout, Constants.MESSAGE_SOMETHING_WENT_WRONG, false);

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
                            image1 = FileUtils.getFile(activity, resultUri);
                            double size = Utils.getFileSize(image1);
                            Log.v(TAG, "file_size(MB): " + size
                                    + "\n file_name: " + image1.getName()
                                    + "\n file_path: " + path);

                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), resultUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (imagePath == null) {
                                compIV.setImageBitmap(bitmap);
                                imageCaptureTV.setVisibility(View.GONE);
                                imagePath = FileUtils.getPath(activity, resultUri);
                            } else {
                                compIV.setImageBitmap(bitmap);
                                imageCaptureTV.setVisibility(View.GONE);
                                imagePath = FileUtils.getPath(activity, resultUri);
                            }
                        }
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        result.getError().printStackTrace();
                        Utils.showSnackBar(activity, parentLayout, Constants.MESSAGE_SOMETHING_WENT_WRONG, false);

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
/*    public static String getPath( Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = uri.getPath();
        }
        return result;
    }*/

    /* ====================== Request method for submit complaints ====================== */
    private void requestToSubmitComplaint() {
        try {
            String landmark = landmark_et.getText().toString();
            ApiInterface mApiInterface = Utils.getInterfaceService();
            _shrinkImageFile = new File(image1.getAbsolutePath());

            Bitmap imageBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(_shrinkImageFile.getPath()), 700, 700, false);
            try {
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 30, new FileOutputStream(_shrinkImageFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Call<JsonObject> mService = mApiInterface.submitComplaint(
                    RequestBody.create(MediaType.parse("multipart/form-data"), compDescET.getText().toString()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userLat)),
                    RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userLong)),
                    RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(deptId)),
                    RequestBody.create(MediaType.parse("multipart/form-data"), subDeptId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), uniqueId),
                    RequestBody.create(MediaType.parse("multipart/form-data"), geoAddressTextView.getText().toString().trim()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), compAddET.getText().toString().trim()),
                    RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Constants.APP_TYPE)), String.valueOf(userId),
                    MultipartBody.Part.createFormData("image_source", _shrinkImageFile.getName(),RequestBody.create(MediaType.parse("multipart/form-data"), _shrinkImageFile)),
                    RequestBody.create(MediaType.parse("multipart/form-data"), landmark),
                    RequestBody.create(MediaType.parse("multipart/form-data"), air_pollution_status));

            mService.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {
                        if (response.body() != null) {
                            JsonObject jsonObject = response.body();
                            if (jsonObject.get("response").getAsBoolean()) {
                                String complaintNum = response.body().get("complaint_no").getAsString();
                                showSuccessDialog(complaintNum);
                                spotsDialog.dismiss();
                                Log.v(TAG, "Complaints submitted successfully !!!"
                                        + "\nComplaint No.===>" + complaintNum);
                            } else {
                                spotsDialog.dismiss();
                                String message = jsonObject.get("message").getAsString().trim();
                                Log.v(TAG, "Message===>" + message);
                                Utils.showSnackBar(activity, parentLayout, message, false);
                            }
                        } else {
                            spotsDialog.dismiss();
                            Utils.showSnackBar(activity, parentLayout, Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
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
                    Utils.showSnackBar(activity, parentLayout, Utils.failureMessage(t), false);
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    /* ================================================================================================= */

    private void inOrOut() {
        ApiInterface mApiInterface = Utils.getInterfaceService();
        if(userLat==0.0 && userLong==0.0) {

        }else {

            Call<JsonObject> mService = mApiInterface.complaintInOrOut(userLat,userLong,  userId,
//            Call<JsonObject> mService = mApiInterface.complaintInOrOut(28.6024564,77.067899,  userId,
                    Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity),
                    Constants.HEADER_ACCEPT);
            String token = Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity);

            mService.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    JsonObject mAttendanceObject = response.body();
                    if (mAttendanceObject != null) {
                        spotsDialog.dismiss();
                        if (mAttendanceObject.get("response").getAsBoolean()) {
                            spotsDialog.dismiss();
                            is_geofence = true;
                            String showDialMethod = Constants.getPrefrence(getActivity(), "inOrOut_method");
                            if(showDialMethod.equals("false")) {
                                showoutofcircleDialog();
                                Constants.setPreferenceStringData(getActivity(), "inOrOut_method", "true");
                            }
                        } else {

                            spotsDialog.dismiss();

                        }
                    } else {
                        spotsDialog.dismiss();
                        Constants.customToast(
                                activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 1);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    call.cancel();
                    spotsDialog.dismiss();
                    t.printStackTrace();
                    if (Constants.failureMessage(t).equals(Constants.MESSAGE_CHECK_INTERNET)) {
                        Constants.customToast(
                                activity, Constants.MESSAGE_CHECK_INTERNET, 1);
                    } else {
                        Constants.customToast(
                                activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 1);
                    }
                }
            });
        }
    }

    private void showoutofcircleDialog() {
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
        dialogMessage.setText("Not in MCF Area !");
        dialogInfoMessage.setText(Html.fromHtml("You are out of MCF Area.Please register your complaint from MCF Area."));

        OKButton.setText(R.string.ok);
        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getFragmentManager().popBackStack();
                Constants.setPreferenceStringData(getActivity(), "inOrOut_method", "false");

            }
        });

        dialog.show();

    }

    private void showoutofcircleDialog1() {
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
        dialogMessage.setText("Please fill feedback first !");
        dialogInfoMessage.setText(Html.fromHtml("You didn't fill your feedback for previous complaints, please fill feedback first."));

        OKButton.setText(R.string.ok);
        // cancelButton.setText("Cancel");
        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // getFragmentManager().popBackStack();
                //  Utils.addFragment(getFragmentManager(), new ViewMyComplaintsFragment());

                ViewMyComplaintsFragment complaintFragment = new ViewMyComplaintsFragment();
                if (getFragmentManager() != null) {
                    Utils.changeFragment(getFragmentManager(),complaintFragment);
                }

                //Constants.setPreferenceStringData(getActivity(), "inOrOut_method", "false");

            }
        });
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                dialog.dismiss();
//                getFragmentManager().popBackStackImmediate();
//
//            }
//        });



        dialog.show();

    }


    private void showSuccessDialog(String complaintNum) {
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
        dialogMessage.setText("Complaint Submitted Successfully !");
        dialogInfoMessage.setText(Html.fromHtml("Please use this Complaint <big>#" + complaintNum
                + "</big> for further assistance."));

        OKButton.setText(R.string.ok);
        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getFragmentManager().popBackStackImmediate();
            }
        });

        dialog.show();

        showNotification("Complaint No.: " + complaintNum,
                "Your complaint submitted successfully !");
    }

    /* ====================== Method to create notification ====================== */
    private void showNotification(String title, String message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat
                .Builder(activity, Constants.NOTIFICATION_CHANNEL_ID);

        Intent intent = new Intent(activity, SlidingDrawerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent;
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.S)
        {
            resultPendingIntent =
                    PendingIntent.getActivity(activity,
                            PendingIntent.FLAG_IMMUTABLE,
                            intent,
                            PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        }
        else
        {
                   resultPendingIntent =  PendingIntent.getActivity(activity,
                            0,
                            intent,
                            PendingIntent.FLAG_CANCEL_CURRENT);

        }
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.addLine(message);
        Notification notification;
        notification = mBuilder
                .setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setStyle(inboxStyle)
//                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.drawable.municipal_corporation_faridabad_logo_256x256)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ndmc_logo_large))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(Constants.NOTIFICATION_ID, notification);
        }
    }
    /* ================================================================================================= */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.clear();
        try {
            if (PAGE_TYPE != null && PAGE_TYPE.equals(Constants.VALUE_SHOW_COMPLAINT)) {

                if (Constants.KEY_MODULE_NAME.equals(strmodelId)) {

                    latLng = new LatLng(Double.parseDouble(allComplaintsModel.getLat()),
                            Double.parseDouble(allComplaintsModel.getLng()));
                    title = allComplaintsModel.getGeoAddress();
                } else {

                    latLng = new LatLng(Double.parseDouble(complaintsModel.getLatitude()),
                            Double.parseDouble(complaintsModel.getLongitude()));
                    title = complaintsModel.getCompGeoAdd();
                }
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .snippet(String.valueOf(latLng.latitude) + ", " + String.valueOf(latLng.longitude))
                        .icon(BitmapDescriptorFactory.fromBitmap(Constants
                                .getBitmapFromVectorDrawable(activity, R.drawable.ic_placeholder))));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                marker.showInfoWindow();
                userLat=latLng.latitude;
                userLong= latLng.longitude;
                inOrOut();

            } else {
                if (location != null) {
                    userLat = location.getLatitude();
                    userLong = location.getLongitude();
                    latLng = new LatLng(userLat, userLong);
                    title = Utils.getGeoLocationAddress(activity, userLat, userLong);
                }

                if (latLng != null) {
                    geoAddressTextView.setText(title);
                    if (module_id.equals("68")){
                        compAddET.setText(title);
                        compAddET.setEnabled(false);
                    }
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(title)
                            .snippet(String.valueOf(latLng.latitude) + ", " + String.valueOf(latLng.longitude))
                            .icon(BitmapDescriptorFactory.fromBitmap(Constants
                                    .getBitmapFromVectorDrawable(activity, R.drawable.ic_placeholder))));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                    marker.showInfoWindow();
                    userLat=latLng.latitude;
                    userLong= latLng.longitude;
                    inOrOut();
                }
            }
        } catch (NullPointerException | IllegalStateException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Provides a simple way of getting a device's location and is well suited for
     * applications that do not require a fine-grained location and that do not need location
     * updates. Gets the best and most recent location currently available, which may be null
     * in rare cases when a location is not available.
     * <p>
     * Note: this method should be called after location permission has been granted.
     */
    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
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

    @Override
    public void onResume() {
        super.onResume();
        if (!PAGE_TYPE.equals(Constants.VALUE_SHOW_COMPLAINT)) {

            if (mGoogleApiClient.isConnected()) {
                startLocationUpdates();
                inOrOut();
                Log.d(TAG, "Location update resumed ..............");

            }
        }
        inOrOut();
        getComplaintLimit();
        getCameraType();
        if (!module_id.equals("68") && !module_id.equals("69")) {
            getStatus();
        }

    }

   /*public void getStatus() {
       String userToken = Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity);
       int userId = Utils.getUserDetails(activity).getCivilianId();
       Log.v(TAG, "USER_TOKEN: " + userToken);
       Log.v(TAG, "USER_ID: " + String.valueOf(userId));

       Utils.getInterfaceService().getStatus( userId)
               .enqueue(new Callback<JsonObject>() {
                   @Override
                   public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                       try {
                           if (activity != null && isAdded()) {
                               if (response.body() != null) {

                                   JsonObject jsonObject = response.body();
                                   if (jsonObject.get("response").getAsBoolean()) {
                                       JsonArray allCompJsonArray = jsonObject.getAsJsonArray("data");
                                   }
                               }
                           }
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                   }

                   @Override
                   public void onFailure(Call<JsonObject> call, Throwable t) {
                       if (activity != null && isAdded()) {
                           spotsDialog.dismiss();
                           call.cancel();
                           t.printStackTrace();

                       }
                   }
               });

   }
*/

    private void getStatus() {

        spotsDialog.show();
        ApiInterface mApiInterface = Utils.getInterfaceService();
        //  if(userLat==0.0 && userLong==0.0) {

        // }else {

        int userId = Utils.getUserDetails(activity).getCivilianId();

        Call<JsonObject> mService = mApiInterface.getStatus(userId);
        String token = Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity);

        mService.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject mAttendanceObject = response.body();
                if (mAttendanceObject != null) {
                    spotsDialog.dismiss();
                    if (mAttendanceObject.get("response").getAsBoolean()) {
                        spotsDialog.dismiss();

                        JsonArray allCompJsonArray = mAttendanceObject.getAsJsonArray("data");

                        if (allCompJsonArray.size() > 0) {
                            String feedback_redirection = allCompJsonArray.get(0).getAsJsonObject().get("feedback_redirection").toString();
                            if(feedback_redirection.equals("1")) {
                                showoutofcircleDialog1();
                                //  Utils.changeFragment(getFragmentManager(),new ComplaintFeedbackFragment());
                            }
                        }
                        spotsDialog.dismiss();
//                            is_geofence = true;
//                            String showDialMethod = Constants.getPrefrence(getActivity(), "inOrOut_method");
//                            if(showDialMethod.equals("false")) {

                        //  Constants.setPreferenceStringData(getActivity(), "inOrOut_method", "true");

                    } else {
                        spotsDialog.dismiss();
                    }

                } else {
                    spotsDialog.dismiss();
                    Constants.customToast(
                            activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 1);
                }
            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
                spotsDialog.dismiss();
                t.printStackTrace();
                if (Constants.failureMessage(t).equals(Constants.MESSAGE_CHECK_INTERNET)) {
                    Constants.customToast(
                            activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 1);
                } else {
                    Constants.customToast(
                            activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 1);
                }
            }
        });
    }

}


