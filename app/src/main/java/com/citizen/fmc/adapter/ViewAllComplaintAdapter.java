package com.citizen.fmc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.citizen.fmc.R;
import com.citizen.fmc.fragment.ComplaintCommentsFragment;
import com.citizen.fmc.fragment.ComplaintHistoryFragment;
import com.citizen.fmc.fragment.NewComplaintFragment;
import com.citizen.fmc.fragment.ViewAllComplaintsFragment;
import com.citizen.fmc.model.ViewAllComplaintStatusModel;
import com.citizen.fmc.model.ViewAllComplaintsModel;
import com.citizen.fmc.utils.ApiInterface;
import com.citizen.fmc.utils.ButtonView;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllComplaintAdapter extends RecyclerView.Adapter
        <ViewAllComplaintAdapter.ViewHolder> implements
        Filterable {

    private Context mContext;
    private FragmentManager fragmentManager;
    private List<ViewAllComplaintsModel> mainDataList;
    private List<ViewAllComplaintStatusModel> statusModelList;
    private List<ViewAllComplaintsModel> filteredList;
    private List<ViewAllComplaintsModel> tempList;
    private CustomFilter customFilter;
    String statusTitle = "";
    String statusColorCode= "";
    private String TAG = getClass().getSimpleName();

    public ViewAllComplaintAdapter(@NonNull Context context,
                                   FragmentManager fragmentManager,
                                   @NonNull List<ViewAllComplaintsModel> objects,
                                   List<ViewAllComplaintStatusModel> statusModelList) {

        this.mContext = context;
        this.fragmentManager = fragmentManager;
        this.mainDataList = objects;
        this.filteredList = new ArrayList<>();
        this.statusModelList = statusModelList;
        this.filteredList.addAll(mainDataList);
    }

    @NonNull
    @Override
    public ViewAllComplaintAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_all_complaints_adapter, viewGroup, false);
        return new ViewAllComplaintAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewAllComplaintAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        final ViewAllComplaintsModel listItem = filteredList.get(position);
        try {

            if (listItem != null) {
                viewHolder.serialNumTV.setText(String.valueOf(position + 1) + ".");
                viewHolder.compNumTV.setText(mContext.getResources().getString(R.string.comp_num) + listItem.getComplaintNo());
                viewHolder.compDateTV.setText(Html.fromHtml("<strong>" + mContext.getResources().getString(R.string.comp_date_time) + "</strong> "
                        + Utils.formatServerDateAndTimeToUser(listItem.getDate() + " " + listItem.getTime())));
                viewHolder.compTypeTV.setText(Html.fromHtml("<strong>" + mContext.getResources().getString(R.string.comp_category) + "</strong> " + listItem.getComplaintType()));
                viewHolder.compAddTV.setText(Html.fromHtml("<strong>" + mContext.getResources().getString(R.string.comp_address) + "</strong> " + listItem.getGeoAddress()));
               /* viewHolder.compSDV.setController(Utils.simpleDraweeController(
                        Constants.IMAGE_URL + listItem.getComplaintImage(),
                        100, 100));*/
                              /* viewHolder.gridImageView.setController(Utils.simpleDraweeController(Constants.IMAGE_PUBLIC_URL + itemModel.getModuleIconUniCode(),
                        90, 90));*/
                    if (listItem.getComplaintImage()!=null && (!listItem.getComplaintImage().isEmpty()))
                    {
                        Glide.with(mContext)
                                .load(Constants.IMAGE_PUBLIC_URL+listItem.getComplaintImage())
                                .placeholder(R.drawable.ic_loading_small_dots)
                                .error(R.drawable.place_holder_no_image)
                                .into(viewHolder.compSDV);
                    }
                Log.v(TAG, Constants.IMAGE_URL + listItem.getComplaintImage());

                for (int i = 0; i < statusModelList.size(); i++) {
                    if (listItem.getCurrentStatus() == statusModelList.get(i).getId()) {
                        viewHolder.compStatusTV
                                .setBackgroundColor(Color.parseColor(statusModelList.get(i).getColorCode().toUpperCase().trim()));
                        viewHolder.compStatusTV.setText(statusModelList.get(i).getName());
                        break;
                    }
                }

                viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ViewAllComplaintsModel _item = filteredList.get(position);
                        NewComplaintFragment complaintFragment = new NewComplaintFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.KEY_COMPLAINT_PAGE_TYPE, Constants.VALUE_SHOW_COMPLAINT);
                        bundle.putSerializable(Constants.KEY_COMPLAINT_DATA, _item);
                        for (int i = 0; i < statusModelList.size(); i++) {
                            if (_item.getCurrentStatus() == statusModelList.get(i).getId()) {
                                bundle.putString(Constants.KEY_STATUS_TITLE, statusModelList.get(i).getName().trim());
                                bundle.putString(Constants.KEY_STATUS_COLOR_CODE, statusModelList.get(i).getColorCode().toUpperCase().trim());
                                bundle.putString(Constants.KEY_MODULE_ID, Constants.KEY_MODULE_NAME);
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
                        if (listItem.getDurationDays() > 0) {
                            mBundle.putLong(Constants.KEY_COMPLAINT_DAYS, listItem.getDurationDays());
                        } else if (listItem.getDurationHours() > 0) {
                            mBundle.putLong(Constants.KEY_COMPLAINT_HOURS, listItem.getDurationHours());
                        } else {
                            mBundle.putLong(Constants.KEY_COMPLAINT_MINUTES, listItem.getDurationMinutes());
                        }
                        mBundle.putString(Constants.KEY_COMPLAINT_NUM, listItem.getComplaintNo());
                        complaintCommentsFragment.setArguments(mBundle);
                        Utils.changeFragment(fragmentManager, complaintCommentsFragment);
                    }
                });

                viewHolder.historyIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ComplaintHistoryFragment allCommentsFragment = new ComplaintHistoryFragment();
                        Bundle mBundle = new Bundle();
                        if (listItem.getDurationDays() > 0) {
                            mBundle.putLong(Constants.KEY_COMPLAINT_DAYS, listItem.getDurationDays());
                        } else if (listItem.getDurationHours() > 0) {
                            mBundle.putLong(Constants.KEY_COMPLAINT_HOURS, listItem.getDurationHours());
                        } else {
                            mBundle.putLong(Constants.KEY_COMPLAINT_MINUTES, listItem.getDurationMinutes());
                        }
                        mBundle.putString(Constants.KEY_COMPLAINT_NUM, listItem.getComplaintNo());
                        allCommentsFragment.setArguments(mBundle);
                        Utils.changeFragment(fragmentManager, allCommentsFragment);
                    }
                });
                viewHolder.com_reopen_text_view.setVisibility(View.GONE);

//                if (listItem.getCurrentStatus() == 5 || listItem.getCurrentStatus() == 6
//                        || listItem.getCurrentStatus() == 9) {
//                    viewHolder.com_reopen_text_view.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            ReopenComplaint reopenComplaint = new ReopenComplaint();
//                            Bundle bundle = new Bundle();
//                            bundle.putString(Constants.KEY_COMPLAINT_NUM, listItem.getComplaintNo());
//                            reopenComplaint.setArguments(bundle);
//                            Utils.changeFragment(fragmentManager, reopenComplaint);
//                        }
//                    });
//                } else {
//                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        for (int j = 0; j<statusModelList.size();j++) {
            statusTitle = statusModelList.get(j).getName();
            statusColorCode = statusModelList.get(j).getColorCode();
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
                getupVotesComplaints(listItem.getComplaintNo(),viewHolder);
            }
        });
        String module_id = Constants.getPrefrence(mContext,"complaint_module_id");
        if (module_id.equals("68")){
            viewHolder.vote_buttonview.setVisibility(View.GONE);
            viewHolder.count_status_text_view.setVisibility(View.GONE);
            viewHolder.com_reopen_text_view.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        if (customFilter == null) {
            customFilter = new CustomFilter();
        }

        return customFilter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView serialNumTV;
//        SimpleDraweeView compSDV;
        ImageView compSDV;
        TextView compNumTV;
        TextView compDateTV;
        TextView compStatusTV;
        TextView compTypeTV,comp_status_text_view;
        TextView compAddTV,vote_buttonview,count_status_text_view;
        ButtonView com_reopen_text_view;
        ImageView commentsIV;
        ImageView historyIV;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View convertView) {
            super(convertView);

            parentLayout = convertView.findViewById(R.id.parent_layout);
            serialNumTV = convertView.findViewById(R.id.serial_num_text_view);
            compSDV = convertView.findViewById(R.id.complaint_image_view);
            compNumTV = convertView.findViewById(R.id.complaint_num);
            compDateTV = convertView.findViewById(R.id.complaint_date_time);
            compTypeTV = convertView.findViewById(R.id.complaint_type);
            compAddTV = convertView.findViewById(R.id.complaint_address);
            compStatusTV = convertView.findViewById(R.id.complaint_status);
            commentsIV = convertView.findViewById(R.id.comments_image_view);
            historyIV = convertView.findViewById(R.id.history_image_view);
            vote_buttonview = convertView.findViewById(R.id.vote_buttonview);
            count_status_text_view = convertView.findViewById(R.id.count_status_text_view);
            comp_status_text_view = convertView.findViewById(R.id.comp_status_text_view);
            com_reopen_text_view = convertView.findViewById(R.id.com_reopen_text_view);
        }
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
                tempList = new ArrayList<>();

                // search content in friend mainDataList
                for (ViewAllComplaintsModel user : mainDataList) {
                    if ((user.getComplaintNo() + " " +
                            user.getComplaintType() + " " +
                            user.getGeoAddress() + " " +
                            Utils.formatServerToUserDate(user.getDate()) + " " +
                            Utils.formatTime24To12(user.getTime())).toLowerCase()
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
            filteredList = (List<ViewAllComplaintsModel>) results.values;
            notifyDataSetChanged();
        }
    }

    //NotifyItemInserted:-
    public void add(ViewAllComplaintsModel mc) {
        filteredList.add(mc);
        notifyItemInserted(filteredList.size());

    }

    //Add All Data:-
    public void addAll(List<ViewAllComplaintsModel> mcList) {
        for (ViewAllComplaintsModel mc : mcList) {
            add(mc);
        }

    }

    public List<ViewAllComplaintsModel> getSearchList() {
        return tempList;
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

                            ViewAllComplaintsFragment complaintFragment = new ViewAllComplaintsFragment();
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
