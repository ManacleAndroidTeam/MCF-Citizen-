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
import android.widget.ImageView;
import android.widget.Toast;

import com.citizen.fmc.R;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import dmax.dialog.SpotsDialog;

public class DandDScannerFragment extends Fragment {

    private View mView;
    private final String TAG = getClass().getSimpleName();
    private Activity activity;
    private AppBarLayout appBarLayout;
    private SpotsDialog spotsDialog;
    private ImageView mQrSanbtn;
    private IntentIntegrator qrScan;
    private String PAGE_TYPE = "";
    private String statusTitle;
    int userId;
    EditText nddn_edit_text;
    Button submit_complaint_button;


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

        mView = inflater.inflate(R.layout.fragment_d_and_d_scan, container, false);
        activity = getActivity();
        appBarLayout = mView.findViewById(R.id.app_bar_layout);
        mQrSanbtn=mView.findViewById(R.id.scan_qr_code);
        nddn_edit_text=mView.findViewById(R.id.nddn_edit_text);
        submit_complaint_button=mView.findViewById(R.id.submit_complaint_button);
//       nddn_edit_text.setText("NDMC-LBN290156-G01");
       spotsDialog = new SpotsDialog(activity, getResources().getString(R.string.please_wait_dialog_text), R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);
        qrScan = new IntentIntegrator(getActivity());
        Utils.setToolbarTitle(activity, "Select Qr Scan");
        userId = Utils.getUserDetails(activity).getCivilianId();

        mQrSanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.initiateScan();

            }
        });


        submit_complaint_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!nddn_edit_text.getText().toString().isEmpty()) {


                    DandDChooseTypeFragment ldf = new DandDChooseTypeFragment();
                    Bundle args = new Bundle();
                    args.putString("result1", nddn_edit_text.getText().toString());
                    ldf.setArguments(args);

//Inflate the fragment
                    getFragmentManager().beginTransaction().replace(R.id.main_sliding_container, ldf).addToBackStack("").commit();
                }else{
                  Constants.customToast(activity,"Please type nddn Id first",1);
                }
            }
        });

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
                    String result2=result1.substring(21);
                    DandDChooseTypeFragment ldf = new DandDChooseTypeFragment ();
                    Bundle args = new Bundle();
                    args.putString("result1", result2);
                    ldf.setArguments(args);

//Inflate the fragment
                    getFragmentManager().beginTransaction().replace(R.id.main_sliding_container, ldf).addToBackStack("").commit();
//                    Utils.changeFragment(getFragmentManager(), new ViewMyComplaintsFragment());

//                    qr_code_data_text_view.setText(result1.substring(18));
                } catch (Exception e) {
                    e.printStackTrace();
                    String result1=result.getContents();
//                    qr_code_data_text_view.setText(result1.substring(18));

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
