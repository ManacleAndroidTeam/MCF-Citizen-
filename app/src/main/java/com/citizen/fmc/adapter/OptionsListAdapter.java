package com.citizen.fmc.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.model.ImageTextModel;
import com.citizen.fmc.utils.Constants;

import java.util.List;

public class OptionsListAdapter extends BaseAdapter {

    private Context context;
    private List<ImageTextModel> optionModalList;
    private ViewHolder holder;

    public OptionsListAdapter(Context context, List<ImageTextModel> optionModalList) {
        this.context = context;
        this.optionModalList = optionModalList;
    }

    @Override
    public int getCount() {
        return optionModalList == null ? 0 : optionModalList.size();
    }

    @Override
    public Object getItem(int position) {
        return optionModalList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        holder = new ViewHolder();
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.option_list , parent , false);
            holder.iv_icon_image = convertView.findViewById(R.id.iv_icon_image);
            holder.tv_list_text = convertView.findViewById(R.id.tv_list_text);
            holder.iv_right_icon = convertView.findViewById(R.id.iv_right_icon);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Setting Image Icon , Text Title and Right Icon:-
        holder.iv_icon_image.setImageResource(optionModalList.get(position).getImage());
        int id = optionModalList.get(position).getId();
        if (id== Constants.Complaint_submodule_id_create_new_complaint)
        {
            holder.tv_list_text.setText(R.string.create_new_complaint);
        }
        else if (id== Constants.Complaint_submodule_id_my_complaints)
        {
            holder.tv_list_text.setText(R.string.my_complaints);
        }
        else if (id== Constants.Complaint_submodule_id_view_all_complaints)
        {
            holder.tv_list_text.setText(R.string.view_all_complaints);
        }
        else if (id== Constants.Complaint_submodule_id_write_feedback)
        {
            holder.tv_list_text.setText(R.string.write_feedback);
        }
        else if (id== Constants.Complaint_submodule_id_Ndmc_toll_free_number)
        {
            holder.tv_list_text.setText(R.string.ndmc_toll_free_number1);
        }
        else
        {
            holder.tv_list_text.setText(optionModalList.get(position).getTitle());
        }
        String title = optionModalList.get(position).getTitle();
        Log.d("complaint", id + " " + title);

        holder.iv_right_icon.setImageResource(optionModalList.get(position).getRightIconImage());
        return convertView;
    }

    private class ViewHolder{
        private ImageView iv_icon_image;
        private TextView tv_list_text;
        private ImageView iv_right_icon;
    }
}
