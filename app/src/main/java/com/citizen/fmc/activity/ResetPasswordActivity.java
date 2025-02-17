package com.citizen.fmc.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.citizen.fmc.R;
import com.citizen.fmc.model.ForgotPasswordResponse.ForgotPasswordResponse;
import com.citizen.fmc.utils.ApiInterface;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private String user_id;
    private EditText new_passd_et;
    private Button reset_btn;
    private EditText con_pass;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        new_passd_et = findViewById(R.id.new_passd_et);
        con_pass = findViewById(R.id.confirm_et);
        reset_btn = findViewById(R.id.reset_pasd_btn);

        dialog = new ProgressDialog(ResetPasswordActivity.this, R.style.DialogSlideAnim);
        dialog.setMessage(getString(R.string.loading_dialog_msg));
        dialog.setCancelable(false);

        if (getIntent() != null) {
            user_id = getIntent().getStringExtra(Constants.USER_ID_FORGOTPASSWORD);
        }


        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatePassword();
            }
        });

    }

    private void validatePassword() {

        String newpass = new_passd_et.getText().toString().trim();
        String conpass = con_pass.getText().toString().trim();
        if (TextUtils.isEmpty(newpass)) {
            Utils.customToast(ResetPasswordActivity.this,
                    "Please enter new password", 0);
        } else if (TextUtils.isEmpty(conpass)) {
            Utils.customToast(ResetPasswordActivity.this,
                    "Please enter confirm password", 0);
        } else {
            if (newpass.equals(conpass)) {
                forgotPassword(user_id, conpass);
            } else {
                Constants.customToast(ResetPasswordActivity.this,
                        "Passwords do not match", 0);
            }
        }
    }


    private void forgotPassword(final String user_id, final String password) {
        dialog.show();
        ApiInterface mApiService = Utils.getInterfaceService();
        Call<ForgotPasswordResponse> mService = mApiService.resetPassword(2, password,
                user_id, Constants.HEADER_ACCEPT);
        mService.enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(Call<ForgotPasswordResponse> call, Response<ForgotPasswordResponse> response) {
                ForgotPasswordResponse forgotPasswordResponse = response.body();
                dialog.dismiss();
                if (forgotPasswordResponse != null) {
                    if (forgotPasswordResponse.getResponse()) {
                        Utils.customToast(ResetPasswordActivity.this, forgotPasswordResponse.getMessage(), 1);
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.animation_enter_from_right, R.anim.animation_leave_out_to_left);
                        finish();
                    } else {
                        dialog.dismiss();
                        Utils.customToast(ResetPasswordActivity.this, forgotPasswordResponse.getMessage(), 0);

                    }
                } else {
                    dialog.dismiss();
                    Utils.customToast(ResetPasswordActivity.this, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                }
            }

            @Override
            public void onFailure(Call<ForgotPasswordResponse> call, Throwable t) {
                call.cancel();
                dialog.dismiss();
                t.printStackTrace();
                Utils.customToast(ResetPasswordActivity.this, Utils.failureMessage(t), 0);
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
