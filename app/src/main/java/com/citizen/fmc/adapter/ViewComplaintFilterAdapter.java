package com.citizen.fmc.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;
import com.citizen.fmc.model.ViewAllComplaintStatusModel;

import java.util.List;

public class ViewComplaintFilterAdapter extends ArrayAdapter<ViewAllComplaintStatusModel> implements Filterable {
    private Context mContext;
    private List<ViewAllComplaintStatusModel> mainDataList;
    private String TAG = getClass().getSimpleName();

    public ViewComplaintFilterAdapter(@NonNull Context context,
                                      @NonNull List<ViewAllComplaintStatusModel> objects) {
        super(context, 0, objects);
        this.mContext = context;
        this.mainDataList = objects;
    }

    @Override
    public int getCount() {
        return mainDataList.size();
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Nullable
    @Override
    public ViewAllComplaintStatusModel getItem(int position) {
        return mainDataList.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (getCount() > 0) {
            return getCount();
        } else {
            return super.getViewTypeCount();
        }
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View mView = convertView;
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            mView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
            viewHolder.titleTV = mView.findViewById(android.R.id.text1);
            mView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) mView.getTag();
        }

        ViewAllComplaintStatusModel listItem = mainDataList.get(position);
        viewHolder.titleTV.setText(listItem.getName());

        return mView;
    }

    private class ViewHolder {
        TextView titleTV;
    }
}