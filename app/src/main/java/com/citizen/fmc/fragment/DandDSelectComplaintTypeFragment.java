package com.citizen.fmc.fragment;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.citizen.fmc.R;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;

import dmax.dialog.SpotsDialog;

public class DandDSelectComplaintTypeFragment extends Fragment {

    private View mView;
    private final String TAG = getClass().getSimpleName();
    private Activity activity;
    private AppBarLayout appBarLayout;
    private SpotsDialog spotsDialog;
    private int userId;
    LinearLayout twin_bin_ll;
    LinearLayout garbage_ll;
    LinearLayout bad_behaviour_ll;
    LinearLayout not_supportive_ll;
    String epc_code="";
    private String PAGE_TYPE = "";
    private String statusTitle;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_d_and_d_complaint, container, false);
        activity = getActivity();
        appBarLayout = mView.findViewById(R.id.app_bar_layout);
        Utils.setToolbarTitle(activity, "Register Complaint");
        userId = Utils.getUserDetails(activity).getCivilianId();
            epc_code = getArguments().getString("result1");

        twin_bin_ll = mView.findViewById(R.id.twin_bin_ll);
        garbage_ll = mView.findViewById(R.id.garbage_ll);
        bad_behaviour_ll = mView.findViewById(R.id.bad_behaviour_ll);
        not_supportive_ll = mView.findViewById(R.id.not_supportive_ll);

        spotsDialog = new SpotsDialog(activity, getResources().getString(R.string.please_wait_dialog_text), R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);
        twin_bin_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DandDBadBehaviourFragment ldf = new DandDBadBehaviourFragment ();
                Bundle args = new Bundle();
                args.putString("result1", epc_code);
                args.putString("complaint_id", "1");
                args.putString("type", "Segregate Bin Not in Use");

                ldf.setArguments(args);

//Inflate the fragment
                getFragmentManager().beginTransaction().replace(R.id.main_sliding_container, ldf).addToBackStack("").commit();

            }
        });

        garbage_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DandDBadBehaviourFragment ldf = new DandDBadBehaviourFragment ();
                Bundle args = new Bundle();
                args.putString("result1", epc_code);
                ldf.setArguments(args);
                args.putString("complaint_id", "2");
                args.putString("type", "Garbage Polythene");

//Inflate the fragment
                getFragmentManager().beginTransaction().replace(R.id.main_sliding_container, ldf).addToBackStack("").commit();

            }
        });

        bad_behaviour_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DandDBadBehaviourFragment ldf = new DandDBadBehaviourFragment ();
                Bundle args = new Bundle();
                args.putString("result1", epc_code);
                args.putString("complaint_id", "1");
                args.putString("type", "Bad Behaviour");

                ldf.setArguments(args);

//Inflate the fragment
                getFragmentManager().beginTransaction().replace(R.id.main_sliding_container, ldf).addToBackStack("").commit();
//                if (ConnectivityReceiver.isConnected()) {
//                    spotsDialog.show();
//                    ApiInterface mApiInterface = Utils.getInterfaceService();
//
//                    Call<JsonObject> mService = mApiInterface.submitWastage(
//                            RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(userId)),
//                            RequestBody.create(MediaType.parse("multipart/form-data"),  epc_code),
//                            RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(Constants.APP_TYPE)),
//                            RequestBody.create(MediaType.parse("multipart/form-data"), "5"),
//                            RequestBody.create(MediaType.parse("multipart/form-data"), ""),
//                            RequestBody.create(MediaType.parse("multipart/form-data"), "3"),
//                            MultipartBody.Part.createFormData("image_source",""));
//
//
//
//                    mService.enqueue(new Callback<JsonObject>() {
//                        @Override
//                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                            try {
//                                if (response.body() != null) {
//                                    JsonObject jsonObject = response.body();
//                                    if (jsonObject.get("response").getAsBoolean()) {
//                                        spotsDialog.dismiss();
//                                        Constants.customToast(getActivity(),"Data submitted successfully",1);
//                                        getFragmentManager().popBackStackImmediate();
//
//                                    } else {
//                                        spotsDialog.dismiss();
//                                        String message = jsonObject.get("message").getAsString().trim();
//                                        Constants.customToast(getActivity(),message,1);
//                                    }
//                                } else {
//                                    spotsDialog.dismiss();
//                                    Constants.customToast(getActivity(),Constants.MESSAGE_SOMETHING_WENT_WRONG,1);
//
//                                }
//                            } catch (Exception e) {
//                                spotsDialog.dismiss();
//                                e.printStackTrace();
//                                Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<JsonObject> call, Throwable t) {
//                            spotsDialog.dismiss();
//                            call.cancel();
//                            t.printStackTrace();
//                            Utils.customToast(activity, Utils.failureMessage(t), 0);
//
//                        }
//                    });
//
//
//                }else{
//
//                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
//                            Constants.MESSAGE_CHECK_INTERNET, false);
//
//                }

            }
        });

        not_supportive_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DandDBadBehaviourFragment ldf = new DandDBadBehaviourFragment ();
                Bundle args = new Bundle();
               args.putString("result1", epc_code);
                ldf.setArguments(args);

//Inflate the fragment
                getFragmentManager().beginTransaction().replace(R.id.main_sliding_container, ldf).addToBackStack("").commit();

            }
        });

         return mView;
    }
}
