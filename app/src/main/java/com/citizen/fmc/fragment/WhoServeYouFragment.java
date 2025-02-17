package com.citizen.fmc.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.citizen.fmc.R;
import com.citizen.fmc.adapter.WhoServeYouAdapter;
import com.citizen.fmc.model.WhoServeYouModel;
import com.citizen.fmc.utils.ApiInterface;
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
 * ======> Created by dheeraj-gangwar on 2018-04-24 <======
 */
public class WhoServeYouFragment extends Fragment {
    private String TAG = getClass().getSimpleName();
    private Activity activity;
    private int MODULE_ID;
    private int DEPARTMENT_ID;
    private ArrayList<WhoServeYouModel> dataList = new ArrayList<>();
    private SpotsDialog spotsDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView mListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.layout_swipe_list_view, container, false);
        activity = getActivity();
        swipeRefreshLayout = mView.findViewById(R.id.parent_layout);
        mListView = mView.findViewById(R.id.list_view);

        String toolbarTitle = getArguments().getString(Constants.KEY_TOOL_BAR_TITLE);
        Utils.setToolbarTitle(activity, toolbarTitle);

        MODULE_ID = getArguments().getInt(Constants.KEY_MODULE_ID, Constants.DEFAULT_INTEGER_VALUE);
        Log.v(TAG, Constants.KEY_MODULE_ID + ": " + MODULE_ID);

        DEPARTMENT_ID = getArguments().getInt(Constants.KEY_DEPARTMENT_ID, Constants.DEFAULT_INTEGER_VALUE);
        Log.v(TAG, Constants.KEY_DEPARTMENT_ID + ": " + DEPARTMENT_ID);

        spotsDialog = new SpotsDialog(activity, getResources().getString(R.string.please_wait_dialog_text),
                R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);

        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_refresh_color_scheme));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareDataList();
            }
        });

        prepareDataList();

        return mView;
    }

    /* ====================== Adding elements to List ====================== */
    private void prepareDataList() {
        spotsDialog.show();
        dataList = new ArrayList<>();
        dataList.clear();
        getWhoServeYouListData(DEPARTMENT_ID);
    }
    /* ================================================================================================= */


    /* ====================== Setting adapter to ListView ====================== */
    private void setListAdapter() {
        WhoServeYouAdapter adapter = new WhoServeYouAdapter(activity, dataList);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        spotsDialog.dismiss();
        swipeRefreshLayout.setRefreshing(false);

        listViewItemClick();
    }
    /* ================================================================================================= */


    /* ====================== ListView onItemClickListener ====================== */
    private void listViewItemClick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(Intent.ACTION_DIAL)
                        .setData(Uri.parse("tel:" + dataList.get(position).getContactNumber())));
            }
        });
    }
    /* ================================================================================================= */


    /* ====================== Request method to get complaint categories ====================== */
    private void getWhoServeYouListData(int DEPARTMENT_ID) {
        ApiInterface mApiInterface = Utils.getInterfaceService();
        Log.v(TAG, Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity));
        Call<JsonObject> mService = mApiInterface.getWhoServeYouData(DEPARTMENT_ID,
                Utils.getUserDetails(activity).getCivilianId(),
                Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(activity),
                Constants.HEADER_ACCEPT);
        mService.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (activity != null && isAdded()) {
                        if (response.body() != null) {
                            JsonObject jsonObject = response.body();
                            if (jsonObject.get("response").getAsBoolean()) {
                                JsonArray departmentJsonArray = jsonObject.getAsJsonArray("data");
                                if (departmentJsonArray.size() > 0) {
                                    for (int i = 0; i < departmentJsonArray.size(); i++) {
                                        WhoServeYouModel model = new GsonBuilder().create()
                                                .fromJson(departmentJsonArray.get(i), WhoServeYouModel.class);
                                        dataList.add(model);
                                    }
                                    Log.v(TAG, dataList.toString());
                                    setListAdapter();
                                }
                            } else {
                                swipeRefreshLayout.setRefreshing(false);
                                spotsDialog.dismiss();
                                Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                        jsonObject.get("message").getAsString().trim(), false);
                            }
                        } else {
                            swipeRefreshLayout.setRefreshing(false);
                            spotsDialog.dismiss();
                            Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                    Constants.MESSAGE_NO_DATA_FOUND, false);
                        }
                    }
                } catch (Exception e) {
                    swipeRefreshLayout.setRefreshing(false);
                    spotsDialog.dismiss();
                    e.printStackTrace();
                    Utils.customToast(activity, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (activity != null && isAdded()) {
                    swipeRefreshLayout.setRefreshing(false);
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
