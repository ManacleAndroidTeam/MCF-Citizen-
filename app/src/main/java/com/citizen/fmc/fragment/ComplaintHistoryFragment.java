package com.citizen.fmc.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ======> Created by dheeraj-gangwar on 2018-04-25 <======
 */
public class ComplaintHistoryFragment extends Fragment {
    private FragmentActivity activity;
    private String TAG = getClass().getSimpleName();
    private ArrayList<HistoryModel> dataList;
    private SpotsDialog spotsDialog;
    private ListView mListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_complaint_history, container, false);
        activity = getActivity();
        TextView durationTextView = mView.findViewById(R.id.complaint_duration_text_view);
        TextView complaintNumTextView = mView.findViewById(R.id.complaint_num_text_view);
        mListView = mView.findViewById(R.id.history_list_view);
        Utils.setToolbarTitle(activity, "History");

        String complaintNum = getArguments().getString(Constants.KEY_COMPLAINT_NUM);

        long days = getArguments().getLong(Constants.KEY_COMPLAINT_DAYS, Constants.DEFAULT_INTEGER_VALUE);
        long hours = getArguments().getLong(Constants.KEY_COMPLAINT_HOURS, Constants.DEFAULT_INTEGER_VALUE);
        long minutes = getArguments().getLong(Constants.KEY_COMPLAINT_MINUTES, Constants.DEFAULT_INTEGER_VALUE);

        complaintNumTextView.setText("#" + complaintNum);
        durationTextView.setText(Utils.getComplaintDuration(days, hours, minutes));

        spotsDialog = new SpotsDialog(activity, getResources().getString(R.string.please_wait_dialog_text),
                R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);

        prepareDataList(complaintNum);

        return mView;
    }

    private void prepareDataList(String complaintNum) {
        dataList = new ArrayList<>();
        dataList.clear();
        spotsDialog.show();
        getComplaintHistory(complaintNum);
    }

    private void setStepperAdapter() {
        HistoryStepperAdapter adapter = new HistoryStepperAdapter(activity, dataList);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /* ====================== Request method to get complaint history ====================== */
    private void getComplaintHistory(String complaintNum) {
        String userToken = Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity);
        int userId = Utils.getUserDetails(activity).getCivilianId();
        Log.v(TAG, "USER_TOKEN: " + userToken);
        Log.v(TAG, "USER_ID: " + String.valueOf(userId));

        Utils.getInterfaceService().getComplaintHistory(complaintNum, userId, userToken, Constants.HEADER_ACCEPT)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try {
                            if (activity != null && isAdded()) {
                                if (response.body() != null) {
                                    JsonObject jsonObject = response.body();
                                    if (jsonObject.get("response").getAsBoolean()) {
                                        JsonArray allCompJsonArray = jsonObject.getAsJsonArray("data");

                                        // Adding all complaints to list
                                        if (allCompJsonArray.size() > 0) {
                                            for (int i = 0; i < allCompJsonArray.size(); i++) {
                                                HistoryModel model = new GsonBuilder().create()
                                                        .fromJson(allCompJsonArray.get(i), HistoryModel.class);
                                                dataList.add(model);
                                            }

                                            setStepperAdapter();
                                            Log.v(TAG, dataList.toString());
                                            spotsDialog.dismiss();
                                        }
                                    } else {
                                        spotsDialog.dismiss();
                                        Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                                Constants.MESSAGE_NO_DATA_FOUND, false);
                                    }
                                } else {
                                    spotsDialog.dismiss();
                                    Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                            Constants.MESSAGE_NO_DATA_FOUND, false);
                                }
                            }
                        } catch (Exception e) {
                            spotsDialog.dismiss();
                            e.printStackTrace();
                            Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        if (activity != null && isAdded()) {
                            spotsDialog.dismiss();
                            call.cancel();
                            t.printStackTrace();
                            Utils.showSnackBar(activity, activity.findViewById(android.R.id.content), Utils.failureMessage(t), false);
                        }
                    }
                });
    }
    /* ================================================================================================= */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_filter).setVisible(false);
        menu.findItem(R.id.action_notification).setVisible(false);
    }
}
