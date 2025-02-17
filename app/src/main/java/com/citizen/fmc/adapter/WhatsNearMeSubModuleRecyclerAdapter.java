package com.citizen.fmc.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class WhatsNearMeSubModuleRecyclerAdapter extends RecyclerView.Adapter<WhatsNearMeSubModuleRecyclerAdapter.ViewHolder> implements Filterable {

    //Defining Variables to use:-
    private Context context;
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
    private boolean isLoadingAdded = false;
    private int SUB_MODULE_ID;

    public WhatsNearMeSubModuleRecyclerAdapter(@NonNull Context context,
                                               FragmentManager fragmentManager,
                                               @NonNull List<WhatsNearMeModel> objects,
                                               double currentLatitude, double currentLongitude,int SUB_MODULE_ID) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.mainDataList = objects;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.filteredList = new ArrayList<>();
        this.filteredList.addAll(mainDataList);
        this.SUB_MODULE_ID=SUB_MODULE_ID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.layout_whats_near_me_sub_module_list_item , parent , false);
            return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            //Binding All Data to RecyclerView List :-
        try {
            final WhatsNearMeModel listItem = filteredList.get(position);
            if (listItem != null) {

                //Displaying Image Using SimpleDrawee from Fresco:-
                holder.moduleSDV.setController(Utils.simpleDraweeController(
                        Constants.IMAGE_PUBLIC_URL + listItem.getImagePath(),
                        90, 90));

                //Displaying Unit Number:-
                holder.titleTextView.setText(listItem.getUnitNumber().trim());

                //Displaying Address:-
                holder.addressTextView.setText(listItem.getAddress());

                //API Response Latitude:-
                apiResponseLatitude = Double.parseDouble(listItem.getLatitude() != null
                        && !listItem.getLatitude().isEmpty() ? listItem.getLatitude() : "0.0");

                //API Response Longitude:-
                apiResponselongitude = Double.parseDouble(listItem.getLongitude() != null
                        && !listItem.getLongitude().isEmpty() ? listItem.getLongitude() : "0.0");

                //Calculating the distance between Current Location from the API Fetch Location:-
                if ((SUB_MODULE_ID==90)||(SUB_MODULE_ID==89)){
                    holder.distanceTextView.setVisibility(View.GONE);
                    holder.openMapImageView.setVisibility(View.GONE);
                }
                if (currentLatitude == 0.0 || currentLongitude == 0.0) {
                    holder.distanceTextView.setText("NA");
                } else {
                    holder.distanceTextView.setText(Html.fromHtml("<strong>Distance : </strong>" +
                            String.valueOf(newFormat.format(Utils.calculationByDistance(currentLatitude,
                                    currentLongitude, apiResponseLatitude, apiResponselongitude))) + " km"));
                }
            }

            //Navigate Current Latitude , Longitude to API Response Latitude , Longitude on Google Map Navigation View:-
            holder.openMapImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + apiResponseLatitude
                            + ", " + apiResponselongitude);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    context.startActivity(mapIntent);
                }
            });
            if (SUB_MODULE_ID==90){
                holder.calling_image_view.setVisibility(View.VISIBLE);
            }
            else {
                holder.calling_image_view.setVisibility(View.GONE);
            }
            if (filteredList.get(position).getMobile_no().isEmpty()||filteredList.get(position).getMobile_no().equals("") ){
                holder.calling_image_view.setVisibility(View.GONE);
            }
            holder.calling_image_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(Intent.ACTION_DIAL)
                            .setData(Uri.parse("tel:" + filteredList.get(position).getMobile_no())));
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return filteredList == null ? 0 : filteredList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //Defining All Views to be used:-
        private SimpleDraweeView moduleSDV;
        private RelativeLayout parentLayout;
        private TextView titleTextView;
        private TextView addressTextView;
        private TextView distanceTextView;
        private ImageView openMapImageView,calling_image_view;

        public ViewHolder(View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            moduleSDV = itemView.findViewById(R.id.module_sdv);
            titleTextView = itemView.findViewById(R.id.module_title_text_view);
            addressTextView = itemView.findViewById(R.id.address_text_view);
            distanceTextView = itemView.findViewById(R.id.distance_text_view);
            openMapImageView = itemView.findViewById(R.id.direction_image_view);
            calling_image_view=itemView.findViewById(R.id.calling_image_view);

        }
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


    //NotifyItemInserted:-
    public void add(WhatsNearMeModel mc) {
        filteredList.add(mc);
        notifyItemInserted(filteredList.size() - 1);
    }

    //Add All Data:-
    public void addAll(List<WhatsNearMeModel> mcList) {
        for (WhatsNearMeModel mc : mcList) {
            add(mc);
        }
    }

    private WhatsNearMeModel getItem(int position) {
        return filteredList.get(position);
    }
}
