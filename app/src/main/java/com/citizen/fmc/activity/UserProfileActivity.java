package com.citizen.fmc.activity;

import static android.app.PendingIntent.getActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.database.DBHelper;
import com.citizen.fmc.model.ImageTextModel;
import com.citizen.fmc.model.User;
import com.citizen.fmc.utils.ApiInterface;
import com.citizen.fmc.utils.ButtonView;
import com.citizen.fmc.utils.ConnectivityReceiver;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.ipaulpro.afilechooser.utils.FileUtils;
//import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ======> Created by dheeraj-gangwar on 2018-04-23 <======
 */

@SuppressLint("SimpleDateFormat")
public class UserProfileActivity extends AppCompatActivity {
    private FragmentActivity activity;
    private String TAG = getClass().getSimpleName();
    private RelativeLayout parentLayout;
    private CircleImageView userImageView;
    private SimpleDraweeView userSDV;
    private Button editPhotoButton;
    private EditText firstNameET;
    private EditText lastNameET;
    private EditText mobileNumET;
    private EditText emailET;
    private EditText alternateMobileET;
    private EditText dobET;
    private EditText confirmPasswordET;
    private EditText et_water_consumer_number;
    private EditText et_electricity_consumer_no;
    private Spinner genderSpinner;
    private ButtonView updateButton;
    private int genderId;
    private int userId;
    private int imageStatus = 0;

    private SpotsDialog spotsDialog;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String dateOfBirth = "";
    private Calendar mCalendar;
    private final int IMAGE_PICK_REQUEST_CODE = 7672;
    private File capturedFile = null;
    private boolean hasImage = false;
    private int profileStatus;
    private TextInputLayout oldPasswordIL;
    private TextInputLayout newPasswordIL;
    private TextInputLayout confirmPasswordIL;
    private EditText oldPasswordET;
    private EditText newPasswordET;
    private EditText userNameET;
    private boolean doubleBackToExitPressedOnce = false;
    private ApiInterface mApiInterface;
    private SharedPreferences sharedPrefs;
    private int userLoginType;
    private Dialog changePasswordDialog;

    private Uri resultUri;

    private ImageView capturedImage;
    private File _capturedFile;
    private String imagePath;
    private TextView image_capture_text_view;
    private List<String> imagesList = new ArrayList<>();
    private ImageButton capture_image_button;






    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (profileStatus == 0) {
            if (doubleBackToExitPressedOnce) {
                finish();
                Utils.exitAnimation(this);
            }

            this.doubleBackToExitPressedOnce = true;
            Utils.showSnackBar(UserProfileActivity.this, findViewById(android.R.id.content),
                    "Please click BACK again to exit", false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            finish();
            Utils.exitAnimation(this);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = UserProfileActivity.this;
        setContentView(R.layout.activity_user_profile_simple);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(Constants.TOOLBAR_TITLE_UPDATE_PROFILE);
        setSupportActionBar(toolbar);

        parentLayout = findViewById(R.id.parent_layout);
        userImageView = findViewById(R.id.user_profile_image_view);
        userSDV = findViewById(R.id.user_profile_sdv);
        editPhotoButton = findViewById(R.id.edit_photo_button);
        firstNameET = findViewById(R.id.first_name_edit_text);
        lastNameET = findViewById(R.id.last_name_edit_text);
        userNameET = findViewById(R.id.user_name_edit_text);
        emailET = findViewById(R.id.email_edit_text);
        mobileNumET = findViewById(R.id.user_mobile_number_edit_text);
        alternateMobileET = findViewById(R.id.alternate_mobile_edit_text);
        dobET = findViewById(R.id.dob_edit_text);
//        et_water_consumer_number = findViewById(R.id.et_water_consumer_number);
//        et_electricity_consumer_no = findViewById(R.id.et_electricity_consumer_no);
        genderSpinner = findViewById(R.id.gender_spinner);
        updateButton = findViewById(R.id.update_button);

        mCalendar = Calendar.getInstance();
        dobET.setFocusable(false);
        userNameET.setFocusable(false);

        spotsDialog = new SpotsDialog(activity,
                getResources().getString(R.string.please_wait_dialog_text),
                R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);

        User mUser = Utils.getUserDetails(activity);
        mApiInterface = Utils.getInterfaceService();
        sharedPrefs = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);

        /**
         * Getting value of {@link Constants.USER_PROFILE_STATUS} from shared preference
         */
        profileStatus = sharedPrefs.getInt(Constants.USER_PROFILE_STATUS, Constants.DEFAULT_INTEGER_VALUE);
        Log.v(TAG, "Login Count: " + profileStatus);

        userLoginType = sharedPrefs.getInt(Constants.USER_LOGIN_TYPE, Constants.DEFAULT_INTEGER_VALUE);

        /**
         * Checking for first time user login,
         * this is mandatory for user to completely fill their profile details
         * for first time login, that why this back pressed is disabled only
         * for first time unless {@link Constants.USER_PROFILE_STATUS} is not 1.
         * For more information, check loginUserAccount method in {@link LoginActivity}.
         * {@code onBackPressed} is also disabled.
         */
        if (profileStatus == 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            /**
             * Information dialog for user while logging in only for the first time.
             */
            firstLoginInfoDialog().show();
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        try {
            userId = mUser.getCivilianId();
            String userName = mUser.getUserName();
            String firstName = mUser.getFirstName();
            final String lastName = mUser.getLastName();
            String email = mUser.getEmail();
            String mobileNum = mUser.getMobileNum();
            String alternateMobNum = mUser.getAlternateMobileNum();
            String dob = mUser.getDateOfBirth();
            String userProfileImagePath = Constants.IMAGE_PUBLIC_URL + mUser.getProfileImagePath();
            genderId = mUser.getGenderId();
            String waterConsumerNo = mUser.getWaterConsumerNo();
            String electricityConsumerNo = mUser.getElectricityConsumerNo();

            userSDV.setController(userSDVController(userProfileImagePath));
            firstNameET.setText(firstName != null && !isFieldEmpty(firstName) ? firstName : "");
            lastNameET.setText(lastName != null && !isFieldEmpty(lastName) ? lastName : "");
            userNameET.setText(userName != null && !isFieldEmpty(userName) ? userName : "");
            emailET.setText(email != null && !isFieldEmpty(email) ? email : "");
//            et_water_consumer_number.setText(waterConsumerNo != null && !isFieldEmpty(waterConsumerNo) ? waterConsumerNo : "");
//            et_electricity_consumer_no.setText(electricityConsumerNo != null && !isFieldEmpty(electricityConsumerNo) ? electricityConsumerNo : "");
            mobileNumET.setText(mobileNum != null && !isFieldEmpty(mobileNum) ? mobileNum : "");
            alternateMobileET.setText(alternateMobNum != null && !isFieldEmpty(alternateMobNum) ? alternateMobNum : "");
            dobET.setText(dob != null && !isFieldEmpty(dob) ? Utils.formatServerToUserDate(dob) : "");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        setGenderSpinner();

        editPhotoButtonOnClick();

        dobETOnClick();

        updateUserProfileButtonOnClick();
    }

    /* ====================== Creating dialog for first time info ====================== */
    private Dialog firstLoginInfoDialog() {
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

        titleIcon.setImageResource(R.drawable.ic_information);
        titleText.setText(R.string.Information);
        dialogMessage.setText(R.string.info_text_first_login);

        cancelButton.setText(R.string.not_now);
        OKButton.setText(R.string.sure);
        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                finish();
                Utils.exitAnimation(activity);
            }
        });

        return dialog;
    }
    /* ================================================================================================= */


    /* ================================================================================================= */
    private void updateUserProfileButtonOnClick() {
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lastName = lastNameET.getText().toString().trim();
                String alternateMobNum = alternateMobileET.getText().toString().trim();
                String dob = dobET.getText().toString().trim();
//                String waterConsumerNo = et_water_consumer_number.getText().toString().trim();
//                String electricityConsumerNo = et_electricity_consumer_no.getText().toString().trim();
                MultipartBody.Part partImageFile;
                if (imageStatus == 1) {
                    partImageFile = MultipartBody.Part.createFormData("image_source", capturedFile.getName(),
                            RequestBody.create(MediaType.parse("multipart/form-data"), capturedFile));
                } else {
                    partImageFile = MultipartBody.Part.createFormData("image_source", "");
                }

                if (validateFields()) {
                    if (ConnectivityReceiver.isConnected()) {
                        spotsDialog.show();
                        updateUserProfile(
                                firstNameET.getText().toString().trim(),
                                lastName,
                                mobileNumET.getText().toString(),
                                alternateMobNum,
                                emailET.getText().toString().trim(),
                                genderId,
                                dateOfBirth, imageStatus, partImageFile);
                    } else {
                        Utils.showSnackBar(activity, parentLayout, Constants.MESSAGE_CHECK_INTERNET, false);
                    }
                }
            }
        });
    }
    /* ================================================================================================= */


    /* ================================================================================================= */
    private DraweeController userSDVController(String imagePath) {
        return Fresco.newDraweeControllerBuilder()
                .setControllerListener(new ControllerListener<ImageInfo>() {
                    @Override
                    public void onSubmit(String id, Object callerContext) {

                    }

                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        Log.v(TAG, "Image loaded successfully....!");
                        hasImage = true;
                    }

                    @Override
                    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {

                    }

                    @Override
                    public void onIntermediateImageFailed(String id, Throwable throwable) {

                    }

                    @Override
                    public void onFailure(String id, Throwable throwable) {
                        Log.e(TAG, "Image loading failed....!");
                        hasImage = false;
                    }

                    @Override
                    public void onRelease(String id) {

                    }
                })
                .setUri(Uri.parse(imagePath))
                .build();
    }
    /* ================================================================================================= */


    /* ====================== check if mobile number have 10 characters or not ====================== */
    private boolean isValidMobile(String mobileNum) {
        return mobileNum.length() == 10;
    }
    /* ================================================================================================= */


    /* ====================== Validation for all fields before updating profile ====================== */
    private boolean validateFields() {
//        if (!hasImage) {
//            Utils.showSnackBar(activity, parentLayout, "Profile image is required !", false);
//            return false;
//        }
//
        if (isFieldEmpty(firstNameET.getText().toString().trim())) {
            Utils.showSnackBar(activity, parentLayout, "First name is required !", false);
            return false;
        } else if (isFieldEmpty(emailET.getText().toString().trim())) {
            Utils.showSnackBar(activity, parentLayout, "E-mail is required !", false);
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailET.getText().toString().trim()).matches()) {
            Utils.showSnackBar(activity, parentLayout, "E-mail is not valid !", false);
            return false;
        } else if (isFieldEmpty(mobileNumET.getText().toString())) {
            Utils.showSnackBar(activity, parentLayout, "Mobile No. is required !", false);
            return false;
        } else if (!isValidMobile(mobileNumET.getText().toString().trim())) {
            Utils.showSnackBar(activity, parentLayout, "Mobile No. is not valid !", false);
            return false;
        }
//        else if (!isFieldEmpty(alternateMobileET.getText().toString().trim()) && !isValidMobile(alternateMobileET.getText().toString().trim())) {
//            Utils.showSnackBar(activity, parentLayout, "Alternate Mobile No. is not valid !", false);
//            return false;
//        }

        return true;
    }
    /* ================================================================================================= */


    /* ====================== check if the field is empty or not ====================== */
    private boolean isFieldEmpty(String str) {
        return str.isEmpty();
    }
    /* ================================================================================================= */


    /* ====================== Checking username whether it is a mobile number or an email ====================== */
    private boolean isMobileNum(String str) {
        try {
            long test = Long.parseLong(str.trim());
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }
    /* ================================================================================================= */


    /* ====================== check if password have 8-20 characters ====================== */
    private boolean validatePasswordLength(String str) {
        return str.length() > 7 && str.length() < 21;
    }
    /* ================================================================================================= */


    /* ====================== Setting gender spinner ====================== */
    private void setGenderSpinner() {
        final ArrayList<ImageTextModel> genderList = new DBHelper(UserProfileActivity.this).getAllGender();
        ArrayList<String> list = new ArrayList<>();
        for (int x = 0; x < genderList.size(); x++) {
            list.add(genderList.get(x).getTitle());
        }

        genderSpinner.setAdapter(new ArrayAdapter<>(UserProfileActivity.this,
                android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, list));

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genderId = genderList.get(position).getId();
                Log.v(TAG, "GenderId: " + genderId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // For set the pre
        if (genderId > 0) {
            for (int x = 0; x < genderList.size(); x++) {
                if (genderList.get(x).getId() == genderId) {
                    genderSpinner.setSelection(x);
                }
            }
        }
    }
    /* ================================================================================================= */


    /* ====================== Edit photo button onClick listener ====================== */
    private void editPhotoButtonOnClick() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R)
        {
            editPhotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TedPermission.create()
                            .setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
//                                    startActivityForResult(createChooserIntent(), IMAGE_PICK_REQUEST_CODE);
                                    pickFile();
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
                            .setPermissions(Manifest.permission.CAMERA)
                            .check();


                }
            });
        }
        else {
            editPhotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                        TedPermission.create()
                                .setPermissionListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted() {
//                                    startActivityForResult(createChooserIntent(), IMAGE_PICK_REQUEST_CODE);
                                        pickFile();
                                    }

                                    @Override
                                    public void onPermissionDenied(List<String> deniedPermissions) {
                                        Utils.customToast(activity, "PERMISSION DENIED", 0);

                                    }

                                    /*@Override
                                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                                        Utils.customToast(activity, "PERMISSION DENIED", 0);
                                    }*/
                                })
                                .setGotoSettingButtonText("SETTINGS")
                                .setDeniedMessage("If you reject permission,you can not use this service" +
                                        "\n\nPlease turn on permissions at [Settings] > [Permission]")
                                .setPermissions(Manifest.permission.CAMERA)
                                .check();
                    } else {
                        TedPermission.create()
                                .setPermissionListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted() {
//                                    startActivityForResult(createChooserIntent(), IMAGE_PICK_REQUEST_CODE);
                                        pickFile();
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
                                .setPermissions(Manifest.permission.CAMERA,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .check();
                    }
                }
            });
        }
    }
    /* ================================================================================================= */


    /* ====================== D.O.B. text view onClick listener ====================== */
    private void dobETOnClick() {
        dobET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareDatePicker();
            }
        });
    }
    /* ================================================================================================= */


    /* ====================== Creating date picker for user D.O.B. ====================== */
    private void prepareDatePicker() {
        mCalendar = Calendar.getInstance();
        final DatePickerDialog pickerDialog = new DatePickerDialog(activity, R.style.dialogSlideAnimation, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                try {
                    dateOfBirth = dateFormat.format(dateFormat.parse(year + "-" + (month + 1) + "-" + dayOfMonth));
                    dobET.setText(Utils.formatServerToUserDate(dateOfBirth));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        pickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        pickerDialog.show();
    }
    /* ================================================================================================= */


    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        capturedFile = new File(activity.getExternalCacheDir(), Constants.FILE_NAME_PNG);
        return FileProvider.getUriForFile(activity,
                activity.getPackageName() + ".provider", capturedFile);
    }
    /* ================================================================================================= */

    @Override
    protected void onResume() {
        super.onResume();
        getCitizenDetails();
    }

    /* ====================== Image chooser intent method ====================== */
    public Intent createChooserIntent() {
        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = activity.getPackageManager();

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
            /*intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }*/
            new Intent(UserProfileActivity.this,
                    RetailerImageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

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


    /* ================================================================================================= */
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
        final Dialog countDialoge = new Dialog(UserProfileActivity.this);
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

                                    finish();

                                    Intent intent = new Intent(UserProfileActivity.this, SlidingDrawerActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    Utils.enterAnimation(UserProfileActivity.this);
                                } else {
                                    spotsDialog.dismiss();
                                    Utils.showSnackBar(UserProfileActivity.this, parentLayout,
                                            mLoginObject.get("message").getAsString().trim(), false);
                                }
                            } else {
                                spotsDialog.dismiss();
                                Utils.showSnackBar(UserProfileActivity.this, parentLayout,
                                        Constants.MESSAGE_TRY_AGAIN_LATER, false);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            spotsDialog.dismiss();
                            Utils.showSnackBar(UserProfileActivity.this, parentLayout,
                                    Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        spotsDialog.dismiss();
                        call.cancel();
                        Utils.showSnackBar(activity, parentLayout, Utils.failureMessage(t), false);
                    }
                });
    }


    /* ====================== Request method to update user profile ====================== */
    private void updateUserProfile(String firstName, String lastName, String mobileNum, String alternateNum,
                                   String email, int genderId, String dob, int imageStatus, MultipartBody.Part imageFile
    ) {
        mApiInterface.updateUserProfile(
                userId, Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity),
                Constants.HEADER_ACCEPT,
                RequestBody.create(MediaType.parse("multipart/form-data"), firstName),
                RequestBody.create(MediaType.parse("multipart/form-data"), lastName),
                RequestBody.create(MediaType.parse("multipart/form-data"), mobileNum),
                RequestBody.create(MediaType.parse("multipart/form-data"), alternateNum),
                RequestBody.create(MediaType.parse("multipart/form-data"), email),
                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(genderId)),
                RequestBody.create(MediaType.parse("multipart/form-data"), dob),
                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(imageStatus)),
                imageFile)
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

                                    finish();

                                    Intent intent = new Intent(UserProfileActivity.this, SlidingDrawerActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    Utils.enterAnimation(UserProfileActivity.this);
                                } else {
                                    spotsDialog.dismiss();
                                    Utils.showSnackBar(UserProfileActivity.this, parentLayout,
                                            mLoginObject.get("message").getAsString().trim(), false);
                                }
                            } else {
                                spotsDialog.dismiss();
                                Utils.showSnackBar(UserProfileActivity.this, parentLayout,
                                        Constants.MESSAGE_TRY_AGAIN_LATER, false);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            spotsDialog.dismiss();
                            Utils.showSnackBar(UserProfileActivity.this, parentLayout,
                                    Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        spotsDialog.dismiss();
                        call.cancel();
                        Utils.showSnackBar(activity, parentLayout, Utils.failureMessage(t), false);
                    }
                });
    }
    /* ================================================================================================= */


    /* ====================== Confirmation dialog box for logout ====================== */
    private void changeUserPassword(String oldPassword, String newPassword) {
        mApiInterface.changePassword(
                userId,
                Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity),
                Constants.HEADER_ACCEPT,
                oldPassword, newPassword)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (response != null) {
                                JsonObject mLoginObject = response.body();
                                if (mLoginObject.get("response").getAsBoolean()) {
                                    Utils.customToast(activity, mLoginObject.get("message").getAsString(), 1);
                                    spotsDialog.dismiss();
                                    changePasswordDialog.dismiss();

                                    finish();

                                    Intent intent = new Intent(UserProfileActivity.this, SlidingDrawerActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    Utils.enterAnimation(UserProfileActivity.this);
                                } else {
                                    spotsDialog.dismiss();
                                    Utils.customToast(activity, mLoginObject.get("message").getAsString().trim(), 2);
                                }
                            } else {
                                spotsDialog.dismiss();
                                Utils.customToast(activity, Constants.MESSAGE_TRY_AGAIN_LATER, 2);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            spotsDialog.dismiss();
                            Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        spotsDialog.dismiss();
                        call.cancel();
                        Utils.customToast(activity, Utils.failureMessage(t), 0);
                    }
                });
    }
    /* ================================================================================================= */


    /* ====================== Creating dialog for changing user password ====================== */
    private void editPasswordDialog() {
        changePasswordDialog = new Dialog(activity, R.style.dialogSlideAnimation);
        changePasswordDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        changePasswordDialog.setContentView(R.layout.layout_dialog_change_password);
        changePasswordDialog.setCancelable(false);
        oldPasswordIL = changePasswordDialog.findViewById(R.id.old_password_input_layout);
        newPasswordIL = changePasswordDialog.findViewById(R.id.new_password_input_layout);
        confirmPasswordIL = changePasswordDialog.findViewById(R.id.confirm_password_input_layout);
        oldPasswordET = changePasswordDialog.findViewById(R.id.old_password_edit_text);
        newPasswordET = changePasswordDialog.findViewById(R.id.new_password_edit_text);
        confirmPasswordET = changePasswordDialog.findViewById(R.id.confirm_new_password_edit_text);
        ButtonView confirmButton = changePasswordDialog.findViewById(R.id.confirm_button);
        ButtonView cancelButton = changePasswordDialog.findViewById(R.id.cancel_button);

        validatePasswordFields();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordDialog.dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatePasswordFields()) {
                    spotsDialog.show();
                    changeUserPassword(oldPasswordET.getText().toString(), confirmPasswordET.getText().toString());
                }
            }
        });

        changePasswordDialog.show();
    }
    /* ================================================================================================= */


    /* ====================== Validation before changing user password ====================== */
    private boolean validatePasswordFields() {
        oldPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isFieldEmpty(s.toString())) {
                    oldPasswordIL.setErrorEnabled(true);
                    oldPasswordIL.setError("Required Field !");
                } else if (!validatePasswordLength(s.toString())) {
                    oldPasswordIL.setErrorEnabled(true);
                    oldPasswordIL.setError("Password length must be of 8-20 characters !");
                } else {
                    oldPasswordIL.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        newPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isFieldEmpty(s.toString())) {
                    newPasswordIL.setErrorEnabled(true);
                    newPasswordIL.setError("Required Field !");
                } else if (!validatePasswordLength(s.toString())) {
                    newPasswordIL.setErrorEnabled(true);
                    newPasswordIL.setError("Password length must be of 8-20 characters !");
                } else {
                    newPasswordIL.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        confirmPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isFieldEmpty(s.toString())) {
                    confirmPasswordIL.setErrorEnabled(true);
                    confirmPasswordIL.setError("Required Field !");
                } else if (!validatePasswordLength(s.toString())) {
                    confirmPasswordIL.setErrorEnabled(true);
                    confirmPasswordIL.setError("Password length must be of 8-20 characters !");
                } else {
                    confirmPasswordIL.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (isFieldEmpty(oldPasswordET.getText().toString())) {
            oldPasswordIL.setErrorEnabled(true);
            oldPasswordIL.setError("Required Field !");
            return false;
        } else if (isFieldEmpty(newPasswordET.getText().toString())) {
            newPasswordIL.setErrorEnabled(true);
            newPasswordIL.setError("Required Field !");
            return false;
        } else if (isFieldEmpty(confirmPasswordET.getText().toString())) {
            confirmPasswordIL.setErrorEnabled(true);
            confirmPasswordIL.setError("Required Field !");
            return false;
        } else if (!newPasswordET.getText().toString().equals(confirmPasswordET.getText().toString())) {
            Utils.customToast(activity, "New password not matched !", 2);
            return false;
        }

        return true;
    }
    /* ================================================================================================= */


    /* ====================== Confirmation dialog box for logout ====================== */
    public void logoutDialog() {
        final Dialog dialog = new Dialog(activity, R.style.dialogSlideAnimation);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_logout);
        dialog.setCancelable(false);

        TextView titleText = dialog.findViewById(R.id.dialog_title);
        TextView dialogMessage = dialog.findViewById(R.id.dialog_title_message);
        TextView dialogInfoMessage = dialog.findViewById(R.id.dialog_info_message);
        ImageView titleIcon = dialog.findViewById(R.id.dialog_title_icon);
//        image_capture_text_view=dialog.findViewById(R.id.image_capture_text_view);
//        capture_image_button = dialog.findViewById(R.id.capture_image_button);


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
                Utils.logoutUser(activity);
                finish();
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    /* ================================================================================================= */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_password, menu);//Menu Resource, Menu

        if (profileStatus == 0) {
            menu.findItem(R.id.action_edit_password).setVisible(true);
            menu.findItem(R.id.action_logout).setVisible(true);
        } else {
            /**
             * Comment by dheeraj-gangwar on 2018-04-28
             *
             * This {@link Constants.USER_LOGIN_TYPE} is MANAGED LOCALLY AT LOGIN,
             *
             * if {@link userLoginType} is null then it means user logged in
             * by entering username and password, i.e, {@link Constants.USER_LOGIN_TYPE}
             * is equals {@link Constants.USER_EMAIL_SIGN_IN}(In this case, USER CAN CHANGE PASSWORD),
             *
             * if {@link userLoginType} is not null then it is a social media login, i.e, {@link Constants.USER_LOGIN_TYPE}
             * is equals {@link Constants.USER_SOCIAL_SIGN_IN}(In this case, USER CANNOT CHANGE PASSWORD).
             *
             * For more information, check loginUserAccount method in {@link LoginActivity}.
             */
            if (userLoginType == Constants.USER_SOCIAL_SIGN_IN) {
                menu.findItem(R.id.action_edit_password).setVisible(false);
            } else {
                menu.findItem(R.id.action_edit_password).setVisible(true);
            }
            menu.findItem(R.id.action_logout).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit_password) {
            editPasswordDialog();
            return false;
        } else if (item.getItemId() == R.id.action_logout) {
            logoutDialog();
            return false;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), result -> {
        if (result.isSuccessful()) {
            Bitmap cropped = BitmapFactory.decodeFile(result.getUriFilePath(this, true));
        }

        if (result.isSuccessful()) {
            Uri resultUri = result.getOriginalUri();
            // Get the File path from the Uri
            String path = FileUtils.getPath(this, resultUri);

            // Alternatively, use FileUtils.getFile(Context, Uri)
            if (path != null) {
                capturedFile = FileUtils.getFile(activity, resultUri);
                double size = Constants.getFileSize(capturedFile);
                Log.v(TAG, "file_size(MB): " + size
                        + "\n file_name: " + capturedFile.getName()
                        + "\n file_path: " + path);
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeFile(result.getUriFilePath(this, true));
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
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Bitmap bitmap;
            switch (requestCode) {
                case IMAGE_PICK_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
                        String imageStatus = Constants.getPrefrence(UserProfileActivity.this,"image_status");
                        if (imageStatus.equalsIgnoreCase("1"))
                        {
                            Bitmap bm=null;
                            if (data != null) {
                                if(Build.VERSION.SDK_INT < 29  ) {
                                    try {
                                        bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        bm = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), data.getData()));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                if (bm != null) {
                                    bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                                }
                            }
                            File filepath = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),"BaseProductAllImage");
                            if (!filepath.exists())
                                filepath.mkdirs();
                            String unique_id = Constants.generateUniqueId(Integer.valueOf(Constants.getPrefrence(this, "User_id")));
                            File file = new File(filepath, "/"+unique_id+".png");
                            FileOutputStream outputStream = null;
                            try {
                                outputStream = new FileOutputStream(file);
                                if (bm != null) {
                                    bm.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                }
                                String image_path="";

                                image_path = filepath.getAbsolutePath()+ File.separator
                                        +unique_id+".png";
                                final Uri _mainUri;
                                File imgFile = new  File(image_path);
                                if (data != null) {
                                    _mainUri = FileUtils.getUri(imgFile);
                                } else {
                                    _mainUri = FileUtils.getUri(capturedFile);
                                }

                                if (_mainUri != null) {
                                    // start cropping activity for pre-acquired image saved on the device
                                   /* CropImage.activity(_mainUri)
                                            .setOutputCompressQuality(Constants.IMAGE_QUALITY)
                                            .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                                            .start(activity);*/

                                    launchImageCropper(_mainUri);

                                } else {
                                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                            Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
                                }
                                outputStream.flush();
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            final Uri _mainUri;
                            Bundle bundle = data.getExtras();
                            String image_path="";
                            image_path = bundle.getString("Retailer_Image");
                            File imgFile = new  File(image_path);
                            if (data != null) {
                                _mainUri = FileUtils.getUri(imgFile);
                            } else {
                                _mainUri = FileUtils.getUri(capturedFile);
                            }

                            if (_mainUri != null) {
                                // start cropping activity for pre-acquired image saved on the device
                                /*CropImage.activity(_mainUri)
                                        .setOutputCompressQuality(Constants.IMAGE_QUALITY)
                                        .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                                        .start(activity);*/

                                launchImageCropper(_mainUri);

                            } else {
                                Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                        Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
                            }
                        }
                    }
                    break;

/*                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri resultUri = result.getUri();

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
                            userImageView.setImageBitmap(bitmap);
                            userSDV.setVisibility(View.GONE);
                            userImageView.setVisibility(View.VISIBLE);
                            imageStatus = 1;
                            hasImage = true;
                        }
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        result.getError().printStackTrace();
                        Utils.showSnackBar(activity, parentLayout,
                                Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
                        imageStatus = 0;
                    }
                    break;*/

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void pickFile() {
        final CharSequence[] options = {"Choose from Gallery","Camera","Cancel" };
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Select Image!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Choose from Gallery"))
                {

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    Constants.setPreferenceStringData(UserProfileActivity.this,"image_status","1");
                    startActivityForResult(Intent.createChooser(intent, "Select File"),IMAGE_PICK_REQUEST_CODE);
                }
                else if(options[item].equals("Camera"))
                {try {
                    Intent in = new Intent(UserProfileActivity.this,
                            RetailerImageActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Constants.setPreferenceStringData(UserProfileActivity.this,"image_status","2");
                    startActivityForResult(in, IMAGE_PICK_REQUEST_CODE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                }
                else if(options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
}
