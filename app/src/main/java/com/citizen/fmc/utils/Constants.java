package com.citizen.fmc.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelUuid;
import android.provider.Settings;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.cardview.widget.CardView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.citizen.fmc.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import static android.content.Context.MODE_PRIVATE;

/**
 * ======> Created by dheeraj-gangwar on 2018-01-06 <======
 */

public class Constants {

    // TESTING  API
//    static final String BASE_URL = "http://ndmc-uat.msell.in/public/api/";
//    private static final String IMAGE_BASE_URL = "http://ndmc-uat.msell.in/";
//    public static final String IMAGE_URL = IMAGE_BASE_URL + "ndmc-uat";
//    public static final String IMAGE_PUBLIC_URL = IMAGE_BASE_URL + "ndmc-uat/public/";
//    public static final String MAP_VIEW = "Map View";
//    public static final String LATITUDE_LONGITUDE_LIST = "lat_lng_list";
//    public static final String SIDE_NAV = "side_nav";
    //LIVE API
    public static final String KEY_IS_BACK_CAMERA = "is_back_front";

    public static final String FILE_NAME_JPG = "temp_fmc.jpg";
    public static final String BASE_FOLDER_PATH_NDMC_OFFICER_APP = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + Constants.FOLDER_PATH_NDMC_CITIZEN;
    public static final String FOLDER_PATH_NDMC_CITIZEN = "/MCF/Citizen";
    static final String BASE_URL = "https://faridabad.msell.in/public/api/";
    static final String TREE_URL = "https://treecensus.ndmc.gov.in/TreeCensusAPI/";
    private static final String IMAGE_BASE_URL = "https://faridabad.msell.in/public/";
    public static final String IMAGE_URL = IMAGE_BASE_URL;
    public static final String IMAGE_PUBLIC_URL = IMAGE_BASE_URL;
    public static final String MAP_VIEW = "Map View";
    public static final String LATITUDE_LONGITUDE_LIST = "lat_lng_list";
    public static final String SIDE_NAV = "side_nav";


    static final String BASE_URL_PARKING = "https://www.neptunesaipark.com/services/parking/";

    public static final ParcelUuid Service_UUID = ParcelUuid.fromString("eb8b4426-9b92-4b9f-9302-a123f5f4d6d4");

    public static final int REQUEST_ENABLE_BT = 1;
    public static final String LOGIN_URL = "civil_login";
    public static final String REGISTER_URL = "civil_register";
    public static final String COMPLAINT_CATEGORIES_URL = "DepartmentAndSubType";
    public static final String COMPLAINT_CATEGORIES_AIR_POLLUTION_URL = "DepartmentAndSubTypeAirPollution";
    public static final String CITIZEN_COMPLAINT_COUNT_URL = "citizen_complaint_count";
    public static final String CITIZEN_COMPLAINT_CAMERA_TYPE_URL = "camera_setting_api";
    public static final String CITIZEN_DETAILS_URL = "citizen_personal_details";
    public static final String UPVOTESCOMPLAINTS = "upVotesComplaints";
    public static final String SUBMIT_COMPLAINT_URL = "RegisterComplaint";
    public static final String SUBMIT_FEEDBACK_URL = "AddFeedback";
    public static final String UPDATE_USER_PROFILE_URL = "civilianProfileUpdate";
    public static final String UPDATE_USER_MOBILE_URL = "updateUserMobile";
    public static final String ADD_COMPLAINT_COMMENT_URL = "AddCivilComplaintComments";
    public static final String CHANGE_PASSWORD_URL = "changePassword";
    public static final String GENERATE_OTP = "generateotp";
    public static final String OTP_EMAIL = "email";
    public static final String OTP_MOBILENO = "otpmob";
    public static final String NOTIFICATION_UNREAD_COUNT = "unread_count";
    public static final String AQI_STATUS = "aqi_status";
    public static final String RE_OPEN_COMPLAINT = "complaintReopen";
    public static final String GET_MY_COMPLAINTS_URL = "GetCivilComplaints";
    public static final String GET_CIVIL_USER_FEEDBACK_URL = "civil_user_feedback";
    public static final String GET_ALL_COMPLAINTS_URL = "getAllComplaintsCivilian";

    public static final String GET_PAGINATED_COMPLAINTS_URL = "PaginationComplaint";
    public static final String GET_ALL_COMMENTS_URL = "GetCivilComplaintComments";
    public static final String GET_DRAWER_MODULES_URL = "GetAppModuleNew";
    public static final String GET_HELP_LINE_NUMBERS_URL = "MCFHelpLine";
    public static final String GET_TREE_DATA = "GetTrees";
    public static final String GET_FAQs_URL = "FrequentlyQuestionAnswer";
    public static final String GET_WHATS_NEAR_ME_MODULES_OLD = "whatsNearMe";
    public static final String GET_WHATS_NEAR_ME_MODULES_NEW = "whatsNearMePaginate";
    public static final String GET_COMPLAINT_COMMENT = "GetCivilComplaintComments";
    public static final String GET_WHO_SERVE_YOU_DATA = "whoServeYou";
    public static final String GET_COMPLAINT_HISTORY = "ComplaintTracking";

    public static final String SHARED_PREF = "ndmc_citizen_pref";
    public static final String USER_TOKEN = "user_token";
    public static final String USER_PROFILE_STATUS = "user_profile_status";
    public static final String USER_LOGIN_TYPE = "login_type";
    public static final String USER_DATA = "user_data";

    public static final long SPLASH_TIME_OUT_MS = 1500;

    public static final int DEFAULT_INTEGER_VALUE = -901;

    public static final int APP_TYPE = 2;
    public static final int GOOGLE_SIGN_IN = 723;
    public static final int USER_EMAIL_SIGN_IN = 1;
    public static final int USER_SOCIAL_SIGN_IN = 2;
    public static final int USER_SOCIAL_MEDIA_TYPE_GOOGLE = 1;
    public static final int USER_SOCIAL_MEDIA_TYPE_FACEBOOK = 2;
    public static final String USER_ID_FORGOTPASSWORD = "userid_forgot";

    public static final String HEADER_TOKEN_BEARER = "Bearer ";
    public static final String HEADER_ACCEPT = "application/json";

    public static final String FILE_NAME_PNG = "temp_mcf.png";

    public static final String KEY_WEB_VIEW_URL = "web_view_url";
    public static final String KEY_TOOL_BAR_TITLE = "tool_bar_title";
    public static final String KEY_COMPLAINT_PAGE_TYPE = "page_type";
    public static final String KEY_COMPLAINT_DATA = "comp_data";
    public static final String KEY_COMPLAINT_NUM = "comp_num";
    public static final String KEY_STATUS_TITLE = "comp_status_title";
    public static final String KEY_STATUS_COLOR_CODE = "status_color_code";
    public static final String KEY_IMAGE_PATH = "image_path";
    public static final String KEY_MODULE_ID = "module_id";
    public static final String KEY_COMPLAINT_DAYS = "comp_duration";
    public static final String KEY_DEPARTMENT_ID = "dept_id";
    public static final String KEY_SUB_DEPARTMENT_ID = "sub_dept_id";
    public static final String VALUE_SHOW_COMPLAINT = "show_comp";
    public static final String KEY_MODULE_NAME = "module_name";
    public static final String KEY_MODULE_POSSITON = "position";
    public static final String TITLE_ALERT = "Alert !";
    public static final String TITLE_SORRY = "Sorry !";
    public static final String TITLE_SUCCESS = "Success !";
    public static final String TITLE_INFO = "Info !";
    public static final String TITLE_DRAFT_CREATED = "Draft Created !";

    public static final String MESSAGE_TRY_AGAIN_LATER = "Please try again later !";
    public static final String MESSAGE_SOMETHING_WENT_WRONG = "Something went wrong !";
    public static final String MESSAGE_NO_DATA_FOUND = "No data found !";
    public static final String MESSAGE_ADDED_SOON = "Added soon !";
    public static final String MESSAGE_SESSION_EXPIRED = "Your session has been expired, please login again !";
    public static final String MESSAGE_CHECK_INTERNET = "Please check your internet connection !";
    public static final String MESSAGE_INTERNET_NOT_CONNECTED = "No internet connection found !";
    public static final String MESSAGE_INTERNET_CONNECTED = "Internet connected !";

    public static final String CURRENT_WEATHER = "https://api.openweathermap.org/data/2.5/weather?";
    public static final String WEATHER_API_KEY = "9bfbcc2457b4e6842edd7562f593139d";

    //Module Title String:-
    public static final String HELPLINE = "Help Line";

    public static final int IMAGE_QUALITY = 20;
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";
    // firebase token
    public static final String FIREBASE_TOKEN = "token_firebase";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 1985;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 9547;
    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String NOTIFICATION_CHANNEL_ID = "ndmc_citizen_channel";

    public static final int MODULE_ID_WHATS_NEAR_ME = 24;
    public static final int MODULE_ID_WHO_SERVE_YOU = 27;
    public static final int MODULE_ID_COMPLAINTS = 8;
    public static final int MODULE_ID_AIRPOLLUTION = 68;
    public static final int MODULE_ID_WATERNSEWAGECOMPLAINTS= 69;
    public static final int MODULE_ID_SWACHTA = 63;

    public static final int MODULE_ID_CLEAN_NDMC = 16;
    public static final int MODULE_ID_WHATS_APP = 17;
    public static final int MODULE_ID_TRAFIC = 42;
    public static final int MODULE_ID_SMARTCITYPARKING = 43;
    public static final int MODULE_ID_HELPLINE = 58;
    public static final int MODULE_ID_TREE_NEAR_BY_ME = 65;

    public static final String TOOLBAR_TITLE_HOME = "Home";
    public static final String TOOLBAR_TITLE_FAQ = "FAQs";
    public static final String TOOLBAR_TITLE_FEEDBACK = "Write Feedback";
    public static final String TOOLBAR_TITLE_LOGOUT = "logout";
    public static final String TOOLBAR_TITLE_NOTIFICATION = "Notifications";
    public static final String TOOLBAR_TITLE_NEWS_UPDATES = "News & Updates";
    public static final String TOOLBAR_TITLE_UPDATE_PROFILE = "Update Profile";
    public static final String KEY_COMPLAINT_HOURS = "comp_hours";
    public static final String KEY_COMPLAINT_MINUTES = "comp_minutes";
    public static final String APP_DOWNLOAD = "isdownload";
    public static final String REGISTER_EMAIL = "register_email_id";
    public static final String REGISTER_MOBILENO = "register_mobile_no";

    public static final String TOOLBAR_CHANGE_LANGUAGE = "Change_language";
    public static final String MODULE_TITLE_Home="Home";
    public static final String MODULE_TITLE_Profile="Profile";
    public static final String MODULE_TITLE_Frequently_Asked_Ques="Frequently Asked Ques.";
    public static final String MODULE_TITLE_Feedback="Feedback";
    public static final String MODULE_TITLE_Rate_Us="Rate Us";
    public static final String MODULE_TITLE_Logout="Logout";
    public static final String MODULE_TITLE_Change_Language="Change Language";

    public static int MODULE_ID_NDMC=1;
    public static int MODULE_ID_CONNECT_WITH_NDMC=17;
    public static int MODULE_ID_SMART_METER=44;
    public static int MODULE_ID_QUICK_PAY=2;
    public static int MODULE_ID_STARTUPS=59;
    public static int MODULE_ID_SWIMMING_POOL=61;
    public static int MODULE_ID_WHO_SERVES_YOU=27;
    public static int MODULE_ID_E_HOSPITAL=6;
    public static int MODULE_ID_PENSIONER_PORTAL=19;
    public static int MODULE_ID_E_WASTE=9;
    public static int MODULE_ID_IMPORTANT_INFORMATION=11;
    public static int MODULE_ID_MONITOR_WATER_QUALITY=54;
    public static int MODULE_ID_DAILY_TREATED_WATER_TEST=55;
    public static int MODULE_ID_OPD_REGISTRATION=7;
    public static int MODULE_ID_SMART_BIKE=43;
    public static int MODULE_ID_PTU_DASHBOARD=20;
    public static int MODULE_ID_GARBAGE_VEHICLE_TRACKING=13;
    public static int MODULE_ID_EMPLOYEE_CORNER=5;
    public static int MODULE_ID_SPORTS_COACHING=4;
    public static int MODULE_ID_ALL_CITIZEN_SERVICES=23;
    public static int MODULE_ID_TRAFFIC_AND_PARKING=26;
    public static int MODULE_ID_DOOR_TO_DOOR_COLLECTION=63;
    public static int MODULE_ID_TREE_IN_NDMC=65;

    public static int SUBMODULE_ID_ABOUT_NDMC=2;
    public static int SUBMODULE_ID_DIRECTORY=6;
    public static int SUBMODULE_ID_NDMC_HISTORY=3;
    public static int SUBMODULE_ID_WEBSITE=1;
    public static int SUBMODULE_ID_WHAT_IN_THE_NEWS_TODAY=7;
    public static int SUBMODULE_ID_FACEBOOK=53;
    public static int SUBMODULE_ID_NDMC_TOLL_FREE_NUMBER=58;
    public static int SUBMODULE_ID_TWITTER=54;
    public static int SUBMODULE_ID_WRITE_FEEDBACK=57;
    public static int SUBMODULE_ID_Electricity_Bill=9;
    public static int SUBMODULE_ID_Estate_Bill=10;
    public static int SUBMODULE_ID_Property_tax_bill=11;
    public static int SUBMODULE_ID_Water_bill=8;
    public static int SUBMODULE_ID_NDMC_Smart_parking=44;
    public static int SUBMODULE_ID_Smart_parking_summary=43;
    public static int SUBMODULE_ID_Traffic=42;

    /*TODO: Complaint submodule_id */
    public static int Complaint_submodule_id_create_new_complaint =0;
    public static int Complaint_submodule_id_my_complaints =1;
    public static int Complaint_submodule_id_view_all_complaints =4;
    public static int Complaint_submodule_id_write_feedback =2;
    public static int Complaint_submodule_id_Ndmc_toll_free_number =3;


    public static int tree_near_by_me=0;
    public static int qr_near_bt_me=1;


    /*TODO: Helpline number id */
    public static int helpline_sub_id_AIIMS_Hospital=6;
    public static int helpline_sub_id_Ambulance_Service=5;
    public static int helpline_sub_id_CPH_Hospital=9;
    public static int helpline_sub_id_Delhi_Police_control_room=2;
    public static int helpline_sub_id_delhi_police_senior_citizen=11;
    public static int helpline_sub_id_delhi_police_traffic_unit=13;
    public static int helpline_sub_id_delhi_police_women_in_distress=3;
    public static int helpline_sub_id_disaster_helpline=10;
    public static int helpline_sub_id_fire_service=4;
    public static int helpline_sub_id_LHMC_KSC_HOSPITAL=12;
    public static int helpline_sub_id_LHMC_SSK_HOSPITAL=8;
    public static int helpline_sub_id_ndmc_control_room=1;

    public static int helpline_sub_id_RML_HOSPITAL=7;


    /*TODO: All Citizen Services*/
    public static int Citizen_Services_sub_id_baratghar=12;
    public static int Citizen_Services_sub_id_birth_certificate=14;
    public static int Citizen_Services_sub_id_building_plan_approval=14;
    public static int Citizen_Services_sub_id_child_name_inclusion=80;
    public static int Citizen_Services_sub_id_death_certificate=78;
    public static int Citizen_Services_sub_id_online_health_license=88;
    public static int Citizen_Services_sub_id_still_birth_certificate=79;
    public static int Citizen_Services_sub_id_yellow_fever_vaccination=13;

    /*TODO: Employee Corner Sub Modules*/
    public static int Employee_Corner_sub_id_attendance_dashboard=73;
    public static int Employee_Corner_sub_id_employee_corner=72;


    /*TODO: Important Information Sub Modules*/
    public static int Important_Information_sub_id_budget_2018_19=48;
    public static int Important_Information_sub_id_clean_ndmc=66;
    public static int Important_Information_sub_id_cyber_security=51;
    public static int Important_Information_sub_id_e_courts=47;
    public static int Important_Information_sub_id_faqs=65;
    public static int Important_Information_sub_id_fight_dengue_chikungunya=64;
    public static int Important_Information_sub_id_ndmc_walk=45;
    public static int Important_Information_sub_id_pm_relief_fund=84;
    public static int Important_Information_sub_id_rain_water_harvesting=50;
    public static int Important_Information_sub_id_view_medical_stock=46;
    public static int Important_Information_sub_id_virtual_tour_smart_toilets=71;

    /*TODO: What's Near Me Sub Modules*/
    public static int Whats_Near_me_id_ATMs=81;
    public static int Whats_Near_me_id_Baratghars=35;
    public static int Whats_Near_me_id_Bus_stands_dtc=22;
    public static int Whats_Near_me_id_Business_directory=90;
    public static int Whats_Near_me_id_citizen_facilitation_centers_ndmc=26;
    public static int Whats_Near_me_id_clinics_dispensaries=24;
    public static int Whats_Near_me_id_community_centers=39;
    public static int Whats_Near_me_id_construction_and_demolition_waste_malba_bins=19;
    public static int Whats_Near_me_id_Emabssies_high_commissions=38;

    public static int Whats_Near_me_id_hospitals=25;
    public static int Whats_Near_me_id_Litter_Bins=17;
    public static int Whats_Near_me_id_Markets=33;
    public static int Whats_Near_me_id_Metro_Stations=20;
    public static int Whats_Near_me_id_Monuments=40;
    public static int Whats_Near_me_id_NDMC_Gyms=32;
    public static int Whats_Near_me_id_ndmc_open_gyms_in_parks=31;
    public static int Whats_Near_me_id_ndmc_swimming=41;
    public static int Whats_Near_me_id_pharmacies=83;
    public static int Whats_Near_me_id_physiotherapy_centers=29;
    public static int Whats_Near_me_id_police_stations=28;
    public static int Whats_Near_me_id_postal_codes=82;
    public static int Whats_Near_me_id_public_toilets=21;
    public static int Whats_Near_me_id_schools=36;
    public static int Whats_Near_me_id_speed_limits=89;
    public static int Whats_Near_me_id_stadiums=37;
    public static int Whats_Near_me_id_trolley_data=59;
    public static int Whats_Near_me_id_twin_bins=60;
    public static int Whats_Near_me_id_veterinary_clients=27;
    public static int Whats_Near_me_id_water_atms=18;
    public static int TREE_NEAR_BY_ME=92;
    public static int QR_NEAR_BY_ME=93;


    //Weather Detail APIs:-
    public static final String TEMPERATURE_API = "http://apidev.accuweather.com/currentconditions/v1/187745.json?language=en&apikey=hoArfRosT1215";
    public static final String AQI_API = "https://api.openaq.org/v1/cities?country=IN";
    public static final String CITY_NAME = "Delhi";

    public static void customToast(Context mContext, String message, int type) {
        View toastLayout = LayoutInflater.from(mContext).inflate(R.layout.layout_custom_toast, null);

        ImageView customImageView = toastLayout.findViewById(R.id.custom_toast_image);
        TextView customTextView = toastLayout.findViewById(R.id.custom_toast_message);
        CardView customCardView = toastLayout.findViewById(R.id.toast_cardView);

        /** {@link type} is used to define the behaviour of this custom Toast.
         * 0 is for ERROR or if SOMETHING WENT WRONG,
         * 1 is for SUCCESS or TRUE or if SOMETHING WENT RIGHT,
         * 2 is for INFORMATION or if SOMETHING ELSE HAPPENS. */

        customTextView.setText(message);

        switch (type) {
            case 0:
                customImageView.setImageResource(R.drawable.ic_error);
                customCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorRed));
                break;

            case 1:
                customImageView.setImageResource(R.drawable.ic_tick);
                customCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorGreen));
                break;

            case 2:
                customImageView.setImageResource(R.drawable.ic_information);
                customCardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorDarkBlue));
                break;

            default:
                break;
        }

        Toast toast = new Toast(mContext);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        toast.show();
    }


    /*Get locally Stored User Detail*/
  /*  public static User getUserDetail(Context context) {
        User user = new User();
        if (context != null) {
            data = context.getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
            Gson gson = new Gson();
            String json = data.getString(USER_DATA, "");
            user = gson.fromJson(json, User.class);
        }
        return user;
    }*/

    public static DraweeController simpleDraweeController(String path, int width, int height) {
        return Fresco.newDraweeControllerBuilder()
                .setLowResImageRequest(ImageRequestBuilder
                        .newBuilderWithSource(Uri.parse(path))
                        .setResizeOptions(new ResizeOptions(width, height))
                        .build())
                .build();
    }

    public static String getDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        return formattedDate;

    }

    public static String formatDateAndTime(String date_old) {
        if (date_old != null && !date_old.isEmpty()) {
            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = dt.parse(date_old);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // *** same for the format String below
            SimpleDateFormat dt1 = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
            System.out.println(dt1.format(date));

            return dt1.format(date).replace("am", "AM").replace("pm", "PM");
        } else {
            return "NA";
        }
    }

    public static String failureMessage(Throwable t) {
        if (t instanceof TimeoutException || t instanceof ConnectException
                || t instanceof SocketException || t instanceof SocketTimeoutException) {
            return Constants.MESSAGE_CHECK_INTERNET;
        } else {
            return Constants.MESSAGE_SOMETHING_WENT_WRONG;
        }
    }


    public static Spanned setFontAwesome(Context context, TextView textView, String unicode) {
        try {
            Typeface iconFont = FontAwesomeManager.getTypeface(context, FontAwesomeManager.FONTAWESOME);
            FontAwesomeManager.applyTypeFace(textView, iconFont);
            return Html.fromHtml("&#x" + unicode);
        } catch (Exception e) {
            e.printStackTrace();
            return new SpannableString("*");
        }
    }


    public static String getDeviceId(Context context) {
        String deviceId = "";
        try {

            /*
             * getDeviceId() returns the unique device ID.
             * For example,the IMEI for GSM and the MEID or ESN for CDMA phones.
             */
            deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceId;
    }

    public static String getVersion(Context context) {
        String version = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static void setPreferenceIntegerData(Context context, String KEY_INT_PREF, int VALUE_INT_PREF) {
        SharedPreferences.Editor edit = context.getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE).edit();
        edit.putInt(KEY_INT_PREF, VALUE_INT_PREF);
        edit.apply();
    }

    public static void setPreferenceStringData(Context context, String KEY_STRING_PREF, String VALUE_STRING_PREF) {
        SharedPreferences.Editor edit = context.getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE).edit();
        edit.putString(KEY_STRING_PREF, VALUE_STRING_PREF);
        edit.apply();
    }
    public static String getPrefrence(Context context, String key) {
        SharedPreferences prefrence = context.getSharedPreferences(
                Constants.SHARED_PREF, 0);
        return prefrence.getString(key, "");
    }

    /*Show success or error notification Alert*/
   /* public static void showAlert(CoordinatorLayout cl, Context context, boolean isError, String title, String message) {
        Snackbar snackbar = Snackbar.make(cl, "", Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView textView = layout.findViewById(R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        // Inflate our custom view
        View snackView = LayoutInflater.from(context).inflate(R.layout.custom_alert, null);
        // Configure the view
//        LinearLayout parentLinear = snackView.findViewById(R.id.parent_linear);
        ImageView icon = snackView.findViewById(R.id.icon);
        TextView messageTitle = snackView.findViewById(R.id.messagetitle);
        TextView message_tv = snackView.findViewById(R.id.message);
        if (isError) {
            icon.setImageResource(R.drawable.ic_information);
            layout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed));
        } else {
            layout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGreen));
            icon.setImageResource(R.drawable.ic_tick);
        }

        messageTitle.setText(title);
        message_tv.setText(message);

        // Add the view to the Snackbar's layout
        layout.addView(snackView, 0);

        // Show the Snackbar
        snackbar.show();
    }
*/

    public static void showAlert(CoordinatorLayout cl, Context context, boolean isError, String title, String message) {
        Snackbar snackbar = Snackbar.make(cl, "", Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        // Set the default Snackbar background color to transparent (to show the custom layout properly)
        layout.setBackgroundColor(Color.TRANSPARENT);

        // Inflate the custom layout
        View snackView = LayoutInflater.from(context).inflate(R.layout.custom_alert, null);

        // Configure the custom view
        ImageView icon = snackView.findViewById(R.id.icon);
        TextView messageTitle = snackView.findViewById(R.id.messagetitle);
        TextView message_tv = snackView.findViewById(R.id.message);

        // Customize appearance based on error/success
        if (isError) {
            icon.setImageResource(R.drawable.ic_information); // Error icon
            snackView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRed)); // Error background
        } else {
            icon.setImageResource(R.drawable.ic_tick); // Success icon
            snackView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGreen)); // Success background
        }

        // Set text content
        messageTitle.setText(title);
        message_tv.setText(message);

        // Remove any existing child views in the Snackbar layout
        layout.removeAllViews();

        // Add the custom view to the Snackbar layout
        layout.addView(snackView);

        // Show the Snackbar
        snackbar.show();
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static void setRatingBarColor(Context mContext, RatingBar ratingBar) {
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(mContext.getResources().getColor(R.color.gold),
                PorterDuff.Mode.SRC_ATOP); // for filled stars
        stars.getDrawable(1).setColorFilter(mContext.getResources().getColor(R.color.colorGrey),
                PorterDuff.Mode.SRC_ATOP); // for half filled stars
        stars.getDrawable(0)
                .setColorFilter(mContext.getResources().getColor(R.color.colorGrey),
                        PorterDuff.Mode.SRC_ATOP); // for empty stars
    }
    public static String generateUniqueId(Integer userId) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
        Log.v("KEY_UNIQUE_ID===>", df.format(c.getTime()));
        return df.format(c.getTime());
    }
    public static double getFileSize(File file) {
        double bytes, kilobytes, megabytes = 0, gigabytes, terabytes, petabytes, exabytes, zettabytes, yottabytes;
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        if (file.exists()) {
            bytes = file.length();
            kilobytes = (bytes / 1024);
            megabytes = (kilobytes / 1024);
            gigabytes = (megabytes / 1024);
            terabytes = (gigabytes / 1024);
            petabytes = (terabytes / 1024);
            exabytes = (petabytes / 1024);
            zettabytes = (exabytes / 1024);
            yottabytes = (zettabytes / 1024);

            System.out.println("File size in BYTES : " + decimalFormat.format(bytes));
            System.out.println("File size in KILOBYTES : " + decimalFormat.format(kilobytes));
            System.out.println("File size in MEGABYTES : " + decimalFormat.format(megabytes));
            System.out.println("File size in GIGABYTES : " + decimalFormat.format(gigabytes));
            System.out.println("File size in TERABYTES : " + decimalFormat.format(terabytes));
            System.out.println("File size in PETABYTES : " + decimalFormat.format(petabytes));
            System.out.println("File size in EXABYTES : " + decimalFormat.format(exabytes));
            System.out.println("File size in ZETTABYTES : " + decimalFormat.format(zettabytes));
            System.out.println("File size in YOTTABYTES : " + decimalFormat.format(yottabytes));
        } else {
            System.out.println("File does not exists!");
        }

        return megabytes;
    }

}
