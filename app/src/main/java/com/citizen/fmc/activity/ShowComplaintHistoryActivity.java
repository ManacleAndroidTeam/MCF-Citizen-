package com.citizen.fmc.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.adapter.HistoryStepperAdapter;
import com.citizen.fmc.model.HistoryModel;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowComplaintHistoryActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();
    private ArrayList<HistoryModel> dataList;
    private SpotsDialog spotsDialog;
    private ListView mListView;
    private String complaintNum;
    private String currentDateTime;
    private Calendar calendar = Calendar.getInstance();
    private HistoryModel model;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private TextView durationTextView;
    private TextView complaintNumTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_complaint_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("History");

        durationTextView = findViewById(R.id.complaint_duration_text_view);
        complaintNumTextView = findViewById(R.id.complaint_num_text_view);
        mListView = findViewById(R.id.history_list_view);
        currentDateTime = dateFormat.format(calendar.getTime());

        if(getIntent() != null){
             complaintNum = getIntent().getStringExtra(Constants.KEY_COMPLAINT_NUM);
        }
        complaintNumTextView.setText("#" + complaintNum);

        spotsDialog = new SpotsDialog(ShowComplaintHistoryActivity.this, getResources().getString(R.string.please_wait_dialog_text), R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);
        prepareDataList(complaintNum);
    }

    private void prepareDataList(String complaintNum) {
        dataList = new ArrayList<>();
        dataList.clear();
        spotsDialog.show();
        getComplaintHistory(complaintNum);
    }

    public void printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        if(elapsedDays == 0 && elapsedHours >0){
            durationTextView.setText(String.valueOf(elapsedHours + " Hours"));
        }
        if(elapsedDays == 0 && elapsedHours == 0 && elapsedMinutes >0){
            durationTextView.setText(String.valueOf(elapsedMinutes + " Minutes"));
        }
        if(elapsedDays == 0 && elapsedHours == 0 && elapsedMinutes == 0 && elapsedSeconds >0){
            durationTextView.setText("less than a minute");
        }
        if(elapsedDays >0){
            durationTextView.setText(String.valueOf(elapsedDays + " Days"));
        }
    }

    private void getComplaintHistory(String complaintNum) {
        String userToken = Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(ShowComplaintHistoryActivity.this);
        int userId = Utils.getUserDetails(ShowComplaintHistoryActivity.this).getCivilianId();
        Log.v(TAG, "USER_TOKEN: " + userToken);
        Log.v(TAG, "USER_ID: " + String.valueOf(userId));

        Utils.getInterfaceService().getComplaintHistory(complaintNum, userId, userToken, Constants.HEADER_ACCEPT)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                                if (response.body() != null) {
                                    JsonObject jsonObject = response.body();
                                    if (jsonObject.get("response").getAsBoolean()) {
                                        JsonArray allCompJsonArray = jsonObject.getAsJsonArray("data");

                                        // Adding all complaints to list
                                        if (allCompJsonArray.size() > 0) {
                                            for (int i = 0; i < allCompJsonArray.size(); i++) {
                                                 model = new GsonBuilder().create()
                                                        .fromJson(allCompJsonArray.get(i), HistoryModel.class);
                                                dataList.add(model);
                                            }

                                            setStepperAdapter();
                                            setDateDifferenceData();
                                            Log.v(TAG, dataList.toString());
                                            spotsDialog.dismiss();
                                        }
                                    } else {
                                        spotsDialog.dismiss();
                                       Constants.customToast(ShowComplaintHistoryActivity.this , Constants.MESSAGE_NO_DATA_FOUND , 2);
                                    }
                                } else {
                                    spotsDialog.dismiss();
                                    Constants.customToast(ShowComplaintHistoryActivity.this , Constants.MESSAGE_NO_DATA_FOUND , 2);
                                }

                        } catch (Exception e) {
                            spotsDialog.dismiss();
                            e.printStackTrace();
                            Constants.customToast(ShowComplaintHistoryActivity.this , Constants.MESSAGE_SOMETHING_WENT_WRONG , 2);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                            spotsDialog.dismiss();
                            call.cancel();
                            t.printStackTrace();
                        Constants.customToast(ShowComplaintHistoryActivity.this , Utils.failureMessage(t) , 2);

                    }
                });
    }

    private void setDateDifferenceData() {

        try {
            Date date1 = dateFormat.parse(model.getUpdatedAt());
            Date date2 = dateFormat.parse(currentDateTime);

            printDifference(date1, date2);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setStepperAdapter() {
        HistoryStepperAdapter adapter = new HistoryStepperAdapter(ShowComplaintHistoryActivity.this, dataList);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
