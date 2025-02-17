package com.citizen.fmc.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.model.ImageTextModel;

import java.util.List;

/**
 * ======> Created by dheeraj-gangwar on 2018-01-11 <======
 */

public class ImageTextListAdapter extends ArrayAdapter<ImageTextModel> {
    private Activity activity;
    private List<ImageTextModel> list;

    public ImageTextListAdapter(@NonNull Activity activity, @NonNull List<ImageTextModel> objects) {
        super(activity, 0, objects);
        this.activity = activity;
        this.list = objects;
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
        View mView = convertView;
        final ViewHolder viewHolder;
        if (convertView == null) {
            mView = LayoutInflater.from(activity).inflate(R.layout.layout_image_text_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = mView.findViewById(R.id.icon_text_view);
            viewHolder.textView = mView.findViewById(R.id.title_text_view);
            mView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) mView.getTag();
        }

        if (position == 4) {
            viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_call_24dp, 0);
        }

        ImageTextModel imageTextModel = list.get(position);
        viewHolder.textView.setText(imageTextModel.getTitle());

        return mView;
    }

    private class ViewHolder {
        TextView imageView;
        TextView textView;
    }
}