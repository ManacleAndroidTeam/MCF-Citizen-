package com.citizen.fmc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.model.WhoServeYouModel;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * ======> Created by dheeraj-gangwar on 2018-04-24 <======
 */

@SuppressLint("SetTextI18n")
public class WhoServeYouAdapter extends ArrayAdapter<WhoServeYouModel> implements Filterable {
    private Context mContext;
    private List<WhoServeYouModel> mainDataList;
    private List<WhoServeYouModel> filteredList;
    private WhoServeYouAdapter.CustomFilter customFilter;
    private String TAG = getClass().getSimpleName();
    private List<WhoServeYouModel> tempList;

    public WhoServeYouAdapter(@NonNull Context context,
                              @NonNull List<WhoServeYouModel> objects) {
        super(context, 0, objects);
        this.mContext = context;
        this.mainDataList = objects;
        this.filteredList = new ArrayList<>();
        this.filteredList.addAll(mainDataList);
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
    public WhoServeYouModel getItem(int position) {
        return filteredList.get(position);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        WhoServeYouAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_who_serve_you_module_list_item, parent, false);
            viewHolder = new WhoServeYouAdapter.ViewHolder();
            viewHolder.moduleSDV = convertView.findViewById(R.id.module_sdv);
            viewHolder.titleTextView = convertView.findViewById(R.id.module_title_text_view);
            viewHolder.designationTextView = convertView.findViewById(R.id.designation_text_view);
            viewHolder.emailTextView = convertView.findViewById(R.id.email_text_view);
            viewHolder.mobileNumTextView = convertView.findViewById(R.id.mobile_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (WhoServeYouAdapter.ViewHolder) convertView.getTag();
        }

        WhoServeYouModel listItem = getItem(position);
        if (listItem != null) {
            viewHolder.moduleSDV.setController(Utils.simpleDraweeController(
                    Constants.IMAGE_PUBLIC_URL + listItem.getImagePath(),
                    70, 70));
            viewHolder.titleTextView.setText("@" + listItem.getFirstName() + " " + listItem.getLastName() + " ");
            viewHolder.designationTextView.setText(Html.fromHtml("<strong>Designation: </strong>" + listItem.getRoleName()));
            viewHolder.emailTextView.setText(Html.fromHtml("<strong>E-mail: </strong>" + listItem.getEmail()));
            viewHolder.mobileNumTextView.setText(Html.fromHtml("<strong>Contact: </strong>" + listItem.getContactNumber()));
        }

        return convertView;
    }

    private class ViewHolder {
        SimpleDraweeView moduleSDV;
        TextView titleTextView;
        TextView designationTextView;
        TextView emailTextView;
        TextView mobileNumTextView;
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
            customFilter = new WhoServeYouAdapter.CustomFilter();
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
                tempList = new ArrayList<>();

                // search content in friend mainDataList
                for (WhoServeYouModel user : mainDataList) {
                    if ((user.getFirstName() + " " +
                            user.getLastName() + " " +
                            user.getContactNumber() + " " +
                            user.getRoleName() + " " +
                            user.getEmail()).toLowerCase()
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
            filteredList = (List<WhoServeYouModel>) results.values;
            notifyDataSetChanged();
        }
    }

    public List<WhoServeYouModel> getSearchList() {
        return tempList;
    }
}