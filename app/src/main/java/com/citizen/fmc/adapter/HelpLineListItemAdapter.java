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

import com.citizen.fmc.R;
import com.citizen.fmc.model.HelpLineModel;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * ======> Created by dheeraj-gangwar on 2018-01-08 <======
 */

public class HelpLineListItemAdapter extends ArrayAdapter<HelpLineModel> {
    private Activity activity;
    private List<HelpLineModel> list;

    public HelpLineListItemAdapter(@NonNull Activity activity, @NonNull List<HelpLineModel> objects) {
        super(activity, 0, objects);
        this.activity = activity;
        this.list = objects;
    }

    @Nullable
    @Override
    public HelpLineModel getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
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
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.layout_help_line_list_item, parent, false);
            viewHolder.helpLineImageView = convertView.findViewById(R.id.list_image_view);
            viewHolder.helpLineTitleTextView = convertView.findViewById(R.id.title_text_view);
            viewHolder.helpLineAddressTextView = convertView.findViewById(R.id.address_text_view);
            viewHolder.helpLineDialerImageView = convertView.findViewById(R.id.list_dialer_image_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        HelpLineModel helpLineModel = getItem(position);
        if (helpLineModel != null) {
            viewHolder.helpLineImageView.setController(Utils.simpleDraweeController(
                    Constants.IMAGE_PUBLIC_URL + helpLineModel.getImagePath(),
                    90, 90));
            int id = Integer.parseInt(helpLineModel.getId());
            String title = helpLineModel.getContactName();
            String addrees = helpLineModel.getContactAddress();
            Log.d("helpline", id + " " + title + " " + addrees);
          /*  if(id==Constants.helpline_sub_id_AIIMS_Hospital)
            {
                viewHolder.helpLineTitleTextView.setText(R.string.aiims_hospital_central_enquiry);
            }
            else if(id==Constants.helpline_sub_id_Ambulance_Service)
            {
                viewHolder.helpLineTitleTextView.setText(R.string.ambulance_service_control_room);
            }

            else if(id==Constants.helpline_sub_id_CPH_Hospital)
            {
                viewHolder.helpLineTitleTextView.setText(R.string.cph_hospital);
            }

            else if(id==Constants.helpline_sub_id_Delhi_Police_control_room)
            {
                viewHolder.helpLineTitleTextView.setText(R.string.delhi_police_control_room);
            }

            else if(id==Constants.helpline_sub_id_delhi_police_senior_citizen)
            {
                viewHolder.helpLineTitleTextView.setText(R.string.delhi_police_senior_citizen);
            }

            else if(id==Constants.helpline_sub_id_delhi_police_traffic_unit)
            {
                viewHolder.helpLineTitleTextView.setText(R.string.delhi_police_traffic_unit);
            }

            else if(id==Constants.helpline_sub_id_delhi_police_women_in_distress)
            {
                viewHolder.helpLineTitleTextView.setText(R.string.delhi_police_women_in_distress);
            }

            else if(id==Constants.helpline_sub_id_disaster_helpline)
            {
                viewHolder.helpLineTitleTextView.setText(R.string.disaster_helpline);
            }
            else if(id==Constants.helpline_sub_id_fire_service)
            {
                viewHolder.helpLineTitleTextView.setText(R.string.fire_service);
            }
            else if(id==Constants.helpline_sub_id_LHMC_KSC_HOSPITAL)
            {
                viewHolder.helpLineTitleTextView.setText(R.string.lhmc_ksc_hospital);
            }
            else if(id==Constants.helpline_sub_id_LHMC_SSK_HOSPITAL)
            {
                viewHolder.helpLineTitleTextView.setText(R.string.lhmc_sks_hospital);
            }
            else if(id==Constants.helpline_sub_id_ndmc_control_room)
            {
                viewHolder.helpLineTitleTextView.setText(R.string.ndmc_control_room);
            }
            else if(id==Constants.helpline_sub_id_RML_HOSPITAL)
            {
                viewHolder.helpLineTitleTextView.setText(R.string.rml_hospital);
            }
            else
            {*/
                viewHolder.helpLineTitleTextView.setText(helpLineModel.getContactName().trim());

//            }

            String contactAddress = helpLineModel.getContactAddress();
            if (contactAddress != null && !contactAddress.isEmpty()) {
                viewHolder.helpLineAddressTextView.setVisibility(View.VISIBLE);
                /*if (id==Constants.helpline_sub_id_AIIMS_Hospital)
                {
                    viewHolder.helpLineAddressTextView.setText(R.string.aiims_hospital_address);
                }
                else if(id==Constants.helpline_sub_id_Ambulance_Service)
                {
                    viewHolder.helpLineAddressTextView.setText(R.string.ambulance_service_control_room_address);
                }
                else if(id==Constants.helpline_sub_id_CPH_Hospital)
                {
                    viewHolder.helpLineAddressTextView.setText(R.string.cph_hospital_address);
                }
                else if(id==Constants.helpline_sub_id_disaster_helpline)
                {
                    viewHolder.helpLineAddressTextView.setText(R.string.disaster_helpline_address);
                }

                else if(id==Constants.helpline_sub_id_LHMC_KSC_HOSPITAL)
                {
                    viewHolder.helpLineAddressTextView.setText(R.string.lhmc_ksc_hospital_address);
                }

                else if(id==Constants.helpline_sub_id_LHMC_SSK_HOSPITAL)
                {
                    viewHolder.helpLineAddressTextView.setText(R.string.lhmc_sks_hospital_address);
                }

                else if(id==Constants.helpline_sub_id_ndmc_control_room)
                {
                    viewHolder.helpLineAddressTextView.setText(R.string.ndmc_control_room_address);
                }
                else if(id==Constants.helpline_sub_id_RML_HOSPITAL)
                {
                    viewHolder.helpLineAddressTextView.setText(R.string.rml_hospital_address);
                }
                else
                {*/
                    viewHolder.helpLineAddressTextView.setText(contactAddress.trim());
//                }

            } else {
                viewHolder.helpLineAddressTextView.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    private class ViewHolder {
        SimpleDraweeView helpLineImageView;
        TextView helpLineTitleTextView;
        TextView helpLineAddressTextView;
        ImageView helpLineDialerImageView;
    }
}