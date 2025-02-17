package com.citizen.fmc.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.citizen.fmc.activity.BannerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.citizen.fmc.R;
import com.citizen.fmc.activity.WebViewActivity;
import com.citizen.fmc.adapter.CompCatExpListAdapter;
import com.citizen.fmc.adapter.HomeGridAdapter;
import com.citizen.fmc.database.DBHelper;
import com.citizen.fmc.database.DatabaseTable;
import com.citizen.fmc.model.ComplaintCategoryModel;
import com.citizen.fmc.model.DrawerListItemModel;
import com.citizen.fmc.model.User;
import com.citizen.fmc.utils.ApiInterface;
import com.citizen.fmc.utils.ConnectivityReceiver;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.LocationFetch;
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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * ======> Created by dheeraj-gangwar on 2018-01-01 <======
 */

@SuppressLint("SetTextI18n")
public class HomeFragment extends Fragment implements ConnectivityReceiver.ConnectivityReceiverListener,
        LocationListener,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{
    private Activity activity;
    private GridView homeGridView;
    private final String TAG = getClass().getSimpleName();
    private List<DrawerListItemModel> drawerModuleList;
    private boolean refreshDrawerList = true;
    private SpotsDialog spotsDialog;
    private AlertDialog refreshDialog;
    private SimpleDraweeView userProfileImage;
    private TextView userNameTextView;
    private Dialog compCatDialog;
    private ExpandableListView expLV;
    private ArrayList<ComplaintCategoryModel> compCatList = new ArrayList<>();
    private boolean refreshCategoryList = true;
    private TextView tv_temperature;
    private TextView tv_temperature_text;
    private TextView tv_aqi;
    private DBHelper dbHelper;
    private FloatingActionButton complaintsFloatingActionButton;
    private TextView msell;
    private int userId;

    private LocationFetch locationFetch = new LocationFetch();
    private ConstraintLayout data_layout_view;
    private String aqiStatus="";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest = new LocationRequest();
    private Location mLocation;
    private static final long INTERVAL = 1000 * 3;
    private static final long FASTEST_INTERVAL = 1000 * 2;
    final static int REQUEST_LOCATION = 101;
    double lat=0.0;
    double lng= 0.0;
    private GoogleApiClient mGoogleApiClient;
    RecyclerView scrollImageBanner;
    LinearLayoutManager linearLayoutManager;
    Context mContext;
    ArrayList<Integer> bannerList = new ArrayList<>();
    int position = 0;
    Timer timer;
    TimerTask timerTask;
    String surveyor_status = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        setHasOptionsMenu(true);
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Utils.setToolbarTitle(activity, "Home");
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        if(!gpsEnabledCheck()){
            LocationFetch.buildAlertMessageNoGps(getActivity());
        }
        startLocationUpdates();
        getCurrentAddress();
        homeGridView = rootView.findViewById(R.id.list_view_home);
        userProfileImage = rootView.findViewById(R.id.user_image_sdv);
        userNameTextView = rootView.findViewById(R.id.user_name_text_view);
        tv_temperature = rootView.findViewById(R.id.tv_temperature);
        tv_temperature_text = rootView.findViewById(R.id.tv_temperature_text);
        data_layout_view = rootView.findViewById(R.id.data_layout_view);
        complaintsFloatingActionButton = rootView.findViewById(R.id.complaints_fab_home);
        tv_aqi = rootView.findViewById(R.id.tv_aqi);
        userId = Utils.getUserDetails(activity).getCivilianId();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        aqiStatus = sharedPreferences.getString(Constants.AQI_STATUS , "");
        tv_aqi.setText(aqiStatus + " " + Html.fromHtml(getContext().getResources().getString(R.string.aqi)));
        tv_aqi.setVisibility(View.VISIBLE);
//        msell = rootView.findViewById(R.id.msell);
        spotsDialog = new SpotsDialog(activity, getResources().getString(R.string.please_wait_dialog_text), R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);
        User mUser = Utils.getUserDetails(activity);
        dbHelper = new DBHelper(getActivity());

        mContext = getContext();
        scrollImageBanner = rootView.findViewById(R.id.scroll_image_banner);
        linearLayoutManager = new LinearLayoutManager(mContext,RecyclerView.HORIZONTAL,false);
        bannerList.clear();
        setBannerImage();
        setBanner();

        userProfileImage.setController(Utils.simpleDraweeController(
                   Constants.IMAGE_PUBLIC_URL + mUser.getProfileImagePath(), 130, 130));
        userNameTextView.setText(getContext().getResources().getString(R.string.welcome) + mUser.getFirstName());

        complaintsFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.changeFragment(getFragmentManager(), new NewComplaintFragment());
            }
        });

        if (drawerModuleList == null) {
            prepareGridViewDataList();
        } else {
            setGridViewAdapter();
        }
        showBottomText();
        currentWeather();
       // currentWeatherRequest(Constants.TEMPERATURE_API);
        //AQIData();
        return rootView;

    }
    private void setBannerImage() {
        bannerList.add(R.drawable.banner_1);
        bannerList.add(R.drawable.banner_2);
        bannerList.add(R.drawable.banner_3);
        bannerList.add(R.drawable.banner_4);
        bannerList.add(R.drawable.banner_5);
        bannerList.add(R.drawable.banner_6);
        bannerList.add(R.drawable.banner_7);
        bannerList.add(R.drawable.banner_8);
        bannerList.add(R.drawable.banner_9);
        bannerList.add(R.drawable.banner_10);
        scrollImageBanner.setLayoutManager(linearLayoutManager);
        BannerAdapter bannerAdapter = new BannerAdapter(mContext,bannerList);
        scrollImageBanner.setAdapter(bannerAdapter);
    }

    private void setBanner() {
        position = 0;
        scrollImageBanner.scrollToPosition(position);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(scrollImageBanner);
        scrollImageBanner.smoothScrollBy(5,0);
        scrollImageBanner.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 1){
                    stopAutoScrollBanner();
                } else if (newState == 0) {
                    position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                    runAutoScrollBanner();
                }
            }
        });
    }
    private void runAutoScrollBanner() {
        if (timer == null && timerTask == null){
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        if (position == scrollImageBanner.getAdapter().getItemCount()-1){
                            position = 0;
                            scrollImageBanner.smoothScrollBy(5,0);
                            scrollImageBanner.smoothScrollToPosition(position);
                        } else {
                            position++;
                            scrollImageBanner.smoothScrollToPosition(position);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            timer.schedule(timerTask,4000,4000);
        }
    }

    private void stopAutoScrollBanner() {
        if (timer != null && timerTask != null){
            timerTask.cancel();
            timer.cancel();
            timer = null;
            timerTask = null;
            position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        }
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

    /* ====================== Adding elements to List ====================== */
    private void prepareGridViewDataList() {
        spotsDialog.show();
        drawerModuleList = new ArrayList<>();
        drawerModuleList.clear();
        getGridViewModules();
    }
    /* ================================================================================================= */


    /* ====================== Setting adapter to ListView ====================== */
    private void setGridViewAdapter() {
        homeGridView.setAdapter(null);
        HomeGridAdapter adapter = new HomeGridAdapter(activity, drawerModuleList);
        homeGridView.setAdapter(adapter);
        spotsDialog.dismiss();
        gridViewItemClick();
        refreshDrawerList = false;
    }
    /* ================================================================================================= */


    /* ====================== Request method to get complaint categories ====================== */
    private void getGridViewModules() {
        ApiInterface mApiInterface = Utils.getInterfaceService();
        Log.v(TAG, Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity));
        Call<JsonObject> mService = mApiInterface.getDrawerModules(Constants.APP_TYPE,userId);
        surveyor_status = Constants.getPrefrence(mContext,"surveyor_status");
        mService.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    refreshDrawerList = true;
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        if (jsonObject.get("response").getAsBoolean()) {
                            JsonArray dataJsonArray = jsonObject.getAsJsonArray("data");
                            String toll_freeNumber = jsonObject.getAsJsonObject().get("toll_no").getAsString();
                            Constants.setPreferenceStringData(mContext,"TOLL_FREE_NUMBER",toll_freeNumber);
                            if (dataJsonArray.size() > 0) {
                                for (int i = 0; i < dataJsonArray.size(); i++) {
                                    if (surveyor_status.equals("1")) {
                                        DrawerListItemModel itemModel = new GsonBuilder().create()
                                                .fromJson(dataJsonArray.get(i), DrawerListItemModel.class);
                                        drawerModuleList.add(itemModel);
                                    } else {
                                        String id = dataJsonArray.get(i).getAsJsonObject().get("id").getAsString();
                                        if (!id.equals("68")) {
                                            DrawerListItemModel itemModel = new GsonBuilder().create()
                                                    .fromJson(dataJsonArray.get(i), DrawerListItemModel.class);
                                            drawerModuleList.add(itemModel);
                                        }
                                    }
                                }
                                Log.v(TAG, drawerModuleList.toString());
                                spotsDialog.dismiss();
                            }
                            setGridViewAdapter();
                        } else {
                            refreshDrawerList = true;
                            refreshDataDialog();
                            spotsDialog.dismiss();
                            Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                    jsonObject.get("message").getAsString().trim(),
                                    false);
                        }
                    } else {
                        refreshDrawerList = true;
                        refreshDataDialog();
                        spotsDialog.dismiss();
                        Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                Constants.MESSAGE_NO_DATA_FOUND, false);
                    }
                } catch (Exception e) {
                    refreshDrawerList = true;
                    refreshDataDialog();
                    spotsDialog.dismiss();
                    e.printStackTrace();
                    Utils.customToast(activity,
                            Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                refreshDrawerList = true;
                refreshDataDialog();
                spotsDialog.dismiss();
                call.cancel();
                t.printStackTrace();
                if (t instanceof TimeoutException || t instanceof ConnectException
                        || t instanceof SocketException || t instanceof SocketTimeoutException) {
                    Utils.customToast(activity,
                            Constants.MESSAGE_CHECK_INTERNET, 0);
                } else {
                    Utils.customToast(activity,
                            Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                }
            }
        });
    }
    /* ================================================================================================= */
    //Help Line

    /* ====================== Drawer ListView onItemClickListener ====================== */
    private void gridViewItemClick() {
        homeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isAPICall(drawerModuleList.get(position));

            }
        });
    }
    /* ================================================================================================= */


    /* ====================== Method for check whether to open webView or normal fragment ====================== */
    private void isAPICall(DrawerListItemModel mList) {
        if (mList.getModuleURLStatus() == 1) {
            // Calling API
            switch (mList.getModuleId()) {
                case Constants.MODULE_ID_CLEAN_NDMC:
                    Utils.changeFragment(getFragmentManager(), new ViewMyComplaintsFragment());
                    Utils.setToolbarTitle(activity, mList.getModuleTitleName());
                    break;

                case Constants.MODULE_ID_COMPLAINTS:
//                   Utils.changeFragment(getFragmentManager() , new WastageFragment());

                    Utils.changeFragment(getFragmentManager() , new OptionListFragment());
                    Utils.setToolbarTitle(activity, mList.getModuleTitleName());
                    Constants.setPreferenceStringData(activity,"complaint_module_id","8");
                    break;

                case Constants.MODULE_ID_AIRPOLLUTION:
                    Utils.changeFragment(getFragmentManager() , new OptionListFragment());
                    Utils.setToolbarTitle(activity, mList.getModuleTitleName());
                    Constants.setPreferenceStringData(activity,"complaint_module_id","68");
                    break;

                case Constants.MODULE_ID_WATERNSEWAGECOMPLAINTS:
                    Utils.changeFragment(getFragmentManager() , new OptionListFragment());
                    Utils.setToolbarTitle(activity, mList.getModuleTitleName());
                    Constants.setPreferenceStringData(activity,"complaint_module_id","69");
                    break;

                case Constants.MODULE_ID_SWACHTA:
                    Utils.changeFragment(getFragmentManager() , new DandDScannerFragment());

//                    Utils.changeFragment(getFragmentManager() , new OptionListFragment());
                    Utils.setToolbarTitle(activity, mList.getModuleTitleName());
                    break;


                case Constants.MODULE_ID_HELPLINE:
                    spotsDialog.dismiss();
                    Utils.changeFragment(getFragmentManager() , new HelpLineFragment());
                    Utils.setToolbarTitle(activity, mList.getModuleTitleName());
                    break;

                case Constants.MODULE_ID_TREE_NEAR_BY_ME:
                    spotsDialog.dismiss();
//                    Utils.changeFragment(getFragmentManager(),new NearByTreeFragment());
                    Utils.changeFragment(getFragmentManager(),new TreeInNdmcFragment());
                    Utils.setToolbarTitle(activity,mList.getModuleTitleName());
                    break;

                case Constants.MODULE_ID_WHO_SERVE_YOU:
                    if (refreshCategoryList) {
                        spotsDialog.show();
                        getComplaintCategories(mList.getModuleId(), mList.getModuleTitleName());
                    } else {
                        compCatDialog.show();
                    }
                    break;

                case Constants.MODULE_ID_SMARTCITYPARKING:
                  //isAppInstalled Check:-
                    try {

                        Constants.customToast(activity,"Please install FMC Smart Bike Application from Google PlayStore.",2);

                    }catch(Exception e) {
                        Toast.makeText(getApplicationContext(),"Unable to Connect Try Again...",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    break;

                case 20 :
                    Toast.makeText(requireContext(),"Added Soon...",Toast.LENGTH_SHORT).show();
                    break;
                case 13:
                    Toast.makeText(requireContext(),"Added Soon...",Toast.LENGTH_SHORT).show();
                    break;
                case 26:
                    Toast.makeText(requireContext(),"Added Soon...",Toast.LENGTH_SHORT).show();
                    break;

                default:
                    if (mList.getSubItemModelList().size() > 0) {
                        HomeSubListFragment homeSubListFragment = new HomeSubListFragment();
                        Bundle mBundle = new Bundle();
                        mBundle.putInt(Constants.KEY_MODULE_ID, mList.getModuleId());
                        mBundle.putString(Constants.KEY_MODULE_NAME, mList.getModuleTitleName());
                        mBundle.putString(Constants.KEY_TOOL_BAR_TITLE, mList.getModuleTitleName());
                        mBundle.putParcelableArrayList(Constants.KEY_COMPLAINT_DATA, mList.getSubItemModelList());
                        homeSubListFragment.setArguments(mBundle);
                        Utils.changeFragment(getFragmentManager(), homeSubListFragment);
                    }
                    break;
            }
        } else {
            String language_code =  Constants.getPrefrence(getContext(),"locale");
            if (!TextUtils.isEmpty(language_code) && language_code.equalsIgnoreCase("en"))
            {
                Intent intent = new Intent(getActivity() , WebViewActivity.class);
                intent.putExtra(Constants.KEY_TOOL_BAR_TITLE, mList.getModuleTitleName());
                intent.putExtra(Constants.KEY_WEB_VIEW_URL, mList.getModuleURL());
                startActivity(intent);
            }
            else
            {
                String hindi_url = mList.getHindiURL();
                if(!TextUtils.isEmpty(hindi_url))
                {
                    Intent intent = new Intent(getActivity() , WebViewActivity.class);
                    intent.putExtra(Constants.KEY_TOOL_BAR_TITLE, mList.getTitleHindi());
                    intent.putExtra(Constants.KEY_WEB_VIEW_URL, mList.getHindiURL());
                    startActivity(intent);
                }
                else
                {

                    Intent intent = new Intent(getActivity() , WebViewActivity.class);
                    intent.putExtra(Constants.KEY_TOOL_BAR_TITLE, mList.getModuleTitleName());
                    intent.putExtra(Constants.KEY_WEB_VIEW_URL, mList.getModuleURL());
                    startActivity(intent);
//                    Utils.changeFragment(getFragmentManager() , new NearByTreeFragment());

                }

            }

        }
    }

    public boolean isAppInstalled() {
        try {
            boolean smartBikeInstalled = false;
            PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo("net.nextbike.smartbike", 0);
            String getPackageName = packageInfo.toString();
            if (getPackageName.equals("net.nextbike.smartbike")) {

                Intent viewIntent = new Intent("android.intent.action.VIEW",
                                    Uri.parse("net.nextbike.smartbike"));
                startActivity(viewIntent);
                smartBikeInstalled = true;
            }else {
                Intent viewIntent = new Intent("android.intent.action.VIEW",
                                    Uri.parse("https://play.google.com/store/apps/details?id=net.nextbike.smartbike"));
                startActivity(viewIntent);
            }
        } catch (PackageManager.NameNotFoundException e) {
           e.printStackTrace();

        }
        return true;
    }
    /* ================================================================================================= */


    /* ====================== Create request dialog box for Drawer Modules ====================== */
    private void refreshDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(getContext().getResources().getString(R.string.please_check_your_internet_connection))
                .setCancelable(false)
                .setPositiveButton(getContext().getResources().getString(R.string.refresh), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        prepareGridViewDataList();
                    }
                });
        builder.show();
        refreshDialog = builder.create();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            if (refreshDrawerList) {
                spotsDialog.dismiss();
                if (refreshDialog != null) {
                    refreshDialog.dismiss();
                }
                prepareGridViewDataList();
            }
            Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                    Constants.MESSAGE_INTERNET_CONNECTED, true);
        } else {
            Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                    Constants.MESSAGE_INTERNET_NOT_CONNECTED, false);
        }
    }
    /* ================================================================================================= */


    /* ====================== Request method to get complaint categories ====================== */
    private void getComplaintCategories(final int moduleId, final String moduleTitle) {
        ApiInterface mApiInterface = Utils.getInterfaceService();
        Log.v(TAG, Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity));
        Call<JsonObject> mService = mApiInterface.getComplaintCategories(Constants.HEADER_TOKEN_BEARER
                        + Utils.getUserToken(activity),
                Constants.HEADER_ACCEPT);
        mService.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if (activity != null && isAdded()) {
                        if (response.body() != null) {
                            JsonObject jsonObject = response.body();
                            if (jsonObject.get("response").getAsBoolean()) {
                                JsonArray departmentJsonArray = jsonObject.getAsJsonArray("data");
                                if (departmentJsonArray.size() > 0) {
                                    for (int i = 0; i < departmentJsonArray.size(); i++) {
                                        ComplaintCategoryModel categoryModel = new GsonBuilder().create()
                                                .fromJson(departmentJsonArray.get(i), ComplaintCategoryModel.class);
                                        compCatList.add(categoryModel);
                                    }
                                    Log.v(TAG, compCatList.toString());
                                    createComplaintCategoryDialog(moduleId, moduleTitle);
                                }
                            } else {
                                refreshCategoryList = true;
                                spotsDialog.dismiss();
                                Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                        jsonObject.get("message").getAsString().trim(), false);
                            }
                        } else {
                            refreshCategoryList = true;
                            spotsDialog.dismiss();
                            Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
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
                    refreshCategoryList = true;
                    spotsDialog.dismiss();
                    call.cancel();
                    t.printStackTrace();
                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content), Utils.failureMessage(t), false);
                }
            }
        });
    }
    /* ================================================================================================= */


    /* ====================== Setting adapter to Exp. ListView ====================== */
    private void setCompCatListViewAdapter() {
        CompCatExpListAdapter adapter = new CompCatExpListAdapter(activity, compCatList);
        expLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        refreshCategoryList = false;
        spotsDialog.dismiss();
    }
    /* ================================================================================================= */


    /* ====================== Creating compCatDialog for showing complaint categories ====================== */
    private void createComplaintCategoryDialog(final int moduleId, final String moduleTitle) {
        compCatDialog = new Dialog(activity, R.style.dialogSlideAnimation);
        compCatDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        compCatDialog.setContentView(R.layout.layout_comp_cat_dialog);
        compCatDialog.setCancelable(false);

        expLV = compCatDialog.findViewById(R.id.expandable_list_view);

        expLV.setAdapter(new CompCatExpListAdapter());

        setCompCatListViewAdapter();

//        expLV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                return false;
//            }
//        });

        expLV.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                WhoServeYouFragment whoServeYouFragment = new WhoServeYouFragment();
                Bundle mBundle = new Bundle();
                mBundle.putInt(Constants.KEY_MODULE_ID, moduleId);
                mBundle.putInt(Constants.KEY_DEPARTMENT_ID, compCatList.get(groupPosition).getDepartmentId());
//                mBundle.putString(Constants.KEY_SUB_DEPARTMENT_ID,
//                        compCatList.get(groupPosition).getSubCategoryModelList().get(childPosition).getSubCategoryId());
                mBundle.putString(Constants.KEY_TOOL_BAR_TITLE, moduleTitle);
                whoServeYouFragment.setArguments(mBundle);
                Utils.changeFragment(getFragmentManager(), whoServeYouFragment);
                compCatDialog.dismiss();
                return true;
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


    public void currentWeather() {

        ApiInterface mApiInterface = Utils.getInterfaceService();
        Call<JsonObject> callZC = mApiInterface.getWheather(lat
                , lng, Constants.WEATHER_API_KEY);

        callZC.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String main = "";
                Double temp = 0.0;
                try {
                    JsonArray jsonArray = response.body().get("weather").getAsJsonArray();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        main = jsonArray.get(i).getAsJsonObject().get("main").getAsString();
                    }
                    JsonObject jsonObject = response.body().get("main").getAsJsonObject();
                    temp = jsonObject.getAsJsonObject().get("temp").getAsDouble();
                    int intTemp=temp.intValue();
                    intTemp=intTemp/10;
                    tv_temperature.setText(main);
                    tv_temperature_text.setText((intTemp)+"° C");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
            }

        });
    }


    // Trigger new mLocation updates at interval
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

    protected void stopLocationUpdates() {
        if (mLocationCallback != null) {
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            Log.d(TAG, "Location update stopped ..............");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        Log.d(TAG, "Firing onLocationChanged ..............");
        if (mLocation != null) {
            Log.d(TAG, "LAT: " + mLocation.getLatitude() + ",\nLONG: " + mLocation.getLongitude());

            lat=mLocation.getLatitude();
            lng=mLocation.getLongitude();

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
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
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ..............: " + mGoogleApiClient.isConnected());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
        showBannerViews();
        currentWeather();


    }

    private void showBannerViews() {
        View banner_image = getActivity().findViewById(R.id.banner_view);
        View view1 = getActivity().findViewById(R.id.view);
        view1.setVisibility(View.VISIBLE);
//        banner_image.setVisibility(View.VISIBLE);
        userProfileImage.setVisibility(View.INVISIBLE);
        tv_aqi.setVisibility(View.VISIBLE);
        /*RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.user_name_text_view);
        homeGridView.setLayoutParams(params);*/
        homeGridView.smoothScrollToPosition(0);
    }

    private void AQIData() {
        Cursor cursor  = dbHelper.getDelhiAQIData(Constants.CITY_NAME);
        if(cursor.getCount() > 0){
            if(cursor.moveToFirst()){
                do {
                    String count = cursor.getString(cursor.getColumnIndex(DatabaseTable.aqiTable.COLUMN_AQI_COUNT));
                    tv_aqi.setText(count + getContext().getResources().getString(R.string.aqi));
                    //  tv_aqi.setText(count + "CPCB");
                }while (cursor.moveToNext());
            }
        }
    }




    private void showBottomText() {
        homeGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(/*scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL ||*/ scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING){
                    View banner_image = getActivity().findViewById(R.id.banner_view);
                    View view1 = getActivity().findViewById(R.id.view);
                        view1.setVisibility(View.GONE);
//                        banner_image.setVisibility(View.GONE);
                        userProfileImage.setVisibility(View.GONE);
                        tv_aqi.setVisibility(View.GONE);
//                    ConstraintLayout.LayoutParams params= new ConstraintLayout.LayoutParams
//                            (ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//                    params.addRule(RelativeLayout.BELOW, R.id.data_layout_view);
                   /* ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams
                            (ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    homeGridView.setLayoutParams(layoutParams);*/

                }else if(view.getFirstVisiblePosition() == 0){
                    View banner_image = getActivity().findViewById(R.id.banner_view);
                    View view1 = getActivity().findViewById(R.id.view);
                    view1.setVisibility(View.VISIBLE);
//                    banner_image.setVisibility(View.VISIBLE);
                    userProfileImage.setVisibility(View.INVISIBLE);
                    tv_aqi.setVisibility(View.VISIBLE);
                   /* RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams
                            (ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW, R.id.user_name_text_view);
                    homeGridView.setLayoutParams(params);*/
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem > totalItemCount) {
                   /* msell.setText(Html.fromHtml("<font color=\"#000000\"> Manacle Technologies Pvt Ltd </font>"+
                            "<br>" + "<font color=\"#0080ff\"> http://manacleindia.com </font>"));
                    msell.setVisibility(View.VISIBLE);
                    msell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            browserIntent.setData(Uri.parse("http://manacleindia.com/"));
                            startActivity(browserIntent);
                        }
                    });*/

                }else {

                }
            }
        });
    }


}