package com.citizen.fmc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.model.ServerNotificationModel;
import com.citizen.fmc.utils.Constants;
import com.facebook.drawee.view.SimpleDraweeView;


import java.util.ArrayList;

/**
 * ======> Created by dheeraj-gangwar on 2018-06-14 <======
 */

@SuppressLint("SetTextI18n, SimpleDateFormat")
public class ServerNotificationListAdapter extends ArrayAdapter<ServerNotificationModel> {
    private Context context;
    private ArrayList<ServerNotificationModel> notificationModels;

    public ServerNotificationListAdapter(@NonNull Context context, @NonNull ArrayList<ServerNotificationModel> notificationModels) {
        super(context, 0, notificationModels);
        this.context = context;
        this.notificationModels = notificationModels;
    }

    @Override
    public int getCount() {
        return notificationModels.size();
    }

    @Nullable
    @Override
    public ServerNotificationModel getItem(int position) {
        return notificationModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_user_notification_list_item, parent, false);
            holder = new ViewHolder();
            holder.serialNumTextView = convertView.findViewById(R.id.serial_num_text_view);
            holder.messageImageView = convertView.findViewById(R.id.notification_image_view);
            holder.messageDateTimeStampTextView = convertView.findViewById(R.id.message_date_time_stamp_text_view);
            holder.messageTitleTextView = convertView.findViewById(R.id.message_title_text_view);
            holder.messageBodyTextView = convertView.findViewById(R.id.message_body_text_view);
            holder.parent_layout = convertView.findViewById(R.id.parent_layout);
            holder.RV_parent_layout = convertView.findViewById(R.id.RV_parent_layout);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ServerNotificationModel model = getItem(position);

        if (model != null) {
            holder.serialNumTextView.setText(String.valueOf((position + 1) + ". "));
            String _path = model.getImagePath();
            if (_path != null && !_path.isEmpty()) {
                holder.messageImageView.setVisibility(View.VISIBLE);
                holder.messageImageView.setController(
                        Constants.simpleDraweeController(Constants.IMAGE_PUBLIC_URL + _path, 100, 100));
            } else {
                holder.messageImageView.setVisibility(View.GONE);
            }

            //Unread message background change:-
            if(model.getReadStatus() == 0) {
                int transWhite = Color.parseColor("#FFFFFF");
                int greyColor = Color.parseColor("#B0888888");
                holder.parent_layout.setBackgroundColor(greyColor);

                holder.messageTitleTextView.setText(model.getTitle());
                holder.messageTitleTextView.setTextColor(transWhite);
                holder.messageDateTimeStampTextView.setText(Constants.formatDateAndTime(model.getCreatedAt()));
                holder.messageDateTimeStampTextView.setTextColor(transWhite);
                holder.messageBodyTextView.setText(Html.fromHtml("<strong>Message: </strong>" + model.getDescription()));
                holder.messageBodyTextView.setTextColor(transWhite);
            }else {
                holder.messageTitleTextView.setText(model.getTitle());
                holder.messageDateTimeStampTextView.setText(Constants.formatDateAndTime(model.getCreatedAt()));
                holder.messageBodyTextView.setText(Html.fromHtml("<strong>Message: </strong>" + model.getDescription()));
            }
        }

        return convertView;
    }

    private class ViewHolder {
        RelativeLayout RV_parent_layout;
        SimpleDraweeView messageImageView;
        TextView serialNumTextView;
        TextView messageDateTimeStampTextView;
        TextView messageTitleTextView;
        TextView messageBodyTextView;
        CardView parent_layout;
    }
}
