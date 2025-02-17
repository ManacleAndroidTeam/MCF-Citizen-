package com.citizen.fmc.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.adapter.ServerNotificationListAdapter;
import com.citizen.fmc.model.ServerNotificationModel;
import com.citizen.fmc.utils.ApiInterface;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ======> Created by dheeraj-gangwar on 2018-03-12 <======
 */

public class UserNotificationActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView mListView;
    CoordinatorLayout coordinatorLayout;
    TextView noDataTextView;
    private ApiInterface apiInterface;
    private ArrayList<ServerNotificationModel> arrayList;
    private ProgressDialog mDialog;
    private String notification_id;
    private boolean firstTimeCall = true;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_notification);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utils.setToolbarTitle(UserNotificationActivity.this,"Notifications");

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        mListView = findViewById(R.id.notification_list_view);
        noDataTextView = findViewById(R.id.no_data_found_text_view);

        apiInterface = Utils.getInterfaceService();
        mDialog = new ProgressDialog(UserNotificationActivity.this, R.style.DialogSlideAnim);
        mDialog.setMessage(getString(R.string.loading_dialog_msg));
        mDialog.setCancelable(false);

        getAllNotifications();

        //ListView on click event:-
        mListView.setOnItemClickListener(this);
    }

    private void readNotification(int notificationID , final String complaintNum) {
        mDialog.show();
        apiInterface.updateNotificationReadStatus(notificationID,
                Constants.HEADER_TOKEN_BEARER ,
                Constants.HEADER_ACCEPT)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject mResponseObject = response.body();
                        if (mResponseObject != null) {
                            if (mResponseObject.get("response").getAsBoolean()) {
                                mDialog.dismiss();
                                Intent intent = new Intent(UserNotificationActivity.this , ShowComplaintHistoryActivity.class);
                                intent.putExtra(Constants.KEY_COMPLAINT_NUM , complaintNum);
                                startActivity(intent);
                            } else {
                                mDialog.dismiss();
                                Constants.customToast(UserNotificationActivity.this,
                                        Constants.MESSAGE_NO_DATA_FOUND, 2);
                            }
                        } else {
                            mDialog.dismiss();
                            Constants.customToast(UserNotificationActivity.this,
                                    Constants.MESSAGE_SOMETHING_WENT_WRONG, 2);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        call.cancel();
                        mDialog.dismiss();
                        t.printStackTrace();
                        Constants.customToast(UserNotificationActivity.this,
                                Constants.failureMessage(t), 0);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment frg = getSupportFragmentManager().findFragmentById(R.id.main_sliding_container);
        if (frg != null) {
            frg.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getAllNotifications() {
        mDialog.show();
        arrayList = new ArrayList<>();
        arrayList.clear();
        apiInterface.getAllNotifications(2,
                Utils.getUserDetails(UserNotificationActivity.this).getCivilianId(),
                Constants.HEADER_TOKEN_BEARER + " " + Utils.getUserToken(UserNotificationActivity.this),
                Constants.HEADER_ACCEPT)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject mResponseObject = response.body();
                        if (mResponseObject != null) {
                            if (mResponseObject.get("response").getAsBoolean()) {
                                JsonArray jsonArray = mResponseObject.getAsJsonArray("list");
                                String unread_count = mResponseObject.get("unread_count").getAsString();
                                SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Constants.NOTIFICATION_UNREAD_COUNT, unread_count);
                                editor.apply();
                                if (jsonArray.size() > 0) {
                                    for (int x = 0; x < jsonArray.size(); x++) {
                                        ServerNotificationModel model = new GsonBuilder().create()
                                                .fromJson(jsonArray.get(x), ServerNotificationModel.class);arrayList.add(model);
                                    }
                                }
                                if (arrayList.size() > 0) {
                                    noDataTextView.setVisibility(View.GONE);
                                    mListView.setVisibility(View.VISIBLE);
                                    ServerNotificationListAdapter adapter = new ServerNotificationListAdapter(UserNotificationActivity.this, arrayList);
                                    mListView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    noDataTextView.setVisibility(View.VISIBLE);
                                    mListView.setVisibility(View.GONE);
                                }

                                mDialog.dismiss();
                            } else {
                                mDialog.dismiss();
                                Constants.customToast(UserNotificationActivity.this,
                                        Constants.MESSAGE_NO_DATA_FOUND, 2);
                            }
                        } else {
                            mDialog.dismiss();
                            Constants.customToast(UserNotificationActivity.this,
                                    Constants.MESSAGE_SOMETHING_WENT_WRONG, 2);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        call.cancel();
                        mDialog.dismiss();
                        t.printStackTrace();
                        Constants.customToast(UserNotificationActivity.this,
                                Constants.failureMessage(t), 0);
                    }
                });

    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    private void showDetailsDialog(String imagePath, String dateTime, String title, String body) {
        Dialog dialog = new Dialog(UserNotificationActivity.this, R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.layout_user_notification_dialog);
        SimpleDraweeView fullImageView = dialog.findViewById(R.id.full_image_view);
        TextView dateTimeTextView = dialog.findViewById(R.id.date_time_stamp_text_view);
        TextView titleTextView = dialog.findViewById(R.id.title_text_view);
        TextView bodyTextView = dialog.findViewById(R.id.body_text_view);

        if (imagePath.isEmpty()) {
            fullImageView.setVisibility(View.GONE);
        } else {
            fullImageView.setImageURI(Uri.parse(imagePath));
        }

        try {
            dateTimeTextView.setText(Constants.setFontAwesome(UserNotificationActivity.this,
                    dateTimeTextView, "f017")
                    + "  " + new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a")
                    .format(new SimpleDateFormat("yyyyMMddHHmmss")
                            .parse(dateTime)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        titleTextView.setText(title);
        bodyTextView.setText(body);

        dialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.notification_list_view) {
            readNotification(arrayList.get(position).getNotificationID(), arrayList.get(position).getTitle());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(firstTimeCall){
            firstTimeCall = false;
        }else if(!firstTimeCall){
            getAllNotifications();
            firstTimeCall = true;
        }
    }
}
