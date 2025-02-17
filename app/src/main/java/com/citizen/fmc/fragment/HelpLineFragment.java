package com.citizen.fmc.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.citizen.fmc.R;
import com.citizen.fmc.adapter.HelpLineListItemAdapter;
import com.citizen.fmc.model.HelpLineModel;
import com.citizen.fmc.utils.ApiInterface;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ======> Created by dheeraj-gangwar on 2018-01-08 <======
 */

public class HelpLineFragment extends Fragment {
    private ListView helpLineListView;
    private List<HelpLineModel> helpLineModels;
    private Activity activity;
    private String TAG = getClass().getSimpleName();
    private SpotsDialog spotsDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.layout_list_view, container, false);
        activity = getActivity();
        helpLineListView = mView.findViewById(R.id.list_view);

        spotsDialog = new SpotsDialog(activity,
                getResources().getString(R.string.please_wait_dialog_text),
                R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);

        spotsDialog.show();
        prepareDataList();

        listViewItemClick();

        return mView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /* ====================== Adding elements to List ====================== */
    private void prepareDataList() {
        helpLineModels = new ArrayList<>();
        helpLineModels.clear();
        getHelpLineNumbers();
    }
    /* ================================================================================================= */


    /* ====================== Setting adapter to ListView ====================== */
    private void setListViewAdapter() {
        HelpLineListItemAdapter helpLineListItemAdapter = new HelpLineListItemAdapter(activity, helpLineModels);
        helpLineListView.setAdapter(helpLineListItemAdapter);
        helpLineListItemAdapter.notifyDataSetChanged();
    }
    /* ================================================================================================= */


    /* ====================== ListView onItemClickListener ====================== */
    private void listViewItemClick() {
        helpLineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(Intent.ACTION_DIAL)
                        .setData(Uri.parse("tel:" + helpLineModels.get(position).getContactNumber())));
            }
        });
    }
    /* ================================================================================================= */


    /* ====================== Request method to get user's all complaints ====================== */
    private void getHelpLineNumbers() {
        ApiInterface mApiInterface = Utils.getInterfaceService();
        String userToken = Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity);
        int userId = Utils.getUserDetails(activity).getCivilianId();
        Log.v(TAG, "USER_TOKEN: " + userToken);
        Log.v(TAG, "USER_ID: " + String.valueOf(userId));

        Call<JsonObject> mService = mApiInterface.getHelpLineNumbers(userToken, Constants.HEADER_ACCEPT, userId);
        mService.enqueue(new Callback<JsonObject>() {
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
                                        HelpLineModel model = new GsonBuilder().create()
                                                .fromJson(allCompJsonArray.get(i), HelpLineModel.class);
                                        helpLineModels.add(model);
                                    }
                                }

                                setListViewAdapter();

                                Log.v(TAG, helpLineModels.toString());
                                spotsDialog.dismiss();
                            } else {
                                spotsDialog.dismiss();
                                Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                        jsonObject.get("message").getAsString().trim(), false);
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
}
