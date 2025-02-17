package com.citizen.fmc.activity;

import android.app.ProgressDialog;
import androidx.appcompat.app.AppCompatActivity;import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;


import com.citizen.fmc.R;
import com.citizen.fmc.adapter.ParkingSummaryAdapter;
import com.citizen.fmc.model.ParkingSummeryResponse.Datum;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParkingSummaryActivity extends AppCompatActivity {
    private List<Datum> parking_list;
    private ParkingSummaryAdapter mAdapter;
    private ExpandableListView mExpandableListView;
    private ArrayList<GroupModel> groupNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_summery);

        mExpandableListView = (ExpandableListView)findViewById(R.id.expandable_parkinglist_view);

        parking_list = new ArrayList<>();

        getParkingSummeryData();

    }




    private void getParkingSummeryData() {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(ParkingSummaryActivity.this, R.style.DialogSlideAnim);
        dialog.setMessage(getResources().getString(R.string.loading_dialog_msg));
        dialog.show();

        JSONObject jsonObject = new JSONObject();
        //jsonObject.put("","");

        Utils.getInterfaceServiceParking().getParkingSummery(jsonObject,Constants.HEADER_ACCEPT)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                       //     if (activity != null && isAdded()) {
                                if (response.body() != null) {
                                    JsonObject jsonObject = response.body();
                                    String message = jsonObject.get("msg").getAsString().trim().toLowerCase();
                                    String status = jsonObject.get("sts").getAsString().trim().toLowerCase();
                                    if (message.equalsIgnoreCase("success") &&
                                        status.equalsIgnoreCase("200")) {
                                        JsonArray allParkingSummaryData = jsonObject.getAsJsonArray("data");

                                        // Adding all complaints to list
                                        if (allParkingSummaryData.size() > 0) {
                                            for (int i = 0; i < allParkingSummaryData.size(); i++) {
                                                Datum model = new GsonBuilder().create()
                                                        .fromJson(allParkingSummaryData.get(i), Datum.class);
                                                parking_list.add(model);
                                            }
                                            groupNameList = new ArrayList<>();
                                            for(int i=0; i<parking_list.size(); i++){
                                                groupNameList.add(new GroupModel(parking_list.get(i).getParkingName(),parking_list.get(i).getParkingId()));
                                            }



                                            mAdapter = new ParkingSummaryAdapter(ParkingSummaryActivity.this, parking_list , groupNameList);
                                            mExpandableListView.setAdapter(mAdapter);
                                            mExpandableListView.setVisibility(View.VISIBLE);


                                           // setStepperAdapter();

                                        }
                                    } else {

                                        Utils.customToast(getApplicationContext(),
                                                Constants.MESSAGE_NO_DATA_FOUND, 0);
                                    }
                                    dialog.dismiss();
                                } else {
                                        dialog.dismiss();
                                    Utils.customToast(getApplicationContext(),
                                            Constants.MESSAGE_NO_DATA_FOUND, 0);
                                }
                          //  }
                        } catch (Exception e) {

                            e.printStackTrace();
                            Utils.customToast(getApplicationContext(), Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                           dialog.dismiss();
                            call.cancel();
                            t.printStackTrace();
                            Utils.customToast(getApplicationContext(), Utils.failureMessage(t), 0);

                    }
                });
    }
}
