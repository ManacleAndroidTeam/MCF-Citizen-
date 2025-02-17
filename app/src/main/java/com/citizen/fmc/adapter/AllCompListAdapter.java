package com.citizen.fmc.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.citizen.fmc.R;
import com.citizen.fmc.fragment.ComplaintCommentsFragment;
import com.citizen.fmc.fragment.ComplaintFeedbackFragment;
import com.citizen.fmc.fragment.ComplaintHistoryFragment;
import com.citizen.fmc.fragment.NewComplaintFragment;
import com.citizen.fmc.fragment.ReopenComplaint;
import com.citizen.fmc.fragment.ViewMyComplaintsFragment;
import com.citizen.fmc.model.AllComplaintsModel;
import com.citizen.fmc.model.ComplaintStatusModel;
import com.citizen.fmc.model.FeedbackOptionModel;
import com.citizen.fmc.utils.ApiInterface;
import com.citizen.fmc.utils.ButtonView;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ======> Created by dheeraj-gangwar on 2018-01-25 <======
 */

@SuppressLint("SetTextI18n")
public class AllCompListAdapter extends ArrayAdapter<AllComplaintsModel> implements Filterable {
    private Context mContext;
    private FragmentManager fragmentManager;
    String statusTitle = "";
    String statusColorCode= "";
    private List<AllComplaintsModel> mainDataList;
    private List<ComplaintStatusModel> statusModelList;
    private List<AllComplaintsModel> filteredList;
    private List<FeedbackOptionModel> feedbackOptionModelList;
    private CustomFilter customFilter;
    private String TAG = getClass().getSimpleName();

    public AllCompListAdapter(@NonNull Context context,
                              FragmentManager fragmentManager,
                              @NonNull List<AllComplaintsModel> objects,
                              List<ComplaintStatusModel> statusModelList, List<FeedbackOptionModel> feedbackOptionModelList) {
        super(context, 0, objects);
        this.mContext = context;
        this.fragmentManager = fragmentManager;
        this.mainDataList = objects;
        this.filteredList = new ArrayList<>();
        this.statusModelList = statusModelList;
        this.filteredList.addAll(mainDataList);
        this.feedbackOptionModelList = feedbackOptionModelList;
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Nullable
    @Override
    public AllComplaintsModel getItem(int position) {
        return filteredList.get(position);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_all_complaints_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.parentLayout = convertView.findViewById(R.id.parent_layout);
            viewHolder.serialNumTV = convertView.findViewById(R.id.serial_num_text_view);
            viewHolder.compSDV = convertView.findViewById(R.id.complaint_image_view);
            viewHolder.compNumTV = convertView.findViewById(R.id.complaint_num);
            viewHolder.compDateTV = convertView.findViewById(R.id.complaint_date_time);
            viewHolder.compTypeTV = convertView.findViewById(R.id.complaint_type);
            viewHolder.compAddTV = convertView.findViewById(R.id.complaint_address);
            viewHolder.compStatusTV = convertView.findViewById(R.id.complaint_status);
            viewHolder.commentsIV = convertView.findViewById(R.id.comments_image_view);
            viewHolder.historyIV = convertView.findViewById(R.id.history_image_view);
            viewHolder.feedback_buttonview = convertView.findViewById(R.id.feedback_buttonview);
            viewHolder.com_reopen_text_view = convertView.findViewById(R.id.com_reopen_text_view);
            viewHolder.layout_feedback = convertView.findViewById(R.id.layout_feedback);
            viewHolder.feedback_icon = convertView.findViewById(R.id.feedback_icon);
            viewHolder.feedback_status = convertView.findViewById(R.id.feedback_status);
            viewHolder.vote_buttonview = convertView.findViewById(R.id.vote_buttonview);
            viewHolder.count_status_text_view = convertView.findViewById(R.id.count_status_text_view);
            viewHolder.comp_status_text_view = convertView.findViewById(R.id.comp_status_text_view);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

    //    ComplaintStatusModel complaintStatusModel = statusModelList.get(position);


        final AllComplaintsModel listItem = getItem(position);
        if (listItem != null) {

         /*   int unicode = 0x1F601;
            int uni = 0x1F621;
            String happy = "Feeling happy " + getEmojiByUnicode(uni);

            String myString = "U+1F621";
            String myStr = "U+1F621";*/

            //viewHolder.feedback_icon.setText(String.valueOf(Character.toChars(Integer.parseInt(myStr.substring(2), 16))));
            viewHolder.feedback_icon.setImageResource(R.drawable.satisfied_emoji);
            viewHolder.serialNumTV.setText(String.valueOf(position + 1) + ".");
            viewHolder.compNumTV.setText(mContext.getResources().getString(R.string.comp_num) + listItem.getCompNum());
            viewHolder.compDateTV.setText(Html.fromHtml("<strong>" + mContext.getResources().getString(R.string.comp_date_time) + "</strong> "
                    + Utils.formatServerDateAndTimeToUser(listItem.getCompDate() + " " + listItem.getCompTime())));
            viewHolder.compTypeTV.setText(Html.fromHtml("<strong>" + mContext.getResources().getString(R.string.comp_category) + "</strong> " + listItem.getCompType()));
            viewHolder.compAddTV.setText(Html.fromHtml("<strong>" + mContext.getResources().getString(R.string.comp_address) + "</strong> " + listItem.getCompGeoAdd()));
            /*viewHolder.compSDV.setController(Utils.simpleDraweeController(
                    Constants.IMAGE_URL + listItem.getCompImageURL(),
                    100, 100));*/
            if (listItem.getCompImageURL()!=null && (!listItem.getCompImageURL().isEmpty()))
            {

                Glide.with(viewHolder.compSDV)
                        .load(Constants.IMAGE_PUBLIC_URL+listItem.getCompImageURL())
                        .placeholder(R.drawable.ic_loading_small_dots)
                        .error(R.drawable.place_holder_no_image)
                        .into(viewHolder.compSDV);
            }

            Log.v(TAG, Constants.IMAGE_URL + listItem.getCompImageURL());

            for (int i = 0; i < statusModelList.size(); i++) {
                if (listItem.getCurrentStatus() == statusModelList.get(i).getStatusId()) {
                    viewHolder.compStatusTV
                            .setBackgroundColor(Color.parseColor(statusModelList.get(i).getStatusColorCode().toUpperCase().trim()));
                    viewHolder.compStatusTV.setText(statusModelList.get(i).getStatusName());
                    break;
                }
            }
            if (listItem.getCurrentStatus() == 5 || listItem.getCurrentStatus() == 6
                    || listItem.getCurrentStatus() == 9) {
                viewHolder.com_reopen_text_view.setVisibility(View.VISIBLE);
                viewHolder.com_reopen_text_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReopenComplaint reopenComplaint = new ReopenComplaint();
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.KEY_COMPLAINT_NUM, listItem.getCompNum());
                        reopenComplaint.setArguments(bundle);
                        Utils.changeFragment(fragmentManager, reopenComplaint);
                    }
                });
            } else {
                viewHolder.com_reopen_text_view.setVisibility(View.GONE);
            }

            viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AllComplaintsModel _item = mainDataList.get(position);
                    NewComplaintFragment complaintFragment = new NewComplaintFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.KEY_COMPLAINT_PAGE_TYPE, Constants.VALUE_SHOW_COMPLAINT);
                    bundle.putSerializable(Constants.KEY_COMPLAINT_DATA, _item);
                    for (int i = 0; i < statusModelList.size(); i++) {
                        if (_item.getCurrentStatus() == statusModelList.get(i).getStatusId()) {
                            bundle.putString(Constants.KEY_STATUS_TITLE, statusModelList.get(i).getStatusName().trim());
                            bundle.putString(Constants.KEY_STATUS_COLOR_CODE, statusModelList.get(i).getStatusColorCode().toUpperCase().trim());
//                            bundle.putString(Constants.KEY_IMAGE_PATH, statusModelList.get(i).);
                            break;
                        }
                    }
                    complaintFragment.setArguments(bundle);
                    Utils.changeFragment(fragmentManager, complaintFragment);
                }
            });

            viewHolder.commentsIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ComplaintCommentsFragment complaintCommentsFragment = new ComplaintCommentsFragment();
                    Bundle mBundle = new Bundle();
                    if (listItem.getDays() > 0) {
                        mBundle.putLong(Constants.KEY_COMPLAINT_DAYS, listItem.getDays());
                    } else if (listItem.getHours() > 0) {
                        mBundle.putLong(Constants.KEY_COMPLAINT_HOURS, listItem.getHours());
                    } else {
                        mBundle.putLong(Constants.KEY_COMPLAINT_MINUTES, listItem.getMinutes());
                    }
                    mBundle.putString(Constants.KEY_COMPLAINT_NUM, listItem.getCompNum());
                    complaintCommentsFragment.setArguments(mBundle);
                    Utils.changeFragment(fragmentManager, complaintCommentsFragment);
                }
            });

            viewHolder.historyIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ComplaintHistoryFragment allCommentsFragment = new ComplaintHistoryFragment();
                    Bundle mBundle = new Bundle();
                    if (listItem.getDays() > 0) {
                        mBundle.putLong(Constants.KEY_COMPLAINT_DAYS, listItem.getDays());
                    } else if (listItem.getHours() > 0) {
                        mBundle.putLong(Constants.KEY_COMPLAINT_HOURS, listItem.getHours());
                    } else {
                        mBundle.putLong(Constants.KEY_COMPLAINT_MINUTES, listItem.getMinutes());
                    }
                    mBundle.putString(Constants.KEY_COMPLAINT_NUM, listItem.getCompNum());
                    allCommentsFragment.setArguments(mBundle);
                    Utils.changeFragment(fragmentManager, allCommentsFragment);
                }
            });


            if (listItem.getCurrentStatus() == 5 || listItem.getCurrentStatus() == 6
                    || listItem.getCurrentStatus() == 9) {
            if((listItem.getOptions().equals("")) && (listItem.getImages().equals(""))) {
                viewHolder.feedback_buttonview.setVisibility(View.VISIBLE);
                viewHolder.feedback_buttonview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ComplaintFeedbackFragment feedbackFragment = new ComplaintFeedbackFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.USER_DATA, (Serializable) feedbackOptionModelList);
                        bundle.putString(Constants.KEY_COMPLAINT_NUM, listItem.getCompNum());
                        feedbackFragment.setArguments(bundle);
                        Utils.changeFragment(fragmentManager, feedbackFragment);
                    }
                });
            }
            else
                {
                    viewHolder.feedback_buttonview.setVisibility(View.GONE);
                    viewHolder.layout_feedback.setVisibility(View.VISIBLE);
                   /* viewHolder.feedback_icon.setController(Utils.simpleDraweeController(
                            Constants.IMAGE_URL +listItem.getImages(),
                            100, 100));*/

                   Glide.with(mContext).load(Constants.IMAGE_URL +listItem.getImages()).into(viewHolder.feedback_icon);
                    viewHolder.feedback_status.setText(listItem.getOptions());
                }
            }
            else
                {
                    viewHolder.feedback_buttonview.setVisibility(View.GONE);
                    viewHolder.feedback_buttonview.setVisibility(View.GONE);
                }
        }

        viewHolder.feedback_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           String mes = listItem.getFe_remarks();
           if(mes.equals("")) {
           }
           else {
               buildAlertMessageNoGps(mContext,mes);
           }
            }
        });

        for (int j = 0; j<statusModelList.size();j++) {
             statusTitle = statusModelList.get(j).getStatusName();
             statusColorCode = statusModelList.get(j).getStatusColorCode();
            if (statusTitle.equals("Closed")) {
                viewHolder.count_status_text_view.setVisibility(View.GONE);
                //   totalCount_text_view.setVisibility(View.GONE);
                viewHolder.comp_status_text_view.setVisibility(View.VISIBLE);
                viewHolder.vote_buttonview.setVisibility(View.GONE);
                viewHolder.comp_status_text_view.setText(statusTitle);
                viewHolder.comp_status_text_view.setBackgroundColor(Color.parseColor(statusColorCode));
            }
            else {
                if (listItem.isUpVotesDone()) {
                    viewHolder.vote_buttonview.setVisibility(View.GONE);
                    viewHolder.count_status_text_view.setVisibility(View.VISIBLE);
                    viewHolder.comp_status_text_view.setVisibility(View.GONE);
                    //  totalCount_text_view.setVisibility(View.GONE);
                    viewHolder.count_status_text_view.setText("Vote Count: " + listItem.getUpVotesCount().toString());
                } else {
                    viewHolder.vote_buttonview.setVisibility(View.VISIBLE);
                    viewHolder.count_status_text_view.setVisibility(View.GONE);
                    viewHolder.comp_status_text_view.setVisibility(View.GONE);

                }
            }
        }

        viewHolder.vote_buttonview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getupVotesComplaints(listItem.getCompNum(),viewHolder);
            }
        });

        String module_id = Constants.getPrefrence(mContext,"complaint_module_id");
        if (module_id.equals("  68")){
            viewHolder.com_reopen_text_view.setVisibility(View.GONE);
            viewHolder.vote_buttonview.setVisibility(View.GONE);
            viewHolder.count_status_text_view.setVisibility(View.GONE);
            viewHolder.feedback_buttonview.setVisibility(View.GONE);
        }

        return convertView;
    }

    private class ViewHolder {
        TextView serialNumTV;
//        SimpleDraweeView compSDV;
        ImageView compSDV;
        TextView compNumTV,count_status_text_view;
        TextView compDateTV;
        TextView compStatusTV;
        TextView compTypeTV;
        TextView compAddTV;
        TextView feedback_status,comp_status_text_view;
        ImageView feedback_icon;
        ButtonView com_reopen_text_view,vote_buttonview,vote_count_buttonview;
        ImageView commentsIV;
        ImageView historyIV;
        ButtonView feedback_buttonview;
        RelativeLayout parentLayout;
        LinearLayout layout_feedback;
    }

    public static void buildAlertMessageNoGps(final Context context,String mes) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(mes)
                .setTitle("Feedback Remark")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }



    /**
     * Get custom filter
     *
     * @return filter
     */
    @NonNull
    @Override
    public Filter getFilter() {
        if (customFilter == null) {
            customFilter = new CustomFilter();
        }

        return customFilter;
    }

    /**
     * Custom filter for mainDataList
     * Filter content in mainDataList according to the search text
     */
    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                List<AllComplaintsModel> tempList = new ArrayList<>();

                // search content in friend mainDataList
                for (AllComplaintsModel user : mainDataList) {
                    if ((user.getCompNum() + " " +
                            user.getCompType() + " " +
                            user.getCompAdd() + " " +
                            user.getCompGeoAdd() + " " +
                            Utils.formatServerToUserDate(user.getCompDate()) + " " +
                            Utils.formatTime24To12(user.getCompTime())).toLowerCase()
                            .contains(constraint.toString().toLowerCase())) {
                        tempList.add(user);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = mainDataList.size();
                filterResults.values = mainDataList;
            }

            return filterResults;
        }

        /**
         * Notify about mainDataList to ui
         *
         * @param constraint text
         * @param results    filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (List<AllComplaintsModel>) results.values;
            notifyDataSetChanged();
        }
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }



    private void getupVotesComplaints(String complaintNum, final ViewHolder viewHolder) {
      final SpotsDialog spotsDialog = new SpotsDialog(mContext, mContext.getResources().getString(R.string.please_wait_dialog_text), R.style.CustomSpotsDialogStyle);
        spotsDialog.setCancelable(false);
        spotsDialog.show();
        String user_id = "";
      /*  if (Constants.KEY_MODULE_NAME.equals(strmodelId)) {
            user_id = allComplaintsModel.getComplaintNo();
        } else {*/

            user_id = complaintNum;
      //  }

        ApiInterface mApiInterface = Utils.getInterfaceService();
        int userId = Utils.getUserDetails(mContext).getCivilianId();
        Log.v(TAG, Constants.HEADER_TOKEN_BEARER + Utils.getUserToken(mContext));
        Call<JsonObject> mService = mApiInterface.getupVotesComplaints(Constants.HEADER_TOKEN_BEARER
                        + Utils.getUserToken(mContext),
                Constants.HEADER_ACCEPT, complaintNum, Utils.getUserDetails(mContext).getCivilianId());
        mService.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {

                        if (response.body() != null) {
                            JsonObject jsonObject = response.body();
                            if (jsonObject.get("response").getAsBoolean()) {
                                spotsDialog.dismiss();
                                Constants.customToast(mContext, jsonObject.get("message").getAsString(), 1);

                                Constants.setPreferenceStringData(mContext,"countStatus", "true");

                                ViewMyComplaintsFragment complaintFragment = new ViewMyComplaintsFragment();
                                Utils.changeFragment(fragmentManager,complaintFragment);
                                viewHolder.vote_buttonview.setEnabled(false);

                            } else {
                               // refreshCategoryList = true;
                                spotsDialog.dismiss();
                                Utils.showSnackBar(mContext, viewHolder.parentLayout,
                                        jsonObject.get("message").getAsString().trim(), false);
                            }
                        } else {
                        //    refreshCategoryList = true;
                            spotsDialog.dismiss();
                            Utils.showSnackBar(mContext, viewHolder.parentLayout,
                                    Constants.MESSAGE_NO_DATA_FOUND, false);
                        }

                } catch (Exception e) {
                  //  refreshCategoryList = true;
                    spotsDialog.dismiss();
                    e.printStackTrace();
                    Utils.customToast(mContext, Constants.MESSAGE_SOMETHING_WENT_WRONG, 0);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                    spotsDialog.dismiss();
                   // refreshCategoryList = true;
                    call.cancel();
                    t.printStackTrace();
                    Utils.showSnackBar(mContext, viewHolder.parentLayout, Utils.failureMessage(t), false);

            }
        });
    }
}