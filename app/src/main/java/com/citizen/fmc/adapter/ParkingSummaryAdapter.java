package com.citizen.fmc.adapter;

/**
 * Created by manacle-pc on 28/1/18.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.activity.GroupModel;
import com.citizen.fmc.model.ParkingSummeryResponse.Datum;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("SetTextI18n")
public class ParkingSummaryAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Datum> childDataList;
    private ArrayList<GroupModel> groupNameList;
    private String position;
    // private List<ParkingSummeryResponse> parkinglist;

    public ParkingSummaryAdapter(Context context, List<Datum> childDataList, ArrayList<GroupModel> groupNameList) {
        this.context = context;
        this.childDataList = childDataList;
        this.groupNameList = groupNameList;
       // this.childDataList = new ArrayList<>();
       // this.childDataList.addAll(childDataList);
    }

    @Override
    public Datum getChild(int groupPosition, int childPosition) {
        return childDataList.get(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View view, ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_child_list_item, parent, false);
        }

        TextView iconTextView = view.findViewById(R.id.icon_text_view);
        TextView name = view.findViewById(R.id.child_text_view);
        TextView bike_in = view.findViewById(R.id.bike_in);
        TextView bike_out = view.findViewById(R.id.bike_out);
        TextView bike_coll = view.findViewById(R.id.bike_collection);
        TextView car_coll = view.findViewById(R.id.car_collection);
        TextView bike_present = view.findViewById(R.id.bike_present);
        TextView car_in = view.findViewById(R.id.car_in);
        TextView car_out = view.findViewById(R.id.car_out);
        TextView car_present = view.findViewById(R.id.car_present);
        TextView total = view.findViewById(R.id.total);
        if(groupNameList.get(groupPosition).getParking_id().equalsIgnoreCase(childDataList.get(childPosition).getParkingId()))
        {
            for(int i = 0;i<childDataList.size();i++){
                //String park_id = groupNameList.get(i).getParking_id();
                if(position.equalsIgnoreCase(getChild(groupPosition, childPosition).getParkingId())){
                    name.setText("Parking id"+childDataList.get(i).getParkingId().trim());
                    bike_in.setText("Bike In"+childDataList.get(i).getBikeIn().trim());
                    bike_out.setText("Bike Out"+childDataList.get(i).getBikeOut().trim());
                    bike_coll.setText("Bike Collection"+childDataList.get(i).getBikeCollection().trim());
                    bike_present.setText("Bike Present"+childDataList.get(i).getBikePresent().trim());
                    car_in.setText("Car In"+childDataList.get(i).getCarIn().trim());
                    car_out.setText("Car Out"+childDataList.get(i).getCarOut().trim());
                    car_coll.setText("Car Collection"+childDataList.get(i).getCarCollection().trim());
                    car_present.setText("Car Present"+childDataList.get(i).getCarPresent().trim());
                    total.setText("Total"+childDataList.get(i).getTotal().trim());

                }
            }
            /*name.setText("Parking id"+getChild(groupPosition, childPosition).getParkingId().trim());
            bike_in.setText("Bike In"+getChild(groupPosition, childPosition).getBikeIn().trim());
            bike_out.setText("Bike Out"+getChild(groupPosition, childPosition).getBikeOut().trim());
            bike_coll.setText("Bike Collection"+getChild(groupPosition, childPosition).getBikeCollection().trim());
            bike_present.setText("Bike Present"+getChild(groupPosition, childPosition).getBikePresent().trim());
            car_in.setText("Car In"+getChild(groupPosition, childPosition).getCarIn().trim());
            car_out.setText("Car Out"+getChild(groupPosition, childPosition).getCarOut().trim());
            car_coll.setText("Car Collection"+getChild(groupPosition, childPosition).getCarCollection().trim());
            car_present.setText("Car Present"+getChild(groupPosition, childPosition).getCarPresent().trim());
            total.setText("Total"+getChild(groupPosition, childPosition).getTotal().trim());
*/
        }
       // iconTextView.setText(Constants.setFontAwesome(context, iconTextView, getChild(groupPosition, childPosition).getBikeIn()));
        //name.setText(getChild(groupPosition, childPosition).getParkingId().trim());
             //  name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_right_24dp, 0);

        return view;
    }

   /* @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<ComplaintType> complainList = childDataList.get(groupPosition).getComplaintType();
        return complainList.size();

    }*/

    @Override
    public GroupModel getGroup(int groupPosition) {
        return groupNameList.get(groupPosition);
    }


    @Override
    public int getGroupCount() {
        return groupNameList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return childDataList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View view,
                             ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_group_list_item, parent, false);
        }

        TextView iconTextView = view.findViewById(R.id.icon_text_view);
        TextView name = view.findViewById(R.id.group_text_view);
      //  iconTextView.setText(Constants.setFontAwesome(context, iconTextView, getGroup(groupPosition).getIcon_unicode()));
        name.setText(groupNameList.get(groupPosition).getGroupName());
        name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_chevron_right_24dp, 0);


        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 position = groupNameList.get(groupPosition).getParking_id();
            }
        });



        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}