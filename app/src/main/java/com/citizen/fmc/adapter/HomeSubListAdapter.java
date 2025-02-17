package com.citizen.fmc.adapter;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.citizen.fmc.R;
import com.citizen.fmc.model.DrawerListItemModel;
import com.citizen.fmc.utils.Constants;

import java.util.List;

/**
 * ======> Created by dheeraj-gangwar on 2018-04-18 <======
 */

public class HomeSubListAdapter extends ArrayAdapter<DrawerListItemModel> {
    private Activity activity;
    private List<DrawerListItemModel> modelList;

    public HomeSubListAdapter(Activity activity, List<DrawerListItemModel> modelList) {
        super(activity, 0);
        this.activity = activity;
        this.modelList = modelList;
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Nullable
    @Override
    public DrawerListItemModel getItem(int position) {
        return modelList.get(position);
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
            convertView = LayoutInflater.from(activity).inflate(R.layout.layout_image_text_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.helpLineImageView = convertView.findViewById(R.id.list_image_view);
            viewHolder.titleTextView = convertView.findViewById(R.id.title_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DrawerListItemModel model = getItem(position);
        if (model != null) {
            /*viewHolder.helpLineImageView.setController(Utils.simpleDraweeController(
                    Constants.IMAGE_PUBLIC_URL + model.getModuleIconUniCode(),
                    80, 80));*/
            if (model.getModuleIconUniCode()!=null &&!(model.getModuleIconUniCode().isEmpty()))
            {
                Glide.with(getContext())
                        .load(Constants.IMAGE_PUBLIC_URL+model.getModuleIconUniCode())
                        .placeholder(R.drawable.ic_loading_small_dots)
                        .error(R.drawable.place_holder_no_image)
                        .into(viewHolder.helpLineImageView);
            }
            String sub_module_title = model.getModuleTitleName().trim();
            viewHolder.titleTextView.setText(sub_module_title);
           /* int sub_module_id = model.getModuleId();
            if (sub_module_id==Constants.SUBMODULE_ID_ABOUT_NDMC)
            {
                viewHolder.titleTextView.setText(R.string.about_ndmc);
            }
            else if (sub_module_id==Constants.SUBMODULE_ID_DIRECTORY)
            {
                viewHolder.titleTextView.setText(R.string.directory);
            }
            else if (sub_module_id==Constants.SUBMODULE_ID_NDMC_HISTORY)
            {
                viewHolder.titleTextView.setText(R.string.ndmc_history);
            }
            else if (sub_module_id==Constants.SUBMODULE_ID_WEBSITE)
            {
                viewHolder.titleTextView.setText(R.string.website);
            }
            else if (sub_module_id==Constants.SUBMODULE_ID_WHAT_IN_THE_NEWS_TODAY)
            {
                viewHolder.titleTextView.setText(R.string.what_in_the_news_today);
            }
            else if (sub_module_id==Constants.SUBMODULE_ID_FACEBOOK)
            {
                viewHolder.titleTextView.setText(R.string.facebook);
            }
            else if (sub_module_id==Constants.SUBMODULE_ID_NDMC_TOLL_FREE_NUMBER)
            {
                viewHolder.titleTextView.setText(R.string.ndmc_toll_free_number);
            }
            else if (sub_module_id==Constants.SUBMODULE_ID_TWITTER)
            {
                viewHolder.titleTextView.setText(R.string.twitter);
            }
            else if (sub_module_id==Constants.SUBMODULE_ID_WRITE_FEEDBACK)
            {
                viewHolder.titleTextView.setText(R.string.write_feedback);
            }
            else if (sub_module_id==Constants.SUBMODULE_ID_Water_bill)
            {
                viewHolder.titleTextView.setText(R.string.water_bill);
            }
            else if (sub_module_id==Constants.SUBMODULE_ID_Electricity_Bill)
            {
                viewHolder.titleTextView.setText(R.string.electricity_bill);
            }
            else if (sub_module_id==Constants.SUBMODULE_ID_Estate_Bill)
            {
                viewHolder.titleTextView.setText(R.string.estate_bill);
            }
            else if (sub_module_id==Constants.SUBMODULE_ID_Property_tax_bill)
            {
                viewHolder.titleTextView.setText(R.string.property_tax_bill);
            }
            else if (sub_module_id==Constants.SUBMODULE_ID_NDMC_Smart_parking)
            {
                viewHolder.titleTextView.setText(R.string.ndmc_smart_parking);
            }
            else if (sub_module_id==Constants.SUBMODULE_ID_Smart_parking_summary)
            {
                viewHolder.titleTextView.setText(R.string.smart_parking_summary);
            }
            else if (sub_module_id==Constants.SUBMODULE_ID_Traffic)
            {
                viewHolder.titleTextView.setText(R.string.traffic);
            }

            *//*TODO: All Citizen Services Sub Modules*//*
            else if (sub_module_id==Constants.Citizen_Services_sub_id_baratghar)
            {
                viewHolder.titleTextView.setText(R.string.baratghar);
            }

            else if (sub_module_id==Constants.Citizen_Services_sub_id_birth_certificate)
            {
                viewHolder.titleTextView.setText(R.string.birth_certificate);
            }


            else if (sub_module_id==Constants.Citizen_Services_sub_id_building_plan_approval)
            {
                viewHolder.titleTextView.setText(R.string.building_approval);
            }


            else if (sub_module_id==Constants.Citizen_Services_sub_id_child_name_inclusion)
            {
                viewHolder.titleTextView.setText(R.string.child_name_inclusion);
            }


            else if (sub_module_id==Constants.Citizen_Services_sub_id_death_certificate)
            {
                viewHolder.titleTextView.setText(R.string.death_certificate);
            }


            else if (sub_module_id==Constants.Citizen_Services_sub_id_online_health_license)
            {
                viewHolder.titleTextView.setText(R.string.online_health_license);
            }


            else if (sub_module_id==Constants.Citizen_Services_sub_id_still_birth_certificate)
            {
                viewHolder.titleTextView.setText(R.string.still_birth_certificate);
            }


            else if (sub_module_id==Constants.Citizen_Services_sub_id_yellow_fever_vaccination)
            {
                viewHolder.titleTextView.setText(R.string.yellow_fever_vaccination);
            }

            *//*TODO: Employee Corner Sub Modules*//*
            else if (sub_module_id==Constants.Employee_Corner_sub_id_attendance_dashboard)
            {
                viewHolder.titleTextView.setText(R.string.employee_corner_attendance_dashboard);
            }
            else if (sub_module_id==Constants.Employee_Corner_sub_id_employee_corner)
            {
                viewHolder.titleTextView.setText(R.string.employee_corner);
            }

            *//*TODO: Important Information Sub Modules*//*
            else if (sub_module_id==Constants.Important_Information_sub_id_budget_2018_19)
            {
                viewHolder.titleTextView.setText(R.string.budget_2018_19);
            }

            else if (sub_module_id==Constants.Important_Information_sub_id_clean_ndmc)
            {
                viewHolder.titleTextView.setText(R.string.clean_ndmc);
            }

            else if (sub_module_id==Constants.Important_Information_sub_id_cyber_security)
            {
                viewHolder.titleTextView.setText(R.string.cyber_security);
            }

            else if (sub_module_id==Constants.Important_Information_sub_id_e_courts)
            {
                viewHolder.titleTextView.setText(R.string.e_courts);
            }

            else if (sub_module_id==Constants.Important_Information_sub_id_faqs)
            {
                viewHolder.titleTextView.setText(R.string.faqs);
            }

            else if (sub_module_id==Constants.Important_Information_sub_id_fight_dengue_chikungunya)
            {
                viewHolder.titleTextView.setText(R.string.fight_dengue_chikungunya);
            }

            else if (sub_module_id==Constants.Important_Information_sub_id_ndmc_walk)
            {
                viewHolder.titleTextView.setText(R.string.ndmc_walk);
            }

            else if (sub_module_id==Constants.Important_Information_sub_id_pm_relief_fund)
            {
                viewHolder.titleTextView.setText(R.string.pm_relief_fund);
            }

            else if (sub_module_id==Constants.Important_Information_sub_id_rain_water_harvesting)
            {
                viewHolder.titleTextView.setText(R.string.rain_water_harvesting);
            }

            else if (sub_module_id==Constants.Important_Information_sub_id_view_medical_stock)
            {
                viewHolder.titleTextView.setText(R.string.view_medical_stock);
            }

            else if (sub_module_id==Constants.Important_Information_sub_id_virtual_tour_smart_toilets)
            {
                viewHolder.titleTextView.setText(R.string.virtual_tour_smart_toilets);
            }

            *//*TODO: What's Near Me Sub Modules*//*
            else if (sub_module_id==Constants.Whats_Near_me_id_ATMs)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_id_atms);
            }

            else if (sub_module_id==Constants.Whats_Near_me_id_Baratghars)
            {
                viewHolder.titleTextView.setText(R.string.what_near_me_baratghars);
            }

            else if (sub_module_id==Constants.Whats_Near_me_id_Bus_stands_dtc)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_bus_stands_dtc);
            }

            else if (sub_module_id==Constants.Whats_Near_me_id_Business_directory)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_business_directory);
            }

            else if (sub_module_id==Constants.Whats_Near_me_id_citizen_facilitation_centers_ndmc)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_citizen_faciliation_centers_ndmc);
            }

            else if (sub_module_id==Constants.Whats_Near_me_id_clinics_dispensaries)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_clinic_dispensaries);
            }

            else if (sub_module_id==Constants.Whats_Near_me_id_community_centers)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_community_centers);
            }

            else if (sub_module_id==Constants.Whats_Near_me_id_construction_and_demolition_waste_malba_bins)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_construction_and_demolition_waste_bins);
            }

            else if (sub_module_id==Constants.Whats_Near_me_id_Emabssies_high_commissions)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_emabssies_high_commissions);
            }

            else if (sub_module_id==Constants.Whats_Near_me_id_hospitals)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_hospitals);
            }


            else if (sub_module_id==Constants.Whats_Near_me_id_Litter_Bins)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_litter_bins);
            }


            else if (sub_module_id==Constants.Whats_Near_me_id_Markets)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_markets);
            }


            else if (sub_module_id==Constants.Whats_Near_me_id_Metro_Stations)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_metro_stations);
            }


            else if (sub_module_id==Constants.Whats_Near_me_id_Monuments)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_monuments);
            }

            else if (sub_module_id==Constants.Whats_Near_me_id_NDMC_Gyms)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_ndmc_gyms);
            }


            else if (sub_module_id==Constants.Whats_Near_me_id_ndmc_open_gyms_in_parks)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_ndmc_open_gyms_in_park);
            }


            else if (sub_module_id==Constants.Whats_Near_me_id_ndmc_swimming)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_ndmc_swimming);
            }


            else if (sub_module_id==Constants.Whats_Near_me_id_pharmacies)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_pharmacies);
            }


            else if (sub_module_id==Constants.Whats_Near_me_id_physiotherapy_centers)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_physiotherapy_centers);
            }


            else if (sub_module_id==Constants.Whats_Near_me_id_police_stations)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_police_stations);
            }


            else if (sub_module_id==Constants.Whats_Near_me_id_postal_codes)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_postal_codes);
            }


            else if (sub_module_id==Constants.Whats_Near_me_id_public_toilets)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_public_toilets);
            }


            else if (sub_module_id==Constants.Whats_Near_me_id_schools)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_schools);
            }


            else if (sub_module_id==Constants.Whats_Near_me_id_speed_limits)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_speed_limits);
            }

            else if (sub_module_id==Constants.Whats_Near_me_id_stadiums)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_stadiums);
            }

            else if (sub_module_id==Constants.Whats_Near_me_id_trolley_data)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_trolley_data);
            }

            else if (sub_module_id==Constants.Whats_Near_me_id_twin_bins)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_twin_bins);
            }

            else if (sub_module_id==Constants.Whats_Near_me_id_veterinary_clients)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_veterinary_clients);
            }

            else if (sub_module_id==Constants.Whats_Near_me_id_water_atms)
            {
                viewHolder.titleTextView.setText(R.string.whats_near_me_water_atms);
            }

            else if(sub_module_id==Constants.TREE_NEAR_BY_ME)
            {
                viewHolder.titleTextView.setText(R.string.tree_near_by_me);
            }
            else if(sub_module_id==Constants.QR_NEAR_BY_ME)
            {
                viewHolder.titleTextView.setText(R.string.qr_near_by_me);
            }







            else
            {
                viewHolder.titleTextView.setText(model.getModuleTitleName().trim());
            }
            Log.d("submodule_title",sub_module_id + " " + sub_module_title);*/
//            viewHolder.titleTextView.setText(model.getModuleTitleName().trim());
        }

        return convertView;
    }

    private class ViewHolder {
//        SimpleDraweeView helpLineImageView;
        ImageView helpLineImageView;
        TextView titleTextView;
    }
}