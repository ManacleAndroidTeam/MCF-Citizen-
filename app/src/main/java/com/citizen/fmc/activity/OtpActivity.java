package com.citizen.fmc.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {
    private EditText et1,et2,et3,et4,et5,et6;
    private Button  submit_otp_btn;
    private String genotp;
    private String et1s,et2s,et3s,et4s,et5s,et6s;
    private TextView resend_otp;
    private String otp_email,otp_mob_no;
    private int user_id;
    private ProgressDialog dialog;
    MySMSBroadCastReceiver smsBroadCastReceiver;
    private String msg_notify = "VK-NDMCIT";
    private String msg_notify1 = "VM-NDMCIT";

    private CountDownTimer countDownTimer;
    // private RelativeLayout cart_l;
    private final static int PERMISSION_REQUEST_CODE = 12;
    private TextView otp_timer;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private String total_et;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        et4 = findViewById(R.id.et4);
        et5 = findViewById(R.id.et5);
        et6 = findViewById(R.id.et6);
        otp_timer = findViewById(R.id.otp_timer);
        submit_otp_btn = findViewById(R.id.compare_otp);
        resend_otp = findViewById(R.id.resend_otp);

        //et6 TextWatcher:-


        dialog = new ProgressDialog(OtpActivity.this, R.style.DialogSlideAnim);
        dialog.setMessage(getString(R.string.loading_dialog_msg));
        dialog.setCancelable(false);

        if(getIntent()!=null){
            genotp = getIntent().getStringExtra(Constants.GENERATE_OTP);
            otp_email = getIntent().getStringExtra(Constants.OTP_EMAIL);
            otp_mob_no = getIntent().getStringExtra(Constants.OTP_MOBILENO);
            user_id = getIntent().getIntExtra(Constants.USER_ID_FORGOTPASSWORD,0);
        }

        TedPermission.create()
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        finish();
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    }

                  /*  @Override
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
        //startCountDownTimer();
        setOtp();
        clearOTP();

       // setTOEditTExt(genotp);

        
        
        submit_otp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(et1.getText().toString()))
                {
                    Utils.customToast(OtpActivity.this,
                            "Please fill otp1", 0); 
                }else  if(TextUtils.isEmpty(et2.getText().toString()))
                {
                    Utils.customToast(OtpActivity.this,
                            "Please fill otp2", 0);
                }else  if(TextUtils.isEmpty(et3.getText().toString()))
                {
                    Utils.customToast(OtpActivity.this,
                            "Please fill otp3", 0);
                }else  if(TextUtils.isEmpty(et4.getText().toString()))
                {
                    Utils.customToast(OtpActivity.this,
                            "Please fill otp4", 0);
                }else  if(TextUtils.isEmpty(et5.getText().toString()))
                {
                    Utils.customToast(OtpActivity.this,
                            "Please fill otp5", 0);
                }else  if(TextUtils.isEmpty(et6.getText().toString()))
                {
                    Utils.customToast(OtpActivity.this,
                            "Please fill otp6", 0);
                }else {

                    et1s =et1.getText().toString().trim();
                    et2s =et2.getText().toString().trim();
                    et3s =et3.getText().toString().trim();
                    et4s =et4.getText().toString().trim();
                    et5s =et5.getText().toString().trim();
                    et6s =et6.getText().toString().trim();
                    total_et = et1s+et2s+et3s+et4s+et5s+et6s;
                    generateOtp(String.valueOf(user_id),otp_email,otp_mob_no , total_et);
                }
                
                
            }
        });


        resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(otp_email)){
                    generateOtp(String.valueOf(user_id),otp_email,"" , total_et);
                }else {
                    generateOtp(String.valueOf(user_id),"",otp_mob_no , total_et);
                }
            }
        });


    }

    private void clearOTP() {
            //et6 AddTextWatcher:-
            et6.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String text = s.toString();
                    if(text.isEmpty()){
                        et5.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            //et5 AddTextWatcher:-
            et5.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String text = s.toString();
                    if(text.isEmpty()){
                        et4.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            //et4 AddTextWatcher:-
            et4.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String text = s.toString();
                    if(text.isEmpty()){
                        et3.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            //et3 AddTextWatcher:-
            et3.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String text = s.toString();
                    if(text.isEmpty()){
                        et2.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            //et2 AddTextWatcher:-
            et2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String text = s.toString();
                    if(text.isEmpty()){
                        et1.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }


    private void setTOEditTExt(String genotp) {

        String otp[] = genotp.split("");
        et1.setText(otp[1]);
        et2.setText(otp[2]);
        et3.setText(otp[3]);
        et4.setText(otp[4]);
        et5.setText(otp[5]);
        et6.setText(otp[6]);
    }


    private void generateOtp(final String user_id, final String email_id, final String mobno , String generatedOTP) {
        dialog.show();
        Call<JsonObject> call =  Utils.getInterfaceService().verifiedOTP("2",user_id,total_et,mobno,
                email_id,Constants.HEADER_ACCEPT);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                int code = response.code();
                JsonObject jsonObject = response.body();
                message = jsonObject.get("message").getAsString();
                if(code == 200){
                    if(jsonObject.get("response").getAsBoolean()){
                        dialog.dismiss();
                        Constants.customToast(OtpActivity.this , message, 1);
                        Intent intent = new Intent(getApplicationContext(),ResetPasswordActivity.class);
                        intent.putExtra(Constants.USER_ID_FORGOTPASSWORD,user_id);
                        startActivity(intent);
                        finish();
                    }else {
                        dialog.dismiss();
                        Constants.customToast(OtpActivity.this , message, 0);
                    }
                }else {
                    dialog.dismiss();
                    Constants.customToast(OtpActivity.this , message, 0);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
                dialog.dismiss();
                t.printStackTrace();
                Constants.customToast(OtpActivity.this,Constants.failureMessage(t), 0);
            }
        });
    }
    
    
    private void setOtp(){
        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String st = charSequence.toString();
                if(st.length()==1){
                    et2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
              
                
            }
        });

        et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String st = charSequence.toString();
                if(st.length()==1){
                    et3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

        et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String st = charSequence.toString();
                if(st.length()==1){
                    et4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });


        et4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String st = charSequence.toString();
                if(st.length()==1){
                    et5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

        et5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String st = charSequence.toString();
                if(st.length()==1){
                    et6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
        
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    public class MySMSBroadCastReceiver extends BroadcastReceiver {

        private String phoneNumberFilter;
        private String filter;



        @Override
        public void onReceive(Context context, Intent intent) {
            final Bundle bundle = intent.getExtras();
            try {
                if (bundle != null) {
                    final Object[] pdusObj = (Object[]) bundle.get("pdus");
                    for (int i = 0; i < pdusObj.length; i++) {
                        SmsMessage currentMessage = getIncomingMessage(pdusObj[i], bundle);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                       /* Log.e("phone number", "====>message_no" + phoneNumber);
                        Log.e("curr_msg", "====>message" + currentMessage.getDisplayMessageBody());*/
                        if (phoneNumberFilter != null && !phoneNumber.equals(phoneNumberFilter)) {
                            return;
                        }


                        if(!TextUtils.isEmpty(phoneNumber) && (phoneNumber.equalsIgnoreCase(msg_notify) || phoneNumber.equalsIgnoreCase(msg_notify1))){
                            String message = currentMessage.getDisplayMessageBody();
                            if(!TextUtils.isEmpty(message)){
                                // String substr=message.substring(message.length()-8,message.length()-2);
                                String substr=message.substring(4,message.length());
                                Log.e("phone otp", "====>message_otp" + substr);
                                //   et_new.setText(substr);
                                //   String[] otp = substr.split("");
                                //   String otp = Character.digit(substr.charAt(0), 10);
                                if(substr!=null && substr.length()==6){
                                    et1.setText(Character.digit(substr.charAt(0), 10)+"");
                                    et2.setText(Character.digit(substr.charAt(1), 10)+"");
                                    et3.setText(Character.digit(substr.charAt(2), 10)+"");
                                    et4.setText(Character.digit(substr.charAt(3), 10)+"");
                                    et5.setText(Character.digit(substr.charAt(4), 10)+"");
                                    et6.setText(Character.digit(substr.charAt(5), 10)+"");
                                    // resend_otp_.setVisibility(View.INVISIBLE);
                                    otp_timer.setVisibility(View.INVISIBLE);
                                    if(countDownTimer!=null)
                                        countDownTimer.cancel();
                                }
                            }

                        }




                    } // end for loop
                } // bundle is null

            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" + e);
            }
        }

        private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
            SmsMessage currentSMS;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String format = bundle.getString("format");
                currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
            } else {
                currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
            }
            return currentSMS;
        }


    }

    private void startCountDownTimer(){

        //  resend_otp_.setVisibility(View.INVISIBLE);
        otp_timer.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                otp_timer.setText("00:"+(millisUntilFinished / 1000));

                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                otp_timer.setText("00:00");
                //resend_otp_.setVisibility(View.VISIBLE);
                otp_timer.setVisibility(View.INVISIBLE);

            }

        }.start();
    }

    private void registerReceiver() {

        smsBroadCastReceiver  = new MySMSBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsBroadCastReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(smsBroadCastReceiver!=null)
            unregisterReceiver(smsBroadCastReceiver);

        if(countDownTimer!=null)
            countDownTimer.cancel();
    }

    public static boolean isStoragePermissionGranted(Context activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS)
                    == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_SMS)
                            == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    /**
     * need for Android 6 real time permissions
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                registerReceiver();
            }
        }
    }

}
