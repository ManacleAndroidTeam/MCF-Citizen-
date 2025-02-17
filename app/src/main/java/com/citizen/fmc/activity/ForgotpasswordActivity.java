package com.citizen.fmc.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.citizen.fmc.R;
import com.citizen.fmc.model.CheckAccountResponse.CheckAccountResponse;
import com.citizen.fmc.utils.ApiInterface;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotpasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText user_name;
    private Button submit_btn;
    private String username;
    private String TAG = getClass().getSimpleName();
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        permissionCheck();
        user_name = findViewById(R.id.user_name_et);
        submit_btn =(Button) findViewById(R.id.submit_btn);

        dialog = new ProgressDialog(ForgotpasswordActivity.this, R.style.DialogSlideAnim);
        dialog.setMessage(getString(R.string.loading_dialog_msg));
        dialog.setCancelable(false);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateUserName()) {
                    Utils.customToast(ForgotpasswordActivity.this,
                            "Please enter Username", 0);
                }else {
                    username = user_name.getText().toString().trim();
                    forgotpassword(username);
                }

            }
        });
    }

    private void permissionCheck() {
        TedPermission.create()
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                    }

                    //New code
                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        finish();
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    }

                    //Previous code
                    /*@Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                        finish();
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    }*/
                })
                .setGotoSettingButtonText("SETTINGS")
                .setDeniedMessage("If you reject permission,you can not use this service" +
                        "\n\nPlease turn on permissions at [Settings] > [Permission]")
                .setPermissions(Manifest.permission.INTERNET)
                .check();
    }

    private boolean validateUserName() {
        return !user_name.getText().toString().isEmpty();
    }


    private void forgotpassword(String username) {
        dialog.show();
        ApiInterface mApiService = Utils.getInterfaceService();
        Call<CheckAccountResponse> mService = mApiService.checkAccount(2,2,
                username,Constants.HEADER_ACCEPT);


        mService.enqueue(new Callback<CheckAccountResponse>() {
            @Override
            public void onResponse(Call<CheckAccountResponse> call, Response<CheckAccountResponse> response) {
                CheckAccountResponse mcheckAccount = response.body();
                dialog.dismiss();
                if (mcheckAccount != null) {
                    if (mcheckAccount.getResponse()) {
                        Utils.customToast(ForgotpasswordActivity.this, "Account verified", 1);
                        Intent intent = new Intent(getApplicationContext(), OtpSendViaEmailorSmsActivity.class);
                        intent.putExtra(Constants.USER_ID_FORGOTPASSWORD,mcheckAccount.getUserId());
                        intent.putExtra(Constants.REGISTER_EMAIL,mcheckAccount.getRegEmailId());
                        intent.putExtra(Constants.REGISTER_MOBILENO,mcheckAccount.getRegMobileNo());
                        startActivity(intent);
                        overridePendingTransition(R.anim.animation_enter_from_right, R.anim.animation_leave_out_to_left);
                    }else {
                        Utils.customToast(ForgotpasswordActivity.this, mcheckAccount.getMessage(), 0);

                    }
                } else {
                  Intent intent = new Intent(getApplicationContext(),UserNotExist.class);
                  startActivity(intent);

                    overridePendingTransition(R.anim.animation_enter_from_right,
                            R.anim.animation_leave_out_to_left);

                    Utils.customToast(ForgotpasswordActivity.this, Constants.MESSAGE_TRY_AGAIN_LATER, 0);
                }
            }

            @Override
            public void onFailure(Call<CheckAccountResponse> call, Throwable t) {
                call.cancel();
                dialog.dismiss();
                t.printStackTrace();
                Utils.customToast(ForgotpasswordActivity.this,Utils.failureMessage(t), 0);
            }
        });
    }


    @Override
    public void onClick(View view) {

    }
}
