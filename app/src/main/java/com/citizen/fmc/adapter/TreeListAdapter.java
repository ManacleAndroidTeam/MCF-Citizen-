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
import com.citizen.fmc.model.TreeInNdmcTextModel;
import com.citizen.fmc.utils.Constants;

import java.util.List;

public class TreeListAdapter extends BaseAdapter {

    private Context context;
    private List<TreeInNdmcTextModel> optionModalList;
    private TreeListAdapter.ViewHolder holder;

    public TreeListAdapter(Context context, List<TreeInNdmcTextModel> optionModalList) {
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
        holder = new TreeListAdapter.ViewHolder();
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.option_list , parent , false);
            holder.iv_icon_image = convertView.findViewById(R.id.iv_icon_image);
            holder.tv_list_text = convertView.findViewById(R.id.tv_list_text);
            holder.iv_right_icon = convertView.findViewById(R.id.iv_right_icon);

            convertView.setTag(holder);
        }else {
            holder = (TreeListAdapter.ViewHolder) convertView.getTag();
        }

        //Setting Image Icon , Text Title and Right Icon:-
        holder.iv_icon_image.setImageResource(optionModalList.get(position).getImage());
        int id = optionModalList.get(position).getId();
        if (id== Constants.tree_near_by_me)
        {
            holder.tv_list_text.setText(R.string.tree_near_by_me);
        }
        else if (id== Constants.qr_near_bt_me)
        {
            holder.tv_list_text.setText(R.string.qr_near_by_me);
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

