package com.citizen.fmc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.model.FeedbackOptionModel;

import java.util.ArrayList;

public class FeedbackComplainAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<FeedbackOptionModel> optionModalList;
    private FeedbackComplainAdapter.ViewHolder holder;

    public FeedbackComplainAdapter(Context context, ArrayList<FeedbackOptionModel> optionModalList) {
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
        holder = new FeedbackComplainAdapter.ViewHolder();
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.feedback_list , parent , false);
            holder.iv_icon_image = convertView.findViewById(R.id.iv_icon_image);
            holder.tv_list_text = convertView.findViewById(R.id.tv_list_text);
            holder.iv_right_icon = convertView.findViewById(R.id.iv_right_icon);

            convertView.setTag(holder);
        }else {
            holder = (FeedbackComplainAdapter.ViewHolder) convertView.getTag();
        }

        //Setting Image Icon , Text Title and Right Icon:-
    //    holder.iv_icon_image.setImageResource(optionModalList.get(position).getImage());
        holder.tv_list_text.setText(optionModalList.get(position).getOptions());
       // holder.iv_right_icon.setImageResource(optionModalList.get(position).getRightIconImage());
        return convertView;
    }

    private class ViewHolder{
        private ImageView iv_icon_image;
        private TextView tv_list_text;
        private TextView iv_right_icon;
    }
}
