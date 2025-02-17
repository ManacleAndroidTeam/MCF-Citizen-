package com.citizen.fmc.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.adapter.FeedbackComplainAdapter;
import com.citizen.fmc.model.FeedbackOptionModel;
import com.citizen.fmc.utils.ButtonView;
import com.citizen.fmc.utils.ConnectivityReceiver;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.login.widget.ProfilePictureView.TAG;

public class ComplaintFeedbackFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private ButtonView feedback_btn;
    private EditText feedback_edit_text;
    private Spinner feedback_spinner;
    private SpotsDialog spotsDialog;
    private Activity activity;
    private int feedbackId = 0;
    private String complaintNum = "";
    FeedbackComplainAdapter feedbackComplainAdapter;
    SimpleDraweeView img_feedback_one, img_feedback_two,img_feedback_three;
    TextView tv_Name_three,tv_Name_two,tv_Name_oneX;
    LinearLayout linear_feedbackJ;
    TextView textView;
    TextView tv_Name_oneID;
    TextView tv_Name_twoID;
    TextView tv_Name_threeID;
    ImageView imageView;
    LinearLayout layoutSection;
    LinearLayout linear_one;
    LinearLayout linear_two;
    LinearLayout linear_three;

    LinearLayout.LayoutParams params;
    private ArrayList<FeedbackOptionModel> feedbackOptionModelList;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (this.getArguments() != null) {
            feedbackOptionModelList = new ArrayList<>();

            feedbackOptionModelList = (ArrayList<FeedbackOptionModel>) getArguments().getSerializable(Constants.USER_DATA);
            complaintNum = getArguments().getString(Constants.KEY_COMPLAINT_NUM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_complaint_feedback, container, false);
        params(mView);
        activity = getActivity();
        Utils.setToolbarTitle(activity, "Complaint FeedBack");
        spotsDialog = new SpotsDialog(activity, getResources().getString(R.string.please_wait_dialog_text),
                R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);
      //  setFeedbacktSpinner();
       // setFeedbackDynamic();

        setFeedbackOptiomOne();
        setFeedbackOptiomTwo();
        setFeedbackOptiomThree();



        linear_one.setBackgroundResource(R.drawable.hyperlink_bottom);
        feedbackId = Integer.valueOf(tv_Name_oneID.getText().toString());

        linear_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_one.setBackgroundResource(R.drawable.hyperlink_bottom);
                feedbackId = Integer.valueOf(tv_Name_oneID.getText().toString());
                linear_two.setBackgroundResource(android.R.color.transparent);
                linear_three.setBackgroundResource(android.R.color.transparent);
            }
        });
        linear_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_two.setBackgroundResource(R.drawable.hyperlink_bottom);
                feedbackId = Integer.valueOf(tv_Name_twoID.getText().toString());
                linear_one.setBackgroundResource(android.R.color.transparent);
                linear_three.setBackgroundResource(android.R.color.transparent);
            }
        });
        linear_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_three.setBackgroundResource(R.drawable.hyperlink_bottom);
                feedbackId = Integer.valueOf(tv_Name_threeID.getText().toString());
                linear_two.setBackgroundResource(android.R.color.transparent);
                linear_one.setBackgroundResource(android.R.color.transparent);
            }
        });
        return mView;
    }

    private void params(View view) {
        feedback_btn = view.findViewById(R.id.feedback_btn);
        feedback_edit_text = view.findViewById(R.id.feedback_edit_text);
        feedback_spinner = view.findViewById(R.id.feedback_spinner);
        img_feedback_one = view.findViewById(R.id.img_feedback_one);
        img_feedback_two = view.findViewById(R.id.img_feedback_two);
        img_feedback_three = view.findViewById(R.id.img_feedback_three);
        tv_Name_three = view.findViewById(R.id.tv_Name_three);
        tv_Name_two = view.findViewById(R.id.tv_Name_two);
        tv_Name_oneX = view.findViewById(R.id.tv_Name_oneX);
        linear_feedbackJ = view.findViewById(R.id.linear_feedbackX);
        tv_Name_oneID = view.findViewById(R.id.tv_Name_oneIDX);
        tv_Name_twoID = view.findViewById(R.id.tv_Name_twoIDX);
        tv_Name_threeID = view.findViewById(R.id.tv_Name_threeIDX);
        linear_one = view.findViewById(R.id.linear_oneX);
        linear_two = view.findViewById(R.id.linear_twoX);
        linear_three = view.findViewById(R.id.linear_threeX);
         layoutSection = new LinearLayout(getActivity());
         textView = new TextView(getActivity());
        feedback_btn.setOnClickListener(this);

         params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    private void setFeedbackOptiomOne()
    {
//        for (int i = 0; i < feedbackOptionModelList.size(); i++)
        {
            tv_Name_oneX.setText(feedbackOptionModelList.get(0).getOptions());
            img_feedback_one.setController(Utils.simpleDraweeController(
                    Constants.IMAGE_URL + feedbackOptionModelList.get(0).getImages(),
                    100, 100));
            String id = String.valueOf(feedbackOptionModelList.get(0).getOptionId());
           tv_Name_oneID.setText(id);
        }
    }

    private void setFeedbackOptiomTwo()
    {
//        for (int i = 0; i < feedbackOptionModelList.size(); i++)
        {
            tv_Name_two.setText(feedbackOptionModelList.get(1).getOptions());
            img_feedback_two.setController(Utils.simpleDraweeController(
                    Constants.IMAGE_URL + feedbackOptionModelList.get(1).getImages(),
                    100, 100));
            String id = String.valueOf(feedbackOptionModelList.get(1).getOptionId());

           tv_Name_twoID.setText(id);
        }
    }

    private void setFeedbackOptiomThree()
    {
//        for (int i = 0; i < feedbackOptionModelList.size(); i++)
        {
            tv_Name_three.setText(feedbackOptionModelList.get(2).getOptions());
            img_feedback_three.setController(Utils.simpleDraweeController(
                    Constants.IMAGE_URL + feedbackOptionModelList.get(2).getImages(),
                    100, 100));
            String id = String.valueOf(feedbackOptionModelList.get(2).getOptionId());

              tv_Name_threeID.setText(id);
        }
    }


  /*  private void setFeedbacktSpinner() {
        final List<String> stringList = new ArrayList<>();
        int i =0;
        for ( i = 0; i < feedbackOptionModelList.size(); i++)
        *//*    textView.setText(feedbackOptionModelList.get(i).getOptions());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT

        );

        textView.setLayoutParams(params);
        linear_feedbackJ.addView(textView);*//*

        *//*    imageView.setController(Utils.simpleDraweeController(
                    Constants.IMAGE_URL + feedbackOptionModelList.get(i).getImages(),
                    100, 100));*//*
        params.setLayoutDirection(LinearLayout.VERTICAL);
        params.gravity = Gravity.CENTER;
        params.setMargins(5,5,5,5);
     //   imageView.setImageResource(R.drawable.image);

    //    imageView.setLayoutParams(params);
        TextView textView = new TextView(getActivity());
        textView.setText(feedbackOptionModelList.get(i).getOptions());
        layoutSection.addView((textView)stringList.get(i));
      //  layoutSection.addView((imageView)stringList.get(i));



         *//*   stringList.add(feedbackOptionModelList.get(i).getOptions());
        feedback_spinner.setAdapter(new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, stringList));
        feedback_spinner.setOnItemSelectedListener(this);*//*
    }*/

    private void setFeedbackDynamic() {
        LayoutInflater layoutInfralte = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        // LinearLayout linearLayout=(LinearLayout)findViewById(R.id.infolayout);
        List views = new ArrayList();

    /*   Iterator<latlng>ite=NavigationContext.getInstance().getArray().iterator();
     //   latlng temp;
        while(ite.hasNext()){
            temp=ite.next();*/
        for (int j = 0; j < feedbackOptionModelList.size(); j++) {
            View view = layoutInfralte.inflate(R.layout.layout_feedback, null);
            // Edit
            TextView tv = (TextView) view.findViewById(R.id.tv_Name_oneX);
            tv.setText(feedbackOptionModelList.get(j).getOptions());
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            views.add(view);
            //  }
        }
        int i = 0;
        for (i = 0; i < views.size(); i++)
            linear_feedbackJ.addView((View)views.get(i)) ;

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getId() == R.id.feedback_spinner){
            feedbackId = feedbackOptionModelList.get(position).getOptionId();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.feedback_btn) {
            if (ConnectivityReceiver.isConnected()) {
                if (feedback_edit_text.getText().toString().isEmpty()) {
                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                            "Please fill feedback edit box", false);
                } else {
                    spotsDialog.show();
                    submitFeedback();
                }
            } else {
                Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                        Constants.MESSAGE_CHECK_INTERNET, false);
            }
        }
    }

    private void submitFeedback() {
        Call<JsonObject> callCreateProject = Utils.getInterfaceService().addFeedbackOption(Utils.getUserDetails(activity).getCivilianId(),
                complaintNum, feedbackId, feedback_edit_text.getText().toString(),
                Constants.HEADER_TOKEN_BEARER + " " + Utils.getUserToken(activity),
                Constants.HEADER_ACCEPT);

        callCreateProject.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (response.body() != null) {
                        JsonObject jsonObject = response.body();
                        if (jsonObject.get("response").getAsBoolean()) {
                            spotsDialog.dismiss();
                            showSuccessDialog();
                        } else {
                            spotsDialog.dismiss();
                            Log.v(TAG, "Message===>" + jsonObject.get("message").getAsString().trim());
                            Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                    jsonObject.get("message").getAsString().trim(), false);
                        }
                    } else {
                        spotsDialog.dismiss();
                        Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                Constants.MESSAGE_TRY_AGAIN_LATER, false);
                    }
                } catch (Exception e) {
                    spotsDialog.dismiss();
                    e.printStackTrace();
                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                            Constants.MESSAGE_SOMETHING_WENT_WRONG, false);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                spotsDialog.dismiss();
                call.cancel();
                t.printStackTrace();
                Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                        Utils.failureMessage(t), false);
            }
        });
    }

    private void showSuccessDialog() {
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

        cancelButton.setVisibility(View.GONE);
        dialogInfoMessage.setVisibility(View.GONE);

        titleIcon.setImageResource(R.drawable.ic_information);
        titleText.setText(R.string.Information);
        dialogMessage.setText(Html.fromHtml("<strong>Feedback submitted successfully !</strong>"));

        OKButton.setText(R.string.ok);
        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Utils.changeFragmentWithoutBackStack(getFragmentManager(), new HomeFragment());
            }
        });

        dialog.show();
    }


}
