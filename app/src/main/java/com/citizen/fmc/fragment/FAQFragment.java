package com.citizen.fmc.fragment;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.citizen.fmc.R;
import com.citizen.fmc.adapter.FAQListAdapter;
import com.citizen.fmc.model.FAQModel;
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
 * ======> Created by dheeraj-gangwar on 2018-04-21 <======
 */

public class FAQFragment extends Fragment {
    private Activity activity;
    private ExpandableListView mExpandableListView;
    private ArrayList<FAQModel> faqModelArrayList;
    private String TAG = getClass().getSimpleName();
    private SpotsDialog spotsDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.layout_expandable_list, container, false);
        activity = getActivity();
        mExpandableListView = mView.findViewById(R.id.expandable_list_view);

        spotsDialog = new SpotsDialog(activity,
                getResources().getString(R.string.please_wait_dialog_text),
                R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);

        spotsDialog.show();

        getFAQs();

        return mView;
    }

    private void getFAQs() {
        faqModelArrayList = new ArrayList<>();
        faqModelArrayList.clear();
        Call<JsonObject> call = Utils.getInterfaceService().getFrequentlyAskedQuestions(Utils.getUserDetails(activity).getCivilianId(),
                Constants.APP_TYPE,
                Constants.HEADER_TOKEN_BEARER + " " + Utils.getUserToken(activity),
                Constants.HEADER_ACCEPT);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JsonObject mSectionActObject = response.body();
                    boolean responseStatus = mSectionActObject.get("response").getAsBoolean();
                    if (responseStatus) {
                        JsonArray frequently_asked_qa = mSectionActObject.get("data").getAsJsonArray();
                        if (frequently_asked_qa.size() > 0) {
                            for (int i = 0; i < frequently_asked_qa.size(); i++) {
                                FAQModel frequentlyAskedQuestionsModal = new GsonBuilder()
                                        .create().fromJson(frequently_asked_qa.get(i), FAQModel.class);
                                faqModelArrayList.add(frequentlyAskedQuestionsModal);
                            }
                            Log.v(TAG, faqModelArrayList.toString());

                            FAQListAdapter adapter = new FAQListAdapter(activity, faqModelArrayList);
                            mExpandableListView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            mExpandableListView.expandGroup(0);
                        }
                        spotsDialog.dismiss();
                    } else {
                        Utils.showSnackBar(activity, activity.findViewById(android.R.id.content),
                                Constants.MESSAGE_TRY_AGAIN_LATER, false);
                        spotsDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    spotsDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                call.cancel();
                spotsDialog.dismiss();
                t.printStackTrace();
                Utils.showSnackBar(activity, activity.findViewById(android.R.id.content), Utils.failureMessage(t), false);
            }
        });
    }
}
