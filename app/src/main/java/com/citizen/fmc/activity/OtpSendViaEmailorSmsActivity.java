package com.citizen.fmc.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.model.GenerateOtpResponse.GenerateOtpResponse;
import com.citizen.fmc.utils.ApiInterface;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpSendViaEmailorSmsActivity extends AppCompatActivity implements View.OnClickListener {

    private RadioButton viasms;
    private RadioButton viaemail;
    private TextView mob_sms;
    private EditText uemailsms;
    private int flag = 1;
    private String emailorsms;
    private Button submitotp;
    private int user_id;
    private ProgressDialog dialog;
    private String reg_email ,reg_mob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_send);

        viasms = findViewById(R.id.viasms);
        viaemail = findViewById(R.id.viaemail);
        mob_sms = findViewById(R.id.mob_sms_txt);
        uemailsms = findViewById(R.id.user_email_or_sms);
        submitotp = findViewById(R.id.submit_otp);

        dialog = new ProgressDialog(OtpSendViaEmailorSmsActivity.this, R.style.DialogSlideAnim);
        dialog.setMessage(getString(R.string.loading_dialog_msg));
        dialog.setCancelable(false);

        if(getIntent()!=null){
            user_id = getIntent().getIntExtra(Constants.USER_ID_FORGOTPASSWORD,0);
            reg_email = getIntent().getStringExtra(Constants.REGISTER_EMAIL);
            reg_mob = getIntent().getStringExtra(Constants.REGISTER_MOBILENO);
        }

        viasms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 1;
                mob_sms.setText("Mobile Number");
                uemailsms.setHint("Please enter your mobile number");

            }
        });


        viaemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = 2;

                mob_sms.setText("Email Id");
                uemailsms.setHint("Please enter your email");

            }
        });

        submitotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag ==2){
                    emailorsms = uemailsms.getText().toString().trim();
                    if(!isvalidEmail()){
                        Constants.customToast(OtpSendViaEmailorSmsActivity.this,
                                "Please enter valid email id", 0);
                    }else if(reg_email.equals(emailorsms)){
                        generateOtp(user_id,emailorsms,"");
                    }else
                        Constants.customToast(OtpSendViaEmailorSmsActivity.this,
                                "This email id does register with us !", 0);
                }else {
                    emailorsms = uemailsms.getText().toString().trim();
                    if(validCellPhone(emailorsms) && !(emailorsms.length() >11)){
                        if(reg_mob.equals(emailorsms))
                            generateOtp(user_id,"",emailorsms);
                        else
                            Constants.customToast(OtpSendViaEmailorSmsActivity.this,
                                    "This mobile no does not register with us !", 0);
                    }
                    else{
                        Constants.customToast(OtpSendViaEmailorSmsActivity.this,
                                "Please enter valid mobile number", 0);
                    }

                }
            }
        });

    }

    private boolean isvalidEmail(){

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return emailorsms.matches(emailPattern);
    }

    public boolean validCellPhone(String number)
    {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }



    private void generateOtp(final int user_id, final String email_id, final String mobno) {
        dialog.show();
        ApiInterface mApiService = Utils.getInterfaceService();
        Call<GenerateOtpResponse> mService = mApiService.generateOtp(2,2,
                email_id,mobno,user_id,Constants.HEADER_ACCEPT);

        mService.enqueue(new Callback<GenerateOtpResponse>() {
            @Override
            public void onResponse(Call<GenerateOtpResponse> call, Response<GenerateOtpResponse> response) {
                GenerateOtpResponse generateOtpResponse = response.body();
                dialog.dismiss();
                if (generateOtpResponse != null) {
                    if (generateOtpResponse.getResponse()) {
                        Utils.customToast(OtpSendViaEmailorSmsActivity.this, generateOtpResponse.getMessage(), 1);
                        Intent intent = new Intent(getApplicationContext(), OtpActivity.class);
                        intent.putExtra(Constants.GENERATE_OTP,generateOtpResponse.getOTP());
                        intent.putExtra(Constants.OTP_EMAIL,email_id);
                        intent.putExtra(Constants.OTP_MOBILENO,mobno);
                        intent.putExtra(Constants.USER_ID_FORGOTPASSWORD,user_id);
                        startActivity(intent);
                        Utils.customToast(OtpSendViaEmailorSmsActivity.this, generateOtpResponse.getOTP(), 1);
                        overridePendingTransition(R.anim.animation_enter_from_right, R.anim.animation_leave_out_to_left);
                        finish();
                    }else {
                        Utils.customToast(OtpSendViaEmailorSmsActivity.this, generateOtpResponse.getMessage(), 0);

                    }
                } else {
                  /*  Intent intent = new Intent(getApplicationContext(),UserNotExist.class);
                    startActivity(intent);

                    overridePendingTransition(R.anim.animation_enter_from_right,
                            R.anim.animation_leave_out_to_left);*/

                    Utils.customToast(OtpSendViaEmailorSmsActivity.this, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                }
            }

            @Override
            public void onFailure(Call<GenerateOtpResponse> call, Throwable t) {
                call.cancel();
                dialog.dismiss();
                t.printStackTrace();
                Utils.customToast(OtpSendViaEmailorSmsActivity.this,Utils.failureMessage(t), 0);
            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
