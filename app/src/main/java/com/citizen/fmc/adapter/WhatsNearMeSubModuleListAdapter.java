package com.citizen.fmc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.model.WhatsNearMeModel;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * ======> Created by dheeraj-gangwar on 2018-04-21 <======
 */

@SuppressLint("SetTextI18n")
public class WhatsNearMeSubModuleListAdapter extends ArrayAdapter<WhatsNearMeModel> implements Filterable {
    private Context mContext;
    private FragmentManager fragmentManager;
    private List<WhatsNearMeModel> mainDataList;
    private List<WhatsNearMeModel> filteredList;
    private CustomFilter customFilter;
    private String TAG = getClass().getSimpleName();
    private List<WhatsNearMeModel> tempList;
    private double apiResponseLatitude = 0.0;
    private double apiResponselongitude = 0.0;
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private DecimalFormat newFormat = new DecimalFormat("####.##");

    public WhatsNearMeSubModuleListAdapter(@NonNull Context context,
                                           FragmentManager fragmentManager,
                                           @NonNull List<WhatsNearMeModel> objects,
                                           double currentLatitude, double currentLongitude) {
        super(context, 0, objects);
        this.mContext = context;
        this.fragmentManager = fragmentManager;
        this.mainDataList = objects;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
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
    public WhatsNearMeModel getItem(int position) {
        return filteredList.get(position);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_whats_near_me_sub_module_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.parentLayout = convertView.findViewById(R.id.parent_layout);
            viewHolder.moduleSDV = convertView.findViewById(R.id.module_sdv);
            viewHolder.titleTextView = convertView.findViewById(R.id.module_title_text_view);
            viewHolder.addressTextView = convertView.findViewById(R.id.address_text_view);
            viewHolder.distanceTextView = convertView.findViewById(R.id.distance_text_view);
            viewHolder.openMapImageView = convertView.findViewById(R.id.direction_image_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        WhatsNearMeModel listItem = getItem(position);
        if (listItem != null) {
            viewHolder.moduleSDV.setController(Utils.simpleDraweeController(
                    Constants.IMAGE_PUBLIC_URL + listItem.getImagePath(),
                    90, 90));
            viewHolder.titleTextView.setText(listItem.getUnitNumber().trim() /*+ " (Circle No." + listItem.getCircleId() + ")"*/);
            viewHolder.addressTextView.setText(listItem.getAddress());

            apiResponseLatitude = Double.parseDouble(listItem.getLatitude() != null
                    && !listItem.getLatitude().isEmpty() ? listItem.getLatitude() : "0.0");
            apiResponselongitude = Double.parseDouble(listItem.getLongitude() != null
                    && !listItem.getLongitude().isEmpty() ? listItem.getLongitude() : "0.0");

            //Calculating the distance between Current Location from the API Fetch Location:-
            if (currentLatitude == 0.0 || currentLongitude == 0.0) {
                viewHolder.distanceTextView.setText("NA");
            } else {
                viewHolder.distanceTextView.setText(Html.fromHtml("<strong>Distance : </strong>" +
                        String.valueOf(newFormat.format(Utils.calculationByDistance(currentLatitude,
                                currentLongitude, apiResponseLatitude, apiResponselongitude))) + " km"));
            }
        }

        viewHolder.openMapImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + apiResponseLatitude
                        + ", " + apiResponselongitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mContext.startActivity(mapIntent);
            }
        });

        return convertView;
    }

    private class ViewHolder {
        SimpleDraweeView moduleSDV;
        RelativeLayout parentLayout;
        TextView titleTextView;
        TextView addressTextView;
        TextView distanceTextView;
        ImageView openMapImageView;
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
                tempList = new ArrayList<>();

                // search content in friend mainDataList
                for (WhatsNearMeModel user : mainDataList) {
                    if ((user.getAddress().toLowerCase() + " " +
                            user.getUnitNumber()).toLowerCase()
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
            filteredList = (List<WhatsNearMeModel>) results.values;
            notifyDataSetChanged();
        }
    }

    public List<WhatsNearMeModel> getSearchList() {
        return tempList;
    }
}