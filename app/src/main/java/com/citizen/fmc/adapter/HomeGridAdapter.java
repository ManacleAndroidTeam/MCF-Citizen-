package com.citizen.fmc.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.citizen.fmc.R;
import com.citizen.fmc.model.DrawerListItemModel;
import com.citizen.fmc.utils.Constants;

import java.util.List;

/**
 * ======> Created by dheeraj-gangwar on 2018-01-30 <======
 */

public class HomeGridAdapter extends ArrayAdapter<DrawerListItemModel> {
    private Activity activity;
    private List<DrawerListItemModel> drawerModuleList;
    private String TAG = getClass().getSimpleName();
    String surveyor_status = "";

    public HomeGridAdapter(Activity activity, List<DrawerListItemModel> drawerModuleList) {
        super(activity, 0);
        this.activity = activity;
        this.drawerModuleList = drawerModuleList;
    }

    @Nullable
    @Override
    public DrawerListItemModel getItem(int position) {
        return drawerModuleList.get(position);
    }

    @Override
    public int getCount() {
        return drawerModuleList.size();
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.new_layout_home_grid_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.gridImageView = convertView.findViewById(R.id.grid_item_image);
            viewHolder.gridTextView = convertView.findViewById(R.id.grid_item_text);
            viewHolder.module_rl = convertView.findViewById(R.id.module_rl);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            DrawerListItemModel itemModel = getItem(position);
            if (itemModel != null) {
                surveyor_status = Constants.getPrefrence(activity,"surveyor_status");
                Log.v(TAG, "Image Path: " + Constants.IMAGE_PUBLIC_URL + itemModel.getModuleIconUniCode());
               /* viewHolder.gridImageView.setController(Utils.simpleDraweeController(Constants.IMAGE_PUBLIC_URL + itemModel.getModuleIconUniCode(),
                        90, 90));*/
                if (itemModel.getModuleIconUniCode()!=null && (!itemModel.getModuleIconUniCode().isEmpty()))
                {
                    Glide.with(getContext())
                            .load(Constants.IMAGE_PUBLIC_URL+itemModel.getModuleIconUniCode())
                            .placeholder(R.drawable.ic_loading_small_dots)
                            .error(R.drawable.place_holder_no_image)
                            .into(viewHolder.gridImageView);
                }
                viewHolder.gridTextView.setText(itemModel.getModuleTitleName().trim());
                String module_title = itemModel.getModuleTitleName().trim();
                int module_id = itemModel.getModuleId();
                String sub_module_title= itemModel.getSubItemModelList().toString();
                String language_code =  Constants.getPrefrence(getContext(),"locale");
                if (drawerModuleList.size() <= 9){
                    if (position <= 2) {
                        viewHolder.module_rl.setBackgroundColor(getContext().getResources().getColor(R.color.saffron));
                    } else if (position <= 5) {
                        viewHolder.gridTextView.setTextColor(getContext().getResources().getColor(R.color.navy_blue));
                        viewHolder.gridImageView.setColorFilter(getContext().getResources().getColor(R.color.navy_blue));
                        viewHolder.module_rl.setBackgroundColor(getContext().getResources().getColor(R.color.colorWhite));
                    } else {
                        viewHolder.module_rl.setBackgroundColor(getContext().getResources().getColor(R.color.green));
                    }
                } else {
                    if (position <= 5) {
                        viewHolder.module_rl.setBackgroundColor(getContext().getResources().getColor(R.color.saffron));
                    } else if (position <= 11) {
                        viewHolder.gridTextView.setTextColor(getContext().getResources().getColor(R.color.navy_blue));
                        viewHolder.gridImageView.setColorFilter(getContext().getResources().getColor(R.color.navy_blue));
                        viewHolder.module_rl.setBackgroundColor(getContext().getResources().getColor(R.color.colorWhite));
                    } else {
                        viewHolder.module_rl.setBackgroundColor(getContext().getResources().getColor(R.color.green));
                    }
                }

              /*  if(module_id==Constants.MODULE_ID_NDMC);
                {
                    viewHolder.gridTextView.setText(R.string.Ndmc);
                }
                if(module_id==Constants.MODULE_ID_CONNECT_WITH_NDMC)
                {
                    viewHolder.gridTextView.setText(R.string.Connect_with_ndmc);
                }
                else if (module_id==Constants.MODULE_ID_SMART_METER)
                {
                    viewHolder.gridTextView.setText(R.string.smart_meter);
                }
                else if(module_id==Constants.MODULE_ID_QUICK_PAY)
                {
                    viewHolder.gridTextView.setText(R.string.quick_pay);
                }
                else if(module_id==Constants.MODULE_ID_COMPLAINTS)
                {
                    viewHolder.gridTextView.setText(R.string.complaints);
                }
                else if(module_id==Constants.MODULE_ID_HELPLINE)
                {
                    viewHolder.gridTextView.setText(R.string.helpline);
                }
                else if(module_id==Constants.MODULE_ID_STARTUPS)
                {
                    viewHolder.gridTextView.setText(R.string.startups);
                }
                else if(module_id==Constants.MODULE_ID_SWIMMING_POOL)
                {
                    viewHolder.gridTextView.setText(R.string.swimming_pool);
                }
                else if(module_id==Constants.MODULE_ID_WHO_SERVE_YOU)
                {
                    viewHolder.gridTextView.setText(R.string.who_serves_you);
                }
                else if(module_id==Constants.MODULE_ID_WHATS_NEAR_ME)
                {
                    viewHolder.gridTextView.setText(R.string.what_near_me);
                }
                else if(module_id==Constants.MODULE_ID_E_HOSPITAL)
                {
                    viewHolder.gridTextView.setText(R.string.e_hospital);
                }
                else if(module_id==Constants.MODULE_ID_PENSIONER_PORTAL)
                {
                    viewHolder.gridTextView.setText(R.string.pensioner_portal);
                }
                else if(module_id==Constants.MODULE_ID_E_WASTE)
                {
                    viewHolder.gridTextView.setText(R.string.e_waste);
                }
                else if(module_id==Constants.MODULE_ID_IMPORTANT_INFORMATION)
                {
                    viewHolder.gridTextView.setText(R.string.important_information);
                }
                else if(module_id==Constants.MODULE_ID_MONITOR_WATER_QUALITY)
                {
                    viewHolder.gridTextView.setText(R.string.monitor_water_quality);
                }
                else if(module_id==Constants.MODULE_ID_DAILY_TREATED_WATER_TEST)
                {
                    viewHolder.gridTextView.setText(R.string.daily_treated_water_test);
                }
                else if(module_id==Constants.MODULE_ID_OPD_REGISTRATION)
                {
                    viewHolder.gridTextView.setText(R.string.opd_registration);
                }
                else if(module_id==Constants.MODULE_ID_SMART_BIKE)
                {
                    viewHolder.gridTextView.setText(R.string.smart_bike);
                }
                else if(module_id==Constants.MODULE_ID_PTU_DASHBOARD)
                {
                    viewHolder.gridTextView.setText(R.string.ptu_dashboard);
                }
                else if(module_id==Constants.MODULE_ID_GARBAGE_VEHICLE_TRACKING)
                {
                    viewHolder.gridTextView.setText(R.string.garbage_vehicle_tracking);
                }
               else if(module_id==Constants.MODULE_ID_EMPLOYEE_CORNER)
                {
                    viewHolder.gridTextView.setText(R.string.employee_corner);
                }
                else if(module_id==Constants.MODULE_ID_SPORTS_COACHING)
                {
                    viewHolder.gridTextView.setText(R.string.sports_coaching);
                }
                else if(module_id==Constants.MODULE_ID_ALL_CITIZEN_SERVICES)
                {
                    viewHolder.gridTextView.setText(R.string.all_citizen_services);
                }
                else if(module_id==Constants.MODULE_ID_TRAFFIC_AND_PARKING) {
                    viewHolder.gridTextView.setText(R.string.traffic_and_parking);
                }

                else
                    {
                        viewHolder.gridTextView.setText(itemModel.getModuleTitleName().trim());

                    }*/
                if(module_id==Constants.MODULE_ID_NDMC)
                {
                    viewHolder.gridTextView.setText(R.string.Ndmc);
                }
                else if(module_id==Constants.MODULE_ID_CONNECT_WITH_NDMC)
                {
                    viewHolder.gridTextView.setText(R.string.Connect_with_ndmc);
                }
                else if (module_id==Constants.MODULE_ID_SMART_METER)
                {
                    viewHolder.gridTextView.setText(R.string.smart_meter);
                }
                else if(module_id==Constants.MODULE_ID_QUICK_PAY)
                {
                    viewHolder.gridTextView.setText(R.string.quick_pay);
                }
                else if(module_id==Constants.MODULE_ID_COMPLAINTS)
                {
                    viewHolder.gridTextView.setText(R.string.complaints);
                }
               else if(module_id==Constants.MODULE_ID_HELPLINE)
                {
                    viewHolder.gridTextView.setText(R.string.helpline);
                }
                else if(module_id==Constants.MODULE_ID_STARTUPS)
                {
                    viewHolder.gridTextView.setText(R.string.startups);
                }
                else if(module_id==Constants.MODULE_ID_SWIMMING_POOL)
                {
                    viewHolder.gridTextView.setText(R.string.swimming_pool);
                }
                else if(module_id==Constants.MODULE_ID_WHO_SERVE_YOU)
                {
                    viewHolder.gridTextView.setText(R.string.who_serves_you);
                }
                else if(module_id==Constants.MODULE_ID_WHATS_NEAR_ME)
                {
                    viewHolder.gridTextView.setText(R.string.what_near_me);
                }
               else if(module_id==Constants.MODULE_ID_E_HOSPITAL)
                {
                    viewHolder.gridTextView.setText(R.string.e_hospital);
                }
                else if(module_id==Constants.MODULE_ID_PENSIONER_PORTAL)
                {
                    viewHolder.gridTextView.setText(R.string.pensioner_portal);
                }
                else if(module_id==Constants.MODULE_ID_E_WASTE)
                {
                    viewHolder.gridTextView.setText(R.string.e_waste);
                }
                else if(module_id==Constants.MODULE_ID_IMPORTANT_INFORMATION)
                {
                    viewHolder.gridTextView.setText(R.string.important_information);
                }
                else if(module_id==Constants.MODULE_ID_MONITOR_WATER_QUALITY)
                {
                    viewHolder.gridTextView.setText(R.string.monitor_water_quality);
                }
                else if(module_id==Constants.MODULE_ID_DAILY_TREATED_WATER_TEST)
                {
                    viewHolder.gridTextView.setText(R.string.daily_treated_water_test);
                }
                else if(module_id==Constants.MODULE_ID_OPD_REGISTRATION)
                {
                    viewHolder.gridTextView.setText(R.string.opd_registration);
                }
                else if(module_id==Constants.MODULE_ID_SMART_BIKE)
                {
                    viewHolder.gridTextView.setText(R.string.smart_bike);
                }
               else if(module_id==Constants.MODULE_ID_PTU_DASHBOARD)
                {
                    viewHolder.gridTextView.setText(R.string.ptu_dashboard);
                }
                else if(module_id==Constants.MODULE_ID_GARBAGE_VEHICLE_TRACKING)
                {
                    viewHolder.gridTextView.setText(R.string.garbage_vehicle_tracking);
                }
                else if(module_id==Constants.MODULE_ID_EMPLOYEE_CORNER)
                {
                    viewHolder.gridTextView.setText(R.string.employee_corner);
                }
                else if(module_id==Constants.MODULE_ID_SPORTS_COACHING)
                {
                    viewHolder.gridTextView.setText(R.string.sports_coaching);
                }
                else if(module_id==Constants.MODULE_ID_ALL_CITIZEN_SERVICES)
                {
                    viewHolder.gridTextView.setText(R.string.all_citizen_services);
                }
                else if(module_id==Constants.MODULE_ID_TRAFFIC_AND_PARKING) {
                    viewHolder.gridTextView.setText(R.string.traffic_and_parking);
                }
                else if(module_id==Constants.MODULE_ID_DOOR_TO_DOOR_COLLECTION) {
                    viewHolder.gridTextView.setText(R.string.door_to_door_collection);
                }
                else if (module_id==Constants.MODULE_ID_TREE_NEAR_BY_ME)
                {
                    viewHolder.gridTextView.setText(R.string.tree_in_ndmc);
                }
                else if (module_id == Constants.MODULE_ID_AIRPOLLUTION){
                    viewHolder.gridTextView.setText(R.string.air_pollution);
                }
                else
                {
                    if (!TextUtils.isEmpty(language_code) && language_code.equalsIgnoreCase("en"))
                    {
                        viewHolder.gridTextView.setText(itemModel.getModuleTitleName());
                    }
                    else
                    {
                        String titleHindi = itemModel.getTitleHindi();
                        if (!TextUtils.isEmpty(titleHindi))
                        {
                            viewHolder.gridTextView.setText(itemModel.getTitleHindi());
                        }
                        else
                        {
                            viewHolder.gridTextView.setText(itemModel.getModuleTitleName());

                        }

                    }

                }
//                viewHolder.gridTextView.setText(itemModel.getModuleTitleName().trim());
                Log.d("module_title",module_id + " " + module_title);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }


    private class ViewHolder {
//        SimpleDraweeView gridImageView;
        ImageView gridImageView;
        TextView gridTextView;
        RelativeLayout module_rl;
    }
}