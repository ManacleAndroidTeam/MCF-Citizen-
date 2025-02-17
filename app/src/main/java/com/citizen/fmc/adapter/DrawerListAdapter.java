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
import com.citizen.fmc.model.ImageTextModel;
import com.citizen.fmc.utils.Constants;

import java.util.List;


/**
 * ======> Created by dheeraj-gangwar on 2018-01-09 <======
 */

public class DrawerListAdapter extends ArrayAdapter<ImageTextModel> {
    private Activity activity;
    private List<ImageTextModel> modelList;
    private String checkStatus;

    public DrawerListAdapter(Activity activity, List<ImageTextModel> modelList , String checkStatus) {
        super(activity, 0);
        this.activity = activity;
        this.modelList = modelList;
        this.checkStatus = checkStatus;
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Nullable
    @Override
    public ImageTextModel getItem(int position) {
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
        View mView = convertView;
        final ViewHolder viewHolder;
        if (convertView == null) {
            mView = LayoutInflater.from(activity).inflate(R.layout.layout_drawer_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.iv_services = mView.findViewById(R.id.iv_services);
            viewHolder.tv_services = mView.findViewById(R.id.tv_services);
            mView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) mView.getTag();
        }

        ImageTextModel model = getItem(position);
        if (model != null) {

            viewHolder.iv_services.setImageResource(model.getId());
            if (model.getTitle().equalsIgnoreCase(Constants.MODULE_TITLE_Home))
            {
                viewHolder.tv_services.setText(R.string.Home);
            }
            if (model.getTitle().equalsIgnoreCase(Constants.MODULE_TITLE_Profile))
            {
                viewHolder.tv_services.setText(R.string.Profile);
            }
            if (model.getTitle().equalsIgnoreCase(Constants.MODULE_TITLE_Frequently_Asked_Ques))
            {
                viewHolder.tv_services.setText(R.string.FrequentlyAskedQues);
            }
            if (model.getTitle().equalsIgnoreCase(Constants.MODULE_TITLE_Feedback))
            {
                viewHolder.tv_services.setText(R.string.Feedback);
            }
            if (model.getTitle().equalsIgnoreCase(Constants.MODULE_TITLE_Rate_Us))
            {
                viewHolder.tv_services.setText(R.string.RateUs);
            }
            if (model.getTitle().equalsIgnoreCase(Constants.MODULE_TITLE_Logout))
            {
                viewHolder.tv_services.setText(R.string.Logout);
            }
            if (model.getTitle().equalsIgnoreCase(Constants.MODULE_TITLE_Change_Language))
            {
                viewHolder.tv_services.setText(R.string.ChangeLanguage);
            }


            String id = String.valueOf(model.getId());
            String title = String.valueOf(model.getTitle());
            Log.d("m_id",id + "" + title);
            Log.d("title",title);
        }

        //Check Status and change Text Color:-
        if(checkStatus.equalsIgnoreCase(Constants.SIDE_NAV)){
            viewHolder.tv_services.setTextColor(activity.getResources().getColor(R.color.colorDarkBlue));
        }else {
            viewHolder.tv_services.setTextColor(activity.getResources().getColor(R.color.colorWhite));
        }
        return mView;
    }

    private class ViewHolder {
        ImageView iv_services;
        TextView tv_services;
    }
}