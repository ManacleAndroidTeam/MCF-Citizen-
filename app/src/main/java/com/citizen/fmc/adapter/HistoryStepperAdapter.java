package com.citizen.fmc.adapter;

import android.graphics.Color;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.model.HistoryModel;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * ======> Created by dheeraj-gangwar on 2018-04-25 <======
 */

public class HistoryStepperAdapter extends ArrayAdapter<HistoryModel> {
    private FragmentActivity activity;
    private ArrayList<HistoryModel> historyList;

    public HistoryStepperAdapter(@NonNull FragmentActivity context, @NonNull ArrayList<HistoryModel> objects) {
        super(context, 0, objects);
        activity = context;
        historyList = objects;
    }

    @Override
    public int getCount() {
        return historyList.size();
    }

    @Nullable
    @Override
    public HistoryModel getItem(int position) {
        return historyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.layout_child_vertical_step, null);
            holder.parentItem = convertView.findViewById(R.id.parent_layout);
            holder.circleView = convertView.findViewById(R.id.circle_view);
            holder.lineView = convertView.findViewById(R.id.line_view);
            holder.userDraweeView = convertView.findViewById(R.id.module_sdv);
            holder.userDraweeView = convertView.findViewById(R.id.module_sdv);
            holder.statusTitleTextView = convertView.findViewById(R.id.status_title_text_view);
            holder.officerNameTitleTextView = convertView.findViewById(R.id.officer_name_title_text_view);
            holder.updatedAtTitleTextView = convertView.findViewById(R.id.updated_at_title_text_view);
            holder.notesTitleTextView = convertView.findViewById(R.id.notes_title_text_view);
            holder.statusTextView = convertView.findViewById(R.id.status_text_view);
            holder.officerNameTextView = convertView.findViewById(R.id.officer_name_text_view);
            holder.updatedAtTextView = convertView.findViewById(R.id.updated_at_text_view);
            holder.notesTextView = convertView.findViewById(R.id.notes_text_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HistoryModel model = historyList.get(position);
        if (model != null) {
            if (position == 0) {
                holder.parentItem.setPadding(Utils.convertDPtoPX(activity, 10),
                        Utils.convertDPtoPX(activity, 20),
                        Utils.convertDPtoPX(activity, 10),
                        0);
            }

            holder.circleView.setColorFilter(Color.parseColor(model.getColorCode().toUpperCase()));
            holder.lineView.setBackgroundColor(Color.parseColor(model.getColorCode().toUpperCase()));

            holder.userDraweeView.setImageURI(Uri.parse(Constants.IMAGE_PUBLIC_URL + model.getAssignedToImagePath()));

            setTextViewColor(holder.statusTitleTextView, model.getColorCode());
            setTextViewString(holder.statusTitleTextView, "Status:");
            setTextViewColor(holder.statusTextView, model.getColorCode());
            setTextViewString(holder.statusTextView, model.getComplaintStatusName());

//            setTextViewColor(holder.officerNameTitleTextView, model.getColorCode());
            setTextViewString(holder.officerNameTitleTextView, "Officer Name:");
//            setTextViewColor(holder.officerNameTextView, model.getColorCode());
            setTextViewString(holder.officerNameTextView, model.getAssignedToName());

//            setTextViewColor(holder.updatedAtTitleTextView, model.getColorCode());
            setTextViewString(holder.updatedAtTitleTextView, "Updated At:");
//            setTextViewColor(holder.updatedAtTextView, model.getColorCode());
            setTextViewString(holder.updatedAtTextView, Utils.formatServerDateAndTimeToUser(model.getUpdatedAt()));

//            setTextViewColor(holder.notesTitleTextView, model.getColorCode());
            setTextViewString(holder.notesTitleTextView, "Notes:");
//            setTextViewColor(holder.notesTextView, model.getColorCode());
            setTextViewString(holder.notesTextView, model.getRemarks());
        }

        return convertView;
    }

    private class ViewHolder {
        RelativeLayout parentItem;
        ImageView circleView;
        View lineView;
        SimpleDraweeView userDraweeView;
        TextView statusTitleTextView;
        TextView officerNameTitleTextView;
        TextView updatedAtTitleTextView;
        TextView notesTitleTextView;
        TextView statusTextView;
        TextView officerNameTextView;
        TextView updatedAtTextView;
        TextView notesTextView;
    }

    private void setTextViewColor(TextView textView, String stringColorCode) {
        textView.setTextColor(Color.parseColor(stringColorCode.toUpperCase()));
    }

    private void setTextViewString(TextView textView, String string) {
        textView.setText(string);
    }
}
