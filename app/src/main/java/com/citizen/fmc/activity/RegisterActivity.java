package com.citizen.fmc.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.citizen.fmc.R;
import com.citizen.fmc.database.DBHelper;
import com.citizen.fmc.model.ImageTextModel;
import com.citizen.fmc.utils.ApiInterface;
import com.citizen.fmc.utils.ConnectivityReceiver;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ======> Created by dheeraj-gangwar on 2017-12-21 <======
 */

public class RegisterActivity extends AppCompatActivity {
    RelativeLayout parentLayout;
    EditText firstNameET;
    EditText lastNameET;
    EditText userNameET;
    EditText passwordET;
    EditText confirmPasswordET;
    EditText et_water_consumer_number;
    EditText et_electricity_consumer_number;
    private Spinner genderSpinner;
    //    RadioGroup genderRadioGroup;
    int genderId;
    private String TAG = getClass().getSimpleName();
    private SpotsDialog spotsDialog;
    private String userName;
    private String mobile;
    private String email;
//    private String waterConsumerNumber;
//    private String electricityConsumerNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new);
        parentLayout = findViewById(R.id.parent_layout);
        firstNameET = findViewById(R.id.first_name_edit_text);
        lastNameET = findViewById(R.id.last_name_edit_text);
        userNameET = findViewById(R.id.user_name_edit_text);
        passwordET = findViewById(R.id.new_password_edit_text);
//        et_water_consumer_number = findViewById(R.id.et_water_consumer_number);
//        et_electricity_consumer_number = findViewById(R.id.et_electricity_consumer_number);
        confirmPasswordET = findViewById(R.id.confirm_new_password_edit_text);
        genderSpinner = findViewById(R.id.gender_spinner);

        spotsDialog = new SpotsDialog(RegisterActivity.this,
                getResources().getString(R.string.please_wait_dialog_text), R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);

        setGenderSpinner();
        registerButtonOnClick();
    }

    private void setGenderSpinner() {
        final ArrayList<ImageTextModel> genderList = new DBHelper(RegisterActivity.this).getAllGender();
        ArrayList<String> list = new ArrayList<>();
        for (int x = 0; x < genderList.size(); x++) {
            list.add(genderList.get(x).getTitle());
        }

        genderSpinner.setAdapter(new ArrayAdapter<>(RegisterActivity.this,
                android.R.layout.simple_spinner_dropdown_item,
                android.R.id.text1, list));

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
    }

    /* ====================== onClickListener for Register Button ====================== */
    private void registerButtonOnClick() {
        findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFieldEmpty(firstNameET.getText().toString().trim())) {
                    Utils.showSnackBar(RegisterActivity.this, parentLayout, "Please enter First Name !", false);
                } else if (isFieldEmpty(lastNameET.getText().toString().trim())) {
                    Utils.showSnackBar(RegisterActivity.this, parentLayout, "Please enter Last Name !", false);
                } else if (isFieldEmpty(userNameET.getText().toString().trim())) {
                    Utils.showSnackBar(RegisterActivity.this, parentLayout, "Please enter a Username !", false);
                } else if (isMobileNum(userNameET.getText().toString().trim()) && !isValidMobile(userNameET.getText().toString())) {
                    Utils.showSnackBar(RegisterActivity.this, parentLayout, "Please enter a valid Mobile No. !", false);
                } else if (!isMobileNum(userNameET.getText().toString()) && !Patterns.EMAIL_ADDRESS.matcher(userNameET.getText().toString()).matches()) {
                    Utils.showSnackBar(RegisterActivity.this, parentLayout, "Please enter a valid E-mail !", false);
                } else if (isFieldEmpty(passwordET.getText().toString())) {
                    Utils.showSnackBar(RegisterActivity.this, parentLayout, "Please enter Password !", false);
                } else if (!validatePasswordLength(passwordET.getText().toString())) {
                    Utils.showSnackBar(RegisterActivity.this, parentLayout, "Password must have 8-20 characters !", false);
                } else if (isFieldEmpty(confirmPasswordET.getText().toString())) {
                    Utils.showSnackBar(RegisterActivity.this, parentLayout, "Please Confirm Password !", false);
                } else if (!validateBothPasswords(passwordET.getText().toString(), confirmPasswordET.getText().toString())) {
                    Utils.showSnackBar(RegisterActivity.this, parentLayout, "Passwords not matched !", false);
                }
                else {
                    if (ConnectivityReceiver.isConnected()) {
                        spotsDialog.show();
                        if (isMobileNum(userNameET.getText().toString().trim()) && isValidMobile(userNameET.getText().toString().trim())) {
                            mobile = userNameET.getText().toString().trim();
                            email = "";
                            userName = mobile;
                        }else{
                            mobile = "";
                            email = userNameET.getText().toString().trim();
                            userName = email;
                        }
//                        waterConsumerNumber = et_water_consumer_number.getText().toString().trim();
//                        electricityConsumerNumber = et_electricity_consumer_number.getText().toString().trim();
//                        if(waterConsumerNumber.isEmpty()){
//                            waterConsumerNumber  = "";
//                        }
//                        if(electricityConsumerNumber.isEmpty()){
//                            electricityConsumerNumber = "";
//                        }
                        registerNewUserAccount(userName, firstNameET.getText().toString().trim(),
                                lastNameET.getText().toString().trim(),
                                email, mobile,
                                passwordET.getText().toString(),
                                genderId );
                    } else {
                        Utils.showSnackBar(RegisterActivity.this, parentLayout, Constants.MESSAGE_INTERNET_NOT_CONNECTED, false);
                    }
                }
            }
        });
    }
    /* ================================================================================================= */


    private boolean isMobileNum(String str) {
        try {
            long test = Long.parseLong(str.trim());
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

//    private void genderRadioGroupCheckChangedListener() {
//        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.cb_female) {
//                    genderId = 1;
//                } else {
//                    genderId = 2;
//                }
//            }
//        });
//    }


    /* ====================== check if the field is empty or not ====================== */
    private boolean isFieldEmpty(String str) {
        return str.isEmpty();
    }
    /* ================================================================================================= */


    /* ====================== check if mobile number have 10 characters or not ====================== */
    private boolean isValidMobile(String str) {
        return str.length() == 10;
    }
    /* ================================================================================================= */


    /* ====================== check if password and confirm password texts are same or not ====================== */
    private boolean validateBothPasswords(String oldStr, String newStr) {
        return oldStr.equals(newStr);
    }
    /* ================================================================================================= */


    /* ====================== check if password have 8-20 characters ====================== */
    private boolean validatePasswordLength(String str) {
        return str.length() > 7 && str.length() < 21;
    }
    /* ================================================================================================= */


    /* ====================== check if a gender is selected or not ====================== */
//    private boolean validateGender() {
//        return genderRadioGroup.getCheckedRadioButtonId() != -1;
//    }
    /* ================================================================================================= */


    /* ====================== request method for registering or creating a new account ====================== */
    private void registerNewUserAccount(String userName, String firstName, String lastName,
                                        String email, String mobileNum,
                                        String password, int genderId ) {
        String fcmToken = String.valueOf(FirebaseMessaging.getInstance().getToken());
        if(fcmToken == null){
            fcmToken = "";
        }
        Log.e(TAG, "FCM_TOKEN: " + fcmToken);
        ApiInterface mApiService = Utils.getInterfaceService();
        Call<JsonObject> mService = mApiService.registerUser(Utils.uniqueDeviceId(RegisterActivity.this),
                Utils.getAppVersionName(RegisterActivity.this),
                fcmToken, userName, password,
                firstName, lastName, email, mobileNum, genderId );
        mService.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject mRegisterObject = response.body();
                String message = mRegisterObject.get("message").getAsString();
                if (response.code() ==  200) {
                    if (mRegisterObject.get("response").getAsBoolean()) {
                        Utils.customToast(RegisterActivity.this, message, 1);
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        spotsDialog.dismiss();
                        startActivity(intent);
                        Utils.exitAnimation(RegisterActivity.this);
                        finish();
                    }
                    else {
                        spotsDialog.dismiss();
                        Utils.showSnackBar(RegisterActivity.this, parentLayout, message, false);
                    }
                } else {
                    spotsDialog.dismiss();
                    Utils.showSnackBar(RegisterActivity.this, parentLayout, message, false);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
                spotsDialog.dismiss();
                t.printStackTrace();
                Utils.showSnackBar(RegisterActivity.this, parentLayout, Utils.failureMessage(t), false);
            }
        });
    }
    /* ================================================================================================= */

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Utils.exitAnimation(RegisterActivity.this);
    }
}
