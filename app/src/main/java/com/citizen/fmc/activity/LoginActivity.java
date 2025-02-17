package com.citizen.fmc.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.citizen.fmc.R;
import com.citizen.fmc.database.DBHelper;
import com.citizen.fmc.utils.ApiInterface;
import com.citizen.fmc.utils.ConnectivityReceiver;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ======> Created by dheeraj-gangwar on 2018-01-21 <======
 */

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {
    private Button btn_signIn;
    private EditText userNameET;
    private EditText userPasswordET;
    private RelativeLayout parentLayout;
    private CallbackManager callbackManager;
    private LoginManager facebookLoginManager;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private final String TAG = getClass().getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private SpotsDialog spotsDialog;
    DBHelper dbHelper;
    private TextView forgot_password;
    private String modelName;
    private String brandName;
    private String osName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);

        String content = "Dear user we access the background location in the app to provide you better service of FMC, we take the background location for below functionalities."+"\n"+"1. Login Access."+"\n"+"2. While generating complaint."+"\n"+ "3. What's near me." +"\n"+ "4. e-Hospitals." +"\n"+"5. Air Quality Index." +"\n"+"6. Smart meter";

        prominantDiscloser(content);
        callbackManager = CallbackManager.Factory.create();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        userNameET = findViewById(R.id.user_name_edit_text);
        userPasswordET = findViewById(R.id.password_edit_text);
        forgot_password = findViewById(R.id.forgot_password);
        btn_signIn = findViewById(R.id.sign_in_button);
        parentLayout = findViewById(R.id.parent_layout);
        modelName = Build.MODEL;
        brandName = Build.BRAND;
        osName = Build.VERSION.RELEASE;
//        try {
//            Field[] fields = Build.VERSION_CODES.class.getFields();
//            osName = fields[Build.VERSION.SDK_INT + 1].getName();
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
        spotsDialog = new SpotsDialog(LoginActivity.this,
                getResources().getString(R.string.please_wait_dialog_text),
                R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        facebookLoginManager = LoginManager.getInstance();
        facebookLoginManager.logOut();
        dbHelper = new DBHelper(LoginActivity.this);
        insertGenderData();
        getRelease();
        registerButtonClick();

        facebookLoginButtonClick();
        googleLoginButtonClick();
        signInButtonClick();


        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ForgotpasswordActivity.class);
                startActivity(intent);
            }
        });

    }

    public void prominantDiscloser(String cc){
        ImageView cross;
        TextView content;
        final Dialog disclosure = new Dialog(LoginActivity.this);
        disclosure.setContentView(R.layout.prominant_discloser);
        cross =  disclosure.findViewById(R.id.wertyu);
        content =  disclosure.findViewById(R.id.content);
        content.setText(cc);
        disclosure.setCancelable(true);
        disclosure.setCanceledOnTouchOutside(false);
        disclosure.show();
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disclosure.dismiss();
                checkAndroidVersion();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (opr.isDone()) {
//            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
//            // and the GoogleSignInResult will be available instantly.
//            Log.d(TAG, "Got cached sign-in");
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        } else {
//            // If the user has not previously signed in on this device or the sign-in has expired,
//            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
//            // single sign-on will occur in this branch.
//            Utils.showSpotsDialog(LoginActivity.this,
//                    getResources().getString(R.string.please_wait_dialog_text), false);
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
//                    spotsDialog.dismiss();
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
    }

    private void insertGenderData() {
        dbHelper.insertGenderData(0, "Select Gender");
        dbHelper.insertGenderData(1, "Female");
        dbHelper.insertGenderData(2, "Male");
        dbHelper.insertGenderData(3, "Trans-gender");
        //dbHelper.insertGenderData(4, "Rather not to specify !");
    }

    /* ====================== onClickListener for Sign in Button ====================== */
    private void signInButtonClick() {
//        userNameET.setText("Dheeraj@gmail.com");
//        userPasswordET.setText("Dheeraj");
        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUserName()) {
                    Utils.showSnackBar(LoginActivity.this, parentLayout,
                            "Please enter Username", false);
                } else if (!validatePassword()) {
                    Utils.showSnackBar(LoginActivity.this, parentLayout,
                            "Please enter Password", false);
                } else if (!ConnectivityReceiver.isConnected()) {
                    Utils.showSnackBar(LoginActivity.this, parentLayout,
                            Constants.MESSAGE_INTERNET_NOT_CONNECTED, false);
                } else {
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                    spotsDialog.show();
                    String token = Utils.accessFirebaseToken(LoginActivity.this);
                    String refreshedToken = String.valueOf(FirebaseMessaging.getInstance().getToken());
                    loginUserAccount(userNameET.getText().toString().trim(),
                            userPasswordET.getText().toString(),
                            Utils.uniqueDeviceId(LoginActivity.this),
                            Utils.getAppVersionName(LoginActivity.this),
                            Utils.accessFirebaseToken(LoginActivity.this),

                            Constants.USER_EMAIL_SIGN_IN,
                            "", "", 0, "", 0,
                            0, "");
                }
            }
        });
    }
    /* ================================================================================================= */

    /* ====================== onClickListener for Register Button ====================== */
    private void registerButtonClick() {
        // Register Button
        findViewById(R.id.register_account_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                overridePendingTransition(R.anim.animation_enter_from_right,
                        R.anim.animation_leave_out_to_left);
            }
        });
    }
    /* ================================================================================================= */

    /* ====================== onClickListener for Facebook Login Button ====================== */
    private void facebookLoginButtonClick() {
        // Register Button
        findViewById(R.id.facebook_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spotsDialog.show();
                facebookLoginManager.logInWithReadPermissions(LoginActivity.this,
                        Arrays.asList("public_profile", "email"));
                performFacebookSignIn();
            }
        });
    }
    /* ================================================================================================= */

    /* ====================== onClickListener for Google Login Button ====================== */
    private void googleLoginButtonClick() {
        // Register Button
        findViewById(R.id.google_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    performGoogleSignIn();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /* ================================================================================================= */

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        spotsDialog.show();
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            if (acct != null) {
                loginUserAccount(acct.getEmail(), "",
                        Utils.uniqueDeviceId(LoginActivity.this),
                        Utils.getAppVersionName(LoginActivity.this),
                        Utils.accessFirebaseToken(LoginActivity.this),
                        Constants.USER_SOCIAL_SIGN_IN,
                        acct.getDisplayName(), "", 0, "", 0,
                        Constants.USER_SOCIAL_MEDIA_TYPE_GOOGLE, acct.getId());
            } else {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                spotsDialog.dismiss();
                Utils.showSnackBar(LoginActivity.this, parentLayout,
                        Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
            }
        } else {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            spotsDialog.dismiss();
            Utils.showSnackBar(LoginActivity.this, parentLayout,
                    Constants.MESSAGE_TRY_AGAIN_LATER, false);
        }
    }


    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            checkAndRequestPermissions();
        }
    }

    //Permission check for marshmallow
    private boolean checkAndRequestPermissions() {
        int storagePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int storagePermission2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int ACCESS_NETWORK_STATE = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (storagePermission2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (ACCESS_NETWORK_STATE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_CAMERA);
            return false;
        }
        return true;
    }

    /* ====================== Method for Facebook login ====================== */
    private void performFacebookSignIn() {
        facebookLoginManager.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        String accessToken = loginResult.getAccessToken().getToken();
                        Log.i("accessToken", accessToken);

                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        if (response.getError() != null) {
                                            spotsDialog.dismiss();
                                            facebookLoginManager.logOut();
                                            // handle error
                                            Utils.showSnackBar(LoginActivity.this, parentLayout,
                                                    Constants.MESSAGE_TRY_AGAIN_LATER, false);
                                        } else {
                                            Log.v(TAG, response.toString());
                                            // Get facebook data from login
//                                            Bundle bFacebookData = getFacebookData(object);
                                            loginUserAccount(response.getJSONObject().optString("email"), "",
                                                    Utils.uniqueDeviceId(LoginActivity.this),
                                                    Utils.getAppVersionName(LoginActivity.this),
                                                    Utils.accessFirebaseToken(LoginActivity.this),
                                                    Constants.USER_SOCIAL_SIGN_IN,
                                                    response.getJSONObject().optString("first_name"),
                                                    response.getJSONObject().optString("last_name"),
                                                    (String.valueOf(response.getJSONObject().optString("gender"))
                                                            .equalsIgnoreCase("male") ? 2 : 1), "",
                                                    0,
                                                    Constants.USER_SOCIAL_MEDIA_TYPE_FACEBOOK, response.getJSONObject().optString("id"));
                                        }
                                    }
                                });

                        Bundle parameters = new Bundle();
                        // Parameters that we ask to facebook
                        parameters.putString("fields", "id, first_name, last_name, email, picture, gender, birthday, location");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        spotsDialog.dismiss();
                        facebookLoginManager.logOut();
                        Log.v(TAG, "Cancelling Facebook login....");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        spotsDialog.dismiss();
                        facebookLoginManager.logOut();
                        Log.e(TAG, Utils.failureMessage(error));
                    }
                });
    }
    /* ================================================================================================= */

    private Bundle getFacebookData(JSONObject object) {
        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        } catch (JSONException e) {
            Log.d(TAG, "Error parsing JSON");
        }
        return null;
    }

    private void performGoogleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, Constants.GOOGLE_SIGN_IN);
    }

    private void performGoogleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }
    /* ====================== Check if username field is empty or not ====================== */
    private boolean validateUserName() {
        return !userNameET.getText().toString().isEmpty();
    }
    /* ================================================================================================= */


    /* ====================== Check if password field is empty or not ====================== */
    private boolean validatePassword() {
        return !userPasswordET.getText().toString().isEmpty();
    }
    /* ================================================================================================= */

    /* 1 :- Gmail Login
       2 :- Facebook Login
     */

    /* ====================== Request method for user login ====================== */
    private void loginUserAccount(String email, String password, String deviceId,
                                  String appVersionName, String fcmToken,
                                  int loginType, String firstName,
                                  String lastName, int genderId,
                                  String mobileNum, int imageSource,
                                  int socialMediaTypeId, String socialAccountId) {
        if(email.isEmpty()){
            email = socialAccountId + "@facebook.com";
        }
        ApiInterface mApiInterface = Utils.getInterfaceService();
//        String fcmToken1 = FirebaseInstanceId.getInstance().getToken();

        String fcmToken1 = String.valueOf(FirebaseMessaging.getInstance().getToken());

        Log.d("FirebaseToken",fcmToken1);
        Call<JsonObject> mService = mApiInterface.loginUser(email, password,
                deviceId, appVersionName,
                fcmToken1, loginType, firstName,
                lastName, genderId,
                mobileNum, imageSource,
                socialMediaTypeId, socialAccountId, "Android",
                brandName, modelName, osName);

        mService.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (response.body() != null) {
                        JsonObject mLoginObject = response.body();
                        if (mLoginObject.get("response").getAsBoolean()) {
                            String token = mLoginObject.get("token").getAsString().trim();
                            Log.v(TAG, Constants.USER_TOKEN + "===>" + token);
                            JsonObject userData = mLoginObject.get("user_data").getAsJsonObject();
                            SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Constants.USER_TOKEN, token);
                            editor.putString(Constants.USER_DATA, userData.toString());

                            JsonElement userLoginType = userData.get("media_type");
                            if (userData != null){
                                String surveyor_status = userData.getAsJsonObject().get("surveyor_status").isJsonNull() ? "" : userData.getAsJsonObject().get("surveyor_status").getAsString();
                                Constants.setPreferenceStringData(LoginActivity.this,"surveyor_status",surveyor_status);
                            }

                            /**
                             * Comment by dheeraj-gangwar on 2018-04-24
                             *
                             * This {@link Constants.USER_PROFILE_STATUS} is "MANAGED LOCALLY ONLY FOR FIRST TIME LOGIN",
                             * the value of this {@link Constants.USER_PROFILE_STATUS} will be update by 1 only after
                             * the user completely update their profile in {@link UserProfileActivity}.
                             */
                            editor.putInt(Constants.USER_PROFILE_STATUS, userData.get("profile_status").getAsInt());

                            /**
                             * Comment by dheeraj-gangwar on 2018-04-28
                             *
                             * This {@link Constants.USER_LOGIN_TYPE} is MANAGED LOCALLY AT LOGIN,
                             *
                             * if {@link userLoginType} is null then it means user logged in
                             * by entering username and password, i.e, {@link Constants.USER_LOGIN_TYPE}
                             * is equals {@link Constants.USER_EMAIL_SIGN_IN},
                             *
                             * if {@link userLoginType} is not null then it is a social media login, i.e, {@link Constants.USER_LOGIN_TYPE}
                             * is equals {@link Constants.USER_SOCIAL_SIGN_IN}.
                             */
                            //   if (userLoginType.isJsonNull()) {
                            if (userLoginType.toString().equalsIgnoreCase("0")) {
                                editor.putInt(Constants.USER_LOGIN_TYPE, Constants.USER_EMAIL_SIGN_IN);
                            } else {
                                editor.putInt(Constants.USER_LOGIN_TYPE, Constants.USER_SOCIAL_SIGN_IN);
                            }

                            editor.apply();
                            airQualityIndexFetching(Constants.AQI_API);

                            Constants.setPreferenceStringData(LoginActivity.this,"locale","en");

                            Intent intent = new Intent(getApplicationContext(), SlidingDrawerActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Utils.enterAnimation(LoginActivity.this);
                            spotsDialog.dismiss();
                            finish();
                        } else {
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                            facebookLoginManager.logOut();
                            spotsDialog.dismiss();
                            Utils.showSnackBar(LoginActivity.this, parentLayout,
                                    mLoginObject.get("message").getAsString().trim(), false);
                        }
                    } else {
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                        facebookLoginManager.logOut();
                        spotsDialog.dismiss();
                        Utils.showSnackBar(LoginActivity.this, parentLayout,
                                Constants.MESSAGE_TRY_AGAIN_LATER, false);
                    }
                } catch (Exception e) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    facebookLoginManager.logOut();
                    e.printStackTrace();
                    spotsDialog.dismiss();
                    Utils.showSnackBar(LoginActivity.this, parentLayout, Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                facebookLoginManager.logOut();
                call.cancel();
                spotsDialog.dismiss();
                Utils.showSnackBar(LoginActivity.this, parentLayout, Utils.failureMessage(t), false);
            }
        });
    }
    /* ================================================================================================= */


    private void onGoogleSignInSuccess() {
        startActivity(new Intent(LoginActivity.this, SlidingDrawerActivity.class));
        overridePendingTransition(R.anim.animation_enter_from_right,
                R.anim.animation_leave_out_to_left);
        spotsDialog.dismiss();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == Constants.GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void airQualityIndexFetching(String url) {
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);

        //Initialize a new JsonObjectRequest instance:-
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Fetching Result Array:-
                        try {
                            JSONArray result = response.getJSONArray("results");
                            if (result.length() > 0) {
                                for (int i = 0; i < result.length(); i++) {
                                    JSONObject airQualityObject = result.getJSONObject(i);
                                    String city = airQualityObject.getString("city");
                                    String country = airQualityObject.getString("country");
                                    String locations = airQualityObject.getString("locations");
                                    String count = airQualityObject.getString("count");

                                    //Putting Data into Database:-
                                    dbHelper.saveAQIData(city, country, locations, count);

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void getRelease() {
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(
                    "com.citizen.ndmc",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", "KeyHash:" + Base64.encodeToString(md.digest(),
                        Base64.DEFAULT));
               /* Toast.makeText(this.getApplicationContext(), Base64.encodeToString(md.digest(),
                        Base64.DEFAULT), Toast.LENGTH_LONG).show();*/
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}