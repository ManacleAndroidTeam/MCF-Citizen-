package com.citizen.fmc.activity;

 import android.annotation.SuppressLint;
 import android.app.AlertDialog;
import android.app.Dialog;
 import android.content.Context;
 import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
 import android.content.res.Configuration;
 import android.content.res.Resources;
 import android.graphics.Paint;
 import android.net.Uri;
 import android.os.Bundle;
import android.os.Handler;

 import androidx.annotation.NonNull;
 import androidx.fragment.app.Fragment;
 import androidx.fragment.app.FragmentActivity;
 import androidx.core.view.GravityCompat;
 import androidx.appcompat.app.ActionBarDrawerToggle;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.appcompat.widget.Toolbar;
 import android.util.DisplayMetrics;
 import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
 import android.widget.EditText;
 import android.widget.ImageView;
import android.widget.ListView;
 import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.UserApplication;
import com.citizen.fmc.adapter.DrawerListAdapter;
import com.citizen.fmc.fragment.FAQFragment;
import com.citizen.fmc.fragment.FeedbackFragment;
 import com.citizen.fmc.fragment.HomeFragment;
import com.citizen.fmc.model.ImageTextModel;
import com.citizen.fmc.model.User;
import com.citizen.fmc.service.UserNotificationService;
import com.citizen.fmc.utils.ApiInterface;
 import com.citizen.fmc.utils.ButtonView;
 import com.citizen.fmc.utils.ConnectivityReceiver;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
 import java.util.Locale;
 import java.util.Timer;
 import java.util.TimerTask;

 import dmax.dialog.SpotsDialog;
 import okhttp3.MediaType;
 import okhttp3.RequestBody;
 import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
 import androidx.drawerlayout.widget.DrawerLayout;
 import androidx.recyclerview.widget.LinearLayoutManager;
 import androidx.recyclerview.widget.LinearSnapHelper;
 import androidx.recyclerview.widget.RecyclerView;
 import androidx.recyclerview.widget.SnapHelper;

/**
 * ======> Created by dheeraj-gangwar on 2018-04-18 <======
 */

@SuppressLint("SetTextI18n, ResourceType")
public class SlidingDrawerActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    private final String TAG = getClass().getSimpleName();
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mainView;
    private List<ImageTextModel> drawerModuleList;
    private ListView drawerListView;
    private TextView tv_company_name;
    private RelativeLayout parentLayout;
    private Toolbar toolbar;
    private RadioGroup drawerBottomTabRadioGroup;
    private boolean doubleBackToExitPressedOnce = false;
    private boolean refreshDrawerList = true;
    private SpotsDialog spotsDialog;
    private AlertDialog refreshDialog;
    private int profileStatus;
    private String unreadCount = "";
    private String aqiStatus = "";
    private boolean versionUpdateCheckCivilian;
    private TextView mCounter;
    private ApiInterface mApiInterface;
    private FragmentActivity activity;
    private SharedPreferences sharedPrefs;
    private Dialog versionUpdateDialog;
    private ButtonView update_version_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = SlidingDrawerActivity.this;
        setContentView(R.layout.activity_sliding_drawer);
         RelativeLayout drawerView = findViewById(R.id.drawerView);
        mainView = findViewById(R.id.mainView);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        drawerListView = drawerView.findViewById(R.id.drawer_list_view);
        tv_company_name = drawerView.findViewById(R.id.tv_company_name);
        drawerBottomTabRadioGroup = drawerView.findViewById(R.id.drawer_tab_layout_radio_group);
        SimpleDraweeView userProfileImageView = drawerView.findViewById(R.id.user_profile_image_view);
        mApiInterface = Utils.getInterfaceService();
        sharedPrefs = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        profileStatus = sharedPrefs.getInt(Constants.USER_PROFILE_STATUS, Constants.DEFAULT_INTEGER_VALUE);
        Log.v(TAG, "Login Count: " + profileStatus);

        User mUser = Utils.getUserDetails(SlidingDrawerActivity.this);

        profileStatus = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE)
                .getInt(Constants.USER_PROFILE_STATUS, Constants.DEFAULT_INTEGER_VALUE);
        Log.v(TAG, "Profile status: " + profileStatus);

        spotsDialog = new SpotsDialog(SlidingDrawerActivity.this,
                getResources().getString(R.string.please_wait_dialog_text),
                R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);

        setupToolbar();
        startService(new Intent(getApplicationContext(),UserNotificationService.class));

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                //mainView.setTranslationX(slideOffset * drawerView.getWidth());
                mDrawerLayout.bringChildToFront(drawerView);
                mDrawerLayout.requestLayout();
            }
        };

//        mDrawerToggle.setDrawerIndicatorEnabled(false);
//        mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_action_menu);
//        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDrawerLayout.openDrawer(GravityCompat.START);
//            }
//        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        prepareDataList();
        versionUpdate();
        getAllNotifications();

//        if (ConnectivityReceiver.isConnected()) {
//            spotsDialog.show();
//            prepareDataList();
//            Utils.changeFragmentWithoutBackStack(getSupportFragmentManager(), new HomeFragment());
//        } else {
//            refreshDataDialog();
//            Utils.showSnackBar(SlidingDrawerActivity.this, findViewById(android.R.id.content),
//                    Constants.MESSAGE_INTERNET_NOT_CONNECTED, false);
//        }

        userProfileImageView.setController(Utils.simpleDraweeController(
                Constants.IMAGE_PUBLIC_URL + mUser.getProfileImagePath(),
                100, 100));

        ((TextView) drawerView.findViewById(R.id.user_name_text_view))
                .setText(getApplicationContext().getResources().getString(R.string.welcome)  + mUser.getFirstName() + " " +
                        (mUser.getLastName() != null ? mUser.getLastName() : "") + " !");

//        (drawerView.findViewById(R.id.edit_profile_radio_button)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment f = getSupportFragmentManager().findFragmentById(R.id.main_sliding_container);
//                if (f != null && f instanceof ProfileFragment) {
//                    closeDrawer();
//                } else {
//                    closeDrawer();
//                    toolbar.setTitle("Profile");
//                    Utils.changeFragment(getSupportFragmentManager(), new ProfileFragment());
//                }
//            }
//        });

        drawerBottomTabRadioGroup.setOnCheckedChangeListener(drawerBottomTabListener());

        tv_company_name.setClickable(true);
        tv_company_name.setPaintFlags(tv_company_name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tv_company_name.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse("http://manacleindia.com/"));
                startActivity(browserIntent);
            }
        });
    }

    /* ====================== Initializing Toolbar ====================== */
    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Constants.TOOLBAR_TITLE_HOME);
        setSupportActionBar(toolbar);
    }
    /* ================================================================================================= */


    /* ====================== Adding elements to List ====================== */
    private void prepareDataList() {
        drawerModuleList = new ArrayList<>();
        drawerModuleList.clear();
        drawerModuleList.add(new ImageTextModel(R.drawable.ic_home, "Home"));
        drawerModuleList.add(new ImageTextModel(R.drawable.ic_profile, "Profile"));
        drawerModuleList.add(new ImageTextModel(R.drawable.ic_faq, "Frequently Asked Ques."));
        drawerModuleList.add(new ImageTextModel(R.drawable.ic_feedback, "Feedback"));
        drawerModuleList.add(new ImageTextModel(R.drawable.ic_logout,"Logout"));
        drawerModuleList.add(new ImageTextModel(R.drawable.ic_chnage_language_24,"Change Language"));

        setDrawerListViewAdapter();
//        getDrawerModules();
    }
    /* ================================================================================================= */


    /* ====================== Setting adapter to ListView ====================== */
    private void setDrawerListViewAdapter() {
        DrawerListAdapter adapter = new DrawerListAdapter(SlidingDrawerActivity.this, drawerModuleList , Constants.SIDE_NAV);
        drawerListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        spotsDialog.dismiss();
        drawerListViewItemClick();

//         Opening Complaints on opening application(on Home screen)
        Utils.changeFragmentWithoutBackStack(getSupportFragmentManager(), new HomeFragment());

        if (profileStatus == 0) {
            finish();
            startActivity(new Intent(this, UserProfileActivity.class));
            Utils.enterAnimation(this);
        }
        refreshDrawerList = false;
    }
    /* ================================================================================================= */


    /* ====================== Request method to get complaint categories ====================== */
//    private void getDrawerModules() {
//        ApiInterface mApiInterface = Utils.getInterfaceService();
//        Log.v(TAG, Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(SlidingDrawerActivity.this));
//        Call<JsonObject> mService = mApiInterface.getDrawerModules(Constants.APP_TYPE);
//        mService.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                try {
//                    refreshDrawerList = true;
//                    if (response.body() != null) {
//                        Log.v(TAG, String.valueOf(response.body()));
//                        JsonObject jsonObject = response.body();
//                        if (jsonObject.get("response").getAsBoolean()) {
//                            JsonArray dataJsonArray = jsonObject.getAsJsonArray("data");
//                            if (dataJsonArray.size() > 0) {
//                                for (int i = 0; i < dataJsonArray.size(); i++) {
//                                    DrawerListItemModel itemModel = new GsonBuilder().create()
//                                            .fromJson(dataJsonArray.get(i), DrawerListItemModel.class);
//                                    drawerModuleList.add(itemModel);
//                                }
//                                Log.v(TAG, drawerModuleList.toString());
//                                spotsDialog.dismiss();
//                            }
//                            setDrawerListViewAdapter();
//                        } else {
//                            refreshDrawerList = true;
//                            refreshDataDialog();
//                            spotsDialog.dismiss();
//                            Utils.showSnackBar(SlidingDrawerActivity.this,
//                                    findViewById(android.R.id.content),
//                                    jsonObject.get("message").getAsString().trim(),
//                                    false);
//                        }
//                    } else {
//                        refreshDrawerList = true;
//                        refreshDataDialog();
//                        spotsDialog.dismiss();
//                        Utils.showSnackBar(SlidingDrawerActivity.this, findViewById(android.R.id.content),
//                                Constants.MESSAGE_NO_DATA_FOUND, false);
//                    }
//                } catch (Exception e) {
//                    refreshDrawerList = true;
//                    refreshDataDialog();
//                    spotsDialog.dismiss();
//                    e.printStackTrace();
//                    Utils.customToast(SlidingDrawerActivity.this,
//                            Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                refreshDrawerList = true;
//                refreshDataDialog();
//                spotsDialog.dismiss();
//                call.cancel();
//                t.printStackTrace();
//                if (t instanceof TimeoutException || t instanceof ConnectException
//                        || t instanceof SocketException || t instanceof SocketTimeoutException) {
//                    Utils.customToast(SlidingDrawerActivity.this,
//                            Constants.MESSAGE_CHECK_INTERNET, 0);
//                } else {
//                    Utils.customToast(SlidingDrawerActivity.this,
//                            Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
//                }
//            }
//        });
//    }
    /* ================================================================================================= */


    /* ====================== Drawer ListView onItemClickListener ====================== */
    private void drawerListViewItemClick() {
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                resetDrawerBottomTabListener();
//                Utils.setToolbarColor(SlidingDrawerActivity.this, getResources().getString(R.color.colorLightGreen));
                switch (position) {
                    case 0:
                        toolbar.setTitle(Constants.TOOLBAR_TITLE_HOME);
                        Utils.changeFragmentWithoutBackStack(getSupportFragmentManager(), new HomeFragment());
                        break;

                    case 1:
                        startActivity(new Intent(SlidingDrawerActivity.this, UserProfileActivity.class));
                        break;

                    case 2:
                        toolbar.setTitle(Constants.TOOLBAR_TITLE_FAQ);
                        Utils.changeFragment(getSupportFragmentManager(), new FAQFragment());
                        break;

                    case 3:
                        toolbar.setTitle(Constants.TOOLBAR_TITLE_FEEDBACK);
                        Utils.changeFragment(getSupportFragmentManager(), new FeedbackFragment());
                        break;
                    case 4:
                        toolbar.setTitle(Constants.TOOLBAR_TITLE_LOGOUT);
                        logoutDialog();
                        break;
                    case 5:
                        toolbar.setTitle(Constants.TOOLBAR_CHANGE_LANGUAGE);
                        localeDialog();
                        break;


                }
                closeDrawer();
            }
        });

//        drawerListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
//            @Override
//            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
//                if (drawerModuleList.get(groupPosition).getSubItemModelList().size() <= 0) {
//                    isAPICall(drawerModuleList.get(groupPosition).getModuleURLStatus(),
//                            drawerModuleList.get(groupPosition).getModuleId(),
//                            drawerModuleList.get(groupPosition).getModuleURL(),
//                            false,
//                            drawerModuleList.get(groupPosition).getModuleTitleName());
//                }
//                return false;
//            }
//        });
//
//        drawerListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//            @Override
//            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                isAPICall(drawerModuleList.get(groupPosition).getSubItemModelList().get(childPosition).getModuleURLStatus(),
//                        drawerModuleList.get(groupPosition).getSubItemModelList().get(childPosition).getModuleId(),
//                        drawerModuleList.get(groupPosition).getSubItemModelList().get(childPosition).getModuleURL(),
//                        true,
//                        drawerModuleList.get(groupPosition).getSubItemModelList().get(childPosition).getModuleTitleName());
//                return false;
//            }
//        });
    }
    /* ================================================================================================= */


    /* ====================== Method for check whether to open webView or normal fragment ====================== */
//    private void isAPICall(int status, int id, String url, boolean isChild, String title) {
//        closeDrawer();
//        toolbar.setTitle(title);
//        drawerBottomTabRadioGroup.setOnCheckedChangeListener(null);
//        drawerBottomTabRadioGroup.clearCheck();
//        drawerBottomTabRadioGroup.setOnCheckedChangeListener(drawerBottomTabListener());
//        if (status == 1) {
//            // Calling API
//            if (isChild) {
//                // if child is clicked
//            } else {
//                // if group is clicked
//                switch (id) {
//                    case 8:
//                        Utils.changeFragmentWithoutBackStack(getSupportFragmentManager(), new ViewMyComplaintsFragment());
//                        break;
//                }
//            }
//        } else {
//            // Calling Web View
//            WebViewFragment webViewFragment = new WebViewFragment();
//            Bundle mBundle = new Bundle();
//            mBundle.putString(Constants.KEY_WEB_VIEW_URL, url);
//            webViewFragment.setArguments(mBundle);
//            Utils.changeFragmentWithoutBackStack(getSupportFragmentManager(), webViewFragment);
//        }
//    }
    /* ================================================================================================= */


    /* ====================== Closing Drawer smoothly inside a Handler ====================== */
    private void closeDrawer() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mDrawerLayout.closeDrawer(Gravity.START);
            }
        });
    }
    /* ================================================================================================= */


    private void localeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogSlideAnim);
        //Setting message manually and performing action on button click
        builder.setMessage(R.string.ChangeLanguage)
                .setCancelable(true)
                .setNegativeButton(R.string.Hindi, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //api used to logout userId
//                        setNewLocale(HomeActivity.this, LocaleManager.HINDI);
                        Constants.setPreferenceStringData(SlidingDrawerActivity.this,
                                "locale","hi");
                        setLocale("hi");
                        dialog.cancel();

                    }
                })
                .setPositiveButton(R.string.English, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        setNewLocale(HomeActivity.this, LocaleManager.ENGLISH);
                        Constants.setPreferenceStringData(SlidingDrawerActivity.this,"locale","en");
                        setLocale("en");

                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                }).show();

    }

    /* ====================== Listener for drawer bottom tab [RADIO GROUP] ====================== */
    private RadioGroup.OnCheckedChangeListener drawerBottomTabListener() {
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                closeDrawer();
//                Utils.setToolbarColor(SlidingDrawerActivity.this, getResources().getString(R.color.colorLightGreen));
                switch (checkedId) {
//                    case R.id.helpline_radio_button:
//                        toolbar.setTitle(((RadioButton) findViewById(checkedId)).getText().toString());
//                        Utils.changeFragmentWithoutBackStack(getSupportFragmentManager(), new HelpLineFragment());
//                        break;
//
//                    case R.id.news_and_updates_radio_button:
//                        Intent intent = new Intent(SlidingDrawerActivity.this , WebViewActivity.class);
//                        intent.putExtra(Constants.KEY_TOOL_BAR_TITLE, Constants.TOOLBAR_TITLE_NEWS_UPDATES);
//                        intent.putExtra(Constants.KEY_WEB_VIEW_URL, "http://online.ndmc.gov.in/info");
//                        startActivity(intent);
//                        break;

//                    case R.id.logout_radio_button:
//
//                        break;
                }
                resetDrawerBottomTabListener();
            }
        };
    }
    /* ================================================================================================= */


    /* ====================== Resetting drawer bottom tab listener[RADIO GROUP] ====================== */
    private void resetDrawerBottomTabListener() {
        drawerBottomTabRadioGroup.setOnCheckedChangeListener(null);
        drawerBottomTabRadioGroup.clearCheck();
        drawerBottomTabRadioGroup.setOnCheckedChangeListener(drawerBottomTabListener());
    }
    /* ================================================================================================= */


    /* ====================== Create request dialog box for Drawer Modules ====================== */
    private void refreshDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SlidingDrawerActivity.this);
        builder.setMessage("Please check your internet connection and try again !")
                .setCancelable(false)
                .setPositiveButton("REFRESH", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        prepareDataList();
                    }
                });
        builder.show();
        refreshDialog = builder.create();
    }
    /* ================================================================================================= */


    /* ====================== Confirmation dialog box for logout ====================== */
    public void logoutDialog() {
        final Dialog dialog = new Dialog(SlidingDrawerActivity.this, R.style.dialogSlideAnimation);
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

        titleIcon.setImageResource(R.drawable.ic_logout);
        titleText.setText(R.string.nav_logout);
        dialogMessage.setText(R.string.info_text_logout);

        cancelButton.setText(R.string.not_now);
        OKButton.setText(R.string.yes);
        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.logoutUser(SlidingDrawerActivity.this);
                finish();
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDrawerBottomTabListener();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    /* ================================================================================================= */


    /* ====================== Implemented method of ConnectivityReceiver ====================== */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
//            if (refreshDrawerList) {
//                spotsDialog.dismiss();
//                if (refreshDialog != null) {
//                    refreshDialog.dismiss();
//                }
//                prepareDataList();
//            }
            Utils.showSnackBar(SlidingDrawerActivity.this, findViewById(android.R.id.content),
                    Constants.MESSAGE_INTERNET_CONNECTED, true);
        } else {
            Utils.showSnackBar(SlidingDrawerActivity.this, findViewById(android.R.id.content),
                    Constants.MESSAGE_INTERNET_NOT_CONNECTED, false);
        }
    }
    /* ================================================================================================= */

    private void getAllNotifications() {
        //spotsDialog.show();
        mApiInterface.getAllNotifications(2,
                Utils.getUserDetails(SlidingDrawerActivity.this).getCivilianId(),
                Constants.HEADER_TOKEN_BEARER + " " + Utils.getUserToken(SlidingDrawerActivity.this),
                Constants.HEADER_ACCEPT)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject mResponseObject = response.body();
                        if (mResponseObject != null) {
                            if (mResponseObject.get("response").getAsBoolean()) {
                                JsonArray jsonArray = mResponseObject.getAsJsonArray("list");
                                unreadCount = mResponseObject.get("unread_count").getAsString();
                                aqiStatus = mResponseObject.get("aqi_status").getAsString();
                                versionUpdateCheckCivilian = mResponseObject.get("civilian_version_update").getAsBoolean();
                                SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Constants.NOTIFICATION_UNREAD_COUNT, unreadCount);
                                editor.putString(Constants.AQI_STATUS, aqiStatus);
                                editor.apply();
                                //spotsDialog.dismiss();

                                //Pop Up Update App Version Dialog if Update is available for Current Using Version of App:-
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (versionUpdateCheckCivilian) {
                                            versionUpdateDialog.show();
                                        }
                                    }
                                });
                            } else {
                               //spotsDialog.dismiss();
                                Constants.customToast(SlidingDrawerActivity.this,
                                        Constants.MESSAGE_NO_DATA_FOUND, 2);
                            }
                        } else {
                           //spotsDialog.dismiss();
                            Constants.customToast(SlidingDrawerActivity.this,
                                    Constants.MESSAGE_SOMETHING_WENT_WRONG, 2);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        call.cancel();
                       //spotsDialog.dismiss();
                        t.printStackTrace();
                        Constants.customToast(SlidingDrawerActivity.this,
                                Constants.failureMessage(t), 0);
                    }
                });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        RelativeLayout badgeLayout = (RelativeLayout) menu.findItem(R.id.action_notification).getActionView();
        mCounter = badgeLayout.findViewById(R.id.counter);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
        unreadCount = sharedPreferences.getString(Constants.NOTIFICATION_UNREAD_COUNT , "");
        if(unreadCount.isEmpty()){
            mCounter.setVisibility(View.GONE);
        }else {
            mCounter.setText(unreadCount);
            mCounter.setVisibility(View.VISIBLE);
        }
        badgeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SlidingDrawerActivity.this, UserNotificationActivity.class));
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            }
        });
        return true;
    }

    private void getCitizenDetails() {
        ApiInterface mApiInterface = Utils.getInterfaceService();
        int userId = Utils.getUserDetails(activity).getCivilianId();
        Log.v(TAG, Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity));
        Call<JsonObject> mService = mApiInterface.getCitizenDetails(Constants.HEADER_TOKEN_BEARER
                        + Utils.getUserToken(activity),
                Constants.HEADER_ACCEPT, userId);
        String token = Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity);
        mService.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (activity != null) {
                        if (response.body() != null) {
                            JsonObject jsonObject = response.body();
                            if (jsonObject.get("response").getAsBoolean()) {

                                JsonObject departmentJsonArray = jsonObject.getAsJsonObject("data");
                                if (departmentJsonArray.size() > 0) {
                                    String mobile_no = departmentJsonArray.get("mobile_no").getAsString();

                                    if (!mobile_no.equals("") || !mobile_no.equals("null") || !mobile_no.isEmpty()) {

                                        ValidateMobile(mobile_no);

                                    }

                                    spotsDialog.dismiss();
                                }

                                // showLimitDialoge();

                            } else {

                                spotsDialog.dismiss();
                                Utils.showSnackBar(activity, parentLayout,
                                        jsonObject.get("message").getAsString().trim(), false);
                            }
                        } else {

                            spotsDialog.dismiss();
                            Utils.showSnackBar(activity, parentLayout,
                                    Constants.MESSAGE_NO_DATA_FOUND, false);
                        }
                    }
                } catch (Exception e) {

                    spotsDialog.dismiss();
                    e.printStackTrace();
                    Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (activity != null) {
                    spotsDialog.dismiss();

                    call.cancel();
                    t.printStackTrace();
                    Utils.showSnackBar(activity, parentLayout, Utils.failureMessage(t), false);
                }
            }
        });
    }

    public void ValidateMobile(String mobile_no) {

        String ss = "+_)(*&^%$#@!?><:;][}{/*12345)'";
        String ss2 = "+_)(*&^%$#@!?><:;][}{/*)'";

        String first_letter = String.valueOf(mobile_no.charAt(0));
        String second_letter = String.valueOf(mobile_no.charAt(1));
        if (ss.contains(first_letter) || ss2.contains(second_letter) ||mobile_no.length()<10) {
            MobileNumberDialogue();
        }
    }

    private void MobileNumberDialogue() {
        final Dialog countDialoge = new Dialog(SlidingDrawerActivity.this);
        countDialoge.setContentView(R.layout.mobile_dialogue);
        countDialoge.setCanceledOnTouchOutside(false);
        countDialoge.setCancelable(false);

        TextView dialog_title = countDialoge.findViewById(R.id.dialog_title);
        final EditText mobile_number = countDialoge.findViewById(R.id.mobile_number);
        ImageView imageView = countDialoge.findViewById(R.id.dialog_title_icon);


        Button ok_button = countDialoge.findViewById(R.id.ok_button);
        Button cancel_button = countDialoge.findViewById(R.id.cancel_button);
        ok_button.setText("SUBMIT");
        cancel_button.setText("CANCEL");
        dialog_title.setText("Alert");


        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                Utils.exitAnimation(activity);
                countDialoge.dismiss();
            }
        });

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mobile = mobile_number.getText().toString().trim();


                UpdateMobileNo(mobile);

                countDialoge.dismiss();


            }
        });

        countDialoge.show();
    }

    public void UpdateMobileNo(String mobile) {
        User mUser = Utils.getUserDetails(activity);
        int userId = mUser.getCivilianId();

        mApiInterface.updateUserMobile(
                userId, Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity),
                Constants.HEADER_ACCEPT,

                RequestBody.create(MediaType.parse("multipart/form-data"), mobile)
        )
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response != null) {
                                JsonObject mLoginObject = response.body();
                                if (mLoginObject.get("response").getAsBoolean()) {
                                    JsonObject userData = mLoginObject.get("user_data").getAsJsonObject();
                                    SharedPreferences.Editor editor = sharedPrefs.edit();
                                    editor.putString(Constants.USER_DATA, userData.toString());

                                    /**
                                     * Comment by dheeraj-gangwar on 2018-04-24
                                     *
                                     * This {@link Constants.USER_PROFILE_STATUS} is "MANAGED LOCALLY ONLY FOR FIRST TIME LOGIN"
                                     * in {@link LoginActivity} and the value of this {@link Constants.USER_PROFILE_STATUS}
                                     * will be updated here by 1 only after the user completely update their
                                     * profile in {@link UserProfileActivity}.
                                     */
                                    editor.putInt(Constants.USER_PROFILE_STATUS, profileStatus + 1);
                                    editor.apply();

                                    Utils.customToast(activity, mLoginObject.get("message").getAsString(), 1);
                                    spotsDialog.dismiss();

                                   // finish();

//                                    Intent intent = new Intent(UserProfileActivity.this, SlidingDrawerActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(intent);
//                                    Utils.enterAnimation(UserProfileActivity.this);
                                } else {
                                    spotsDialog.dismiss();
                                    Utils.showSnackBar(SlidingDrawerActivity.this, mDrawerLayout,
                                            mLoginObject.get("message").getAsString().trim(), false);
                                }
                            } else {
                                spotsDialog.dismiss();
                                Utils.showSnackBar(SlidingDrawerActivity.this, mDrawerLayout,
                                        Constants.MESSAGE_TRY_AGAIN_LATER, false);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            spotsDialog.dismiss();
                            Utils.showSnackBar(SlidingDrawerActivity.this, mDrawerLayout,
                                    Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        spotsDialog.dismiss();
                        call.cancel();
                        Utils.showSnackBar(activity, mDrawerLayout, Utils.failureMessage(t), false);
                    }
                });
    }

    //App Version Force Fully Update PopUp Dialog:-
    private void versionUpdate() {
        versionUpdateDialog = new Dialog(SlidingDrawerActivity.this, R.style.DialogSlideAnim);
        versionUpdateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        versionUpdateDialog.setContentView(R.layout.version_update_view);
        versionUpdateDialog.setCancelable(false);

        //Mapping Views:-
        update_version_btn = versionUpdateDialog.findViewById(R.id.update_version_btn);
        update_version_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Navigate to PlayStore for App Update:-
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

                versionUpdateDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            return false;
        } else if (item.getItemId() == R.id.action_filter) {
            return false;
        } else if (item.getItemId() == R.id.action_notification) {
            toolbar.setTitle(Constants.TOOLBAR_TITLE_NOTIFICATION);
            startActivity(new Intent(SlidingDrawerActivity.this, SlidingDrawerActivity.class));
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment frg = getSupportFragmentManager().findFragmentById(R.id.main_sliding_container);
        if (frg != null) {
            frg.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        }
//        else if (menu != null) {
//            if (!((android.support.v7.widget.SearchView) menu.findItem(R.id.action_search).getActionView()).getQuery().toString().isEmpty()){
//                getSupportFragmentManager().popBackStack();
//            }
        else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();


//            getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
//        }
        else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Utils.showSnackBar(SlidingDrawerActivity.this, findViewById(android.R.id.content),
                    "Please click BACK again to exit", false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        getCitizenDetails();
        getAllNotifications();
        UserApplication.getInstance().setConnectivityListener(this);
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
        finish();
        startActivity(refresh);

    }

}

