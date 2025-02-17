package com.citizen.fmc.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.citizen.fmc.R;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import dmax.dialog.SpotsDialog;

public class WastageFragment extends Fragment {

    private View mView;
    private final String TAG = getClass().getSimpleName();
    private Activity activity;
    private AppBarLayout appBarLayout;
    private SpotsDialog spotsDialog;
    private String uniqueId;
    private int userId;
    private Button mQrSanbtn;
    private IntentIntegrator qrScan;
    private EditText qr_code_data_text_view;
    private String PAGE_TYPE = "";
    private String statusTitle;
    private Button segregate_button;
    private Button non_segragate_button;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (this.getArguments() != null) {
            /**
              */
            PAGE_TYPE = this.getArguments().getString(Constants.KEY_COMPLAINT_PAGE_TYPE);
            statusTitle = this.getArguments().getString(Constants.KEY_STATUS_TITLE);


        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            mView = inflater.inflate(R.layout.fragment_wastage, container, false);
            activity = getActivity();
            appBarLayout = mView.findViewById(R.id.app_bar_layout);
        mQrSanbtn=mView.findViewById(R.id.scan_qr_button);
            userId = Utils.getUserDetails(activity).getCivilianId();
             uniqueId = Utils.generateUniqueId(userId);
        qr_code_data_text_view=mView.findViewById(R.id.qr_code_data_text_view);
        non_segragate_button= mView.findViewById(R.id.non_segragate_button);
        segregate_button= mView.findViewById(R.id.segregate_button);
            spotsDialog = new SpotsDialog(activity, getResources().getString(R.string.please_wait_dialog_text), R.style.CustomSpotsDialogStyle);
            spotsDialog.setCancelable(false);
        qrScan = new IntentIntegrator(getActivity());
        Utils.setToolbarTitle(activity, "Swachta");

        mQrSanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.initiateScan();

            }
        });
//segregate_button.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View v) {
//        if (ConnectivityReceiver.isConnected()) {
//            spotsDialog.show();
//            if (qr_code_data_text_view.getText().toString().isEmpty()) {
//                Constants.customToast(getActivity(),"Please scan Qr Code",1);
//                spotsDialog.dismiss();
//
//            } else {
//                ApiInterface mApiInterface = Utils.getInterfaceService();
//
//                Call<JsonObject> mService = mApiInterface.submitWastage(
//                         RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userId)),
//                        RequestBody.create(MediaType.parse("multipart/form-data"), qr_code_data_text_view.getText().toString().trim()),
//                         RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Constants.APP_TYPE)),
//                         RequestBody.create(MediaType.parse("multipart/form-data"), "1")
//
//                        );
//
//                mService.enqueue(new Callback<JsonObject>() {
//                    @Override
//                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                        try {
//                            if (response.body() != null) {
//                                JsonObject jsonObject = response.body();
//                                if (jsonObject.get("response").getAsBoolean()) {
//                                      spotsDialog.dismiss();
//                                      Constants.customToast(getActivity(),"Data submitted successfully",1);
//                                    getFragmentManager().popBackStackImmediate();
//
//                                } else {
//                                    spotsDialog.dismiss();
//                                    String message = jsonObject.get("message").getAsString().trim();
//                                    Constants.customToast(getActivity(),message,0);
//                                }
//                            } else {
//                                spotsDialog.dismiss();
//                                Constants.customToast(getActivity(),Constants.MESSAGE_SOMETHING_WENT_WRONG,1);
//
//                             }
//                        } catch (Exception e) {
//                            spotsDialog.dismiss();
//                            e.printStackTrace();
//                            Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<JsonObject> call, Throwable t) {
//                        spotsDialog.dismiss();
//                        call.cancel();
//                        t.printStackTrace();
//                        Utils.customToast(activity, Utils.failureMessage(t), 0);
//
//                     }
//                });
//
//            }
//        }else{
//
//                Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
//                        Constants.MESSAGE_CHECK_INTERNET, false);
//
//        }
//
//    }
//});

//        non_segragate_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (ConnectivityReceiver.isConnected()) {
//                    spotsDialog.show();
//                    if (qr_code_data_text_view.getText().toString().isEmpty()) {
//                        Constants.customToast(getActivity(),"Please scan Qr Code",1);
//                        spotsDialog.dismiss();
//                    } else {
//                        ApiInterface mApiInterface = Utils.getInterfaceService();
//
//                        Call<JsonObject> mService = mApiInterface.submitWastage(
//                                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userId)),
//                                RequestBody.create(MediaType.parse("multipart/form-data"), qr_code_data_text_view.getText().toString().trim()),
//                                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Constants.APP_TYPE)),
//                                RequestBody.create(MediaType.parse("multipart/form-data"), "2")
//
//                        );
//
//                        mService.enqueue(new Callback<JsonObject>() {
//                            @Override
//                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                                try {
//                                    if (response.body() != null) {
//                                        JsonObject jsonObject = response.body();
//                                        if (jsonObject.get("response").getAsBoolean()) {
//                                            spotsDialog.dismiss();
//                                            Constants.customToast(getActivity(),"Data submitted successfully",1);
//                                            getFragmentManager().popBackStackImmediate();
//
//                                        } else {
//                                            spotsDialog.dismiss();
//                                            String message = jsonObject.get("message").getAsString().trim();
//                                            Constants.customToast(getActivity(),message,1);
//                                        }
//                                    } else {
//                                        spotsDialog.dismiss();
//                                        Constants.customToast(getActivity(),Constants.MESSAGE_SOMETHING_WENT_WRONG,1);
//
//                                    }
//                                } catch (Exception e) {
//                                    spotsDialog.dismiss();
//                                    e.printStackTrace();
//                                    Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<JsonObject> call, Throwable t) {
//                                spotsDialog.dismiss();
//                                call.cancel();
//                                t.printStackTrace();
//                                Utils.customToast(activity, Utils.failureMessage(t), 0);
//
//                            }
//                        });
//
//                    }
//                }else{
//
//                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
//                            Constants.MESSAGE_CHECK_INTERNET, false);
//
//                }
//
//            }
//        });


        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(activity, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                 try {
                     String result1=result.getContents();
                     qr_code_data_text_view.setText(result1.substring(18));
                 } catch (Exception e) {
                    e.printStackTrace();
                     String result1=result.getContents();
                     qr_code_data_text_view.setText(result1.substring(18));

//                      Toast.makeText(activity, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        Log.v(TAG, "onDetach...");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {

         } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

}

