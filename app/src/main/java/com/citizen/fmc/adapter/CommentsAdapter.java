package com.citizen.fmc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.cardview.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.fragment.ZoomableFragment;
import com.citizen.fmc.model.CommentsModel;
import com.citizen.fmc.model.ComplaintStatusModel;
import com.citizen.fmc.utils.Constants;
import com.citizen.fmc.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * ======> Created by dheeraj-gangwar on 2018-02-05 <======
 */


@SuppressLint("SetTextI18n")
public class CommentsAdapter extends ArrayAdapter<CommentsModel> {
    private Context mContext;
    private List<CommentsModel> mainDataList;
    private List<ComplaintStatusModel> statusModelList;
    private FragmentManager fragmentManager;
    private String TAG = getClass().getSimpleName();

    public CommentsAdapter(@NonNull Context context,
                           @NonNull List<CommentsModel> objects,
                           /*List<ComplaintStatusModel> statusModelList,*/
                           FragmentManager fragmentManager) {
        super(context, 0, objects);
        this.mContext = context;
        this.mainDataList = objects;
//        this.statusModelList = statusModelList;
        this.fragmentManager = fragmentManager;
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
    public CommentsModel getItem(int position) {
        return mainDataList.get(position);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final CommentsModel listItem = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_comments_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.userSDV = convertView.findViewById(R.id.module_sdv);
            viewHolder.commentImageCardView = convertView.findViewById(R.id.comment_image_card_view);
            viewHolder.commentSDV = convertView.findViewById(R.id.comment_sdv);
            viewHolder.nameTV = convertView.findViewById(R.id.name_text_view);
            viewHolder.dateTimeTV = convertView.findViewById(R.id.date_time_text_view);
            viewHolder.commentTV = convertView.findViewById(R.id.comment_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (listItem != null) {
            if (listItem.getCommentImagePath() != null && !listItem.getCommentImagePath().isEmpty()) {
                viewHolder.commentImageCardView.setVisibility(View.VISIBLE);
                viewHolder.commentSDV.setImageURI(Constants.IMAGE_PUBLIC_URL + listItem.getCommentImagePath());
            } else {
                viewHolder.commentImageCardView.setVisibility(View.GONE);
            }

            Log.v(TAG, "User Image: " + Constants.IMAGE_PUBLIC_URL + listItem.getComplainantImagePath());
            viewHolder.userSDV.setImageURI(Uri.parse(Constants.IMAGE_PUBLIC_URL + listItem.getComplainantImagePath()));

            if (listItem.getAppType() != Constants.APP_TYPE) {
                viewHolder.nameTV.setText(Html.fromHtml("<strong>Officer:</strong> @" + listItem.getComplainantName() + " "));
            } else {
                viewHolder.nameTV.setText(listItem.getComplainantName() + " ");
            }

            viewHolder.dateTimeTV.setText(Utils.formatServerDateAndTimeToUser(listItem.getCommentDateTime()));
            viewHolder.commentTV.setText(Html.fromHtml("<strong>Comment: </strong>" + listItem.getComment()));

            viewHolder.commentImageCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ZoomableFragment zoomableFragment = new ZoomableFragment();
                    Bundle mBundle = new Bundle();
                    mBundle.putString(Constants.KEY_IMAGE_PATH, Constants.IMAGE_PUBLIC_URL + listItem.getCommentImagePath());
                    zoomableFragment.setArguments(mBundle);
                    Utils.addFragment(fragmentManager, zoomableFragment);
                }
            });

//        for (int i = 0; i < statusModelList.size(); i++) {
//            if (mainDataList.get(position).getCompStatus() == statusModelList.get(i).getStatusId()) {
//                int colorCode = Color.parseColor(statusModelList.get(i).getStatusColorCode().toUpperCase().trim());
//                viewHolder.commentTV.setBackgroundColor(colorCode);
//                viewHolder.compStatusColorView.setBackgroundColor(colorCode);
//                viewHolder.commentTV.setText(statusModelList.get(i).getStatusName());
//                break;
//            }
//        }
        }

        return convertView;
    }

    private class ViewHolder {
        SimpleDraweeView userSDV;
        CardView commentImageCardView;
        SimpleDraweeView commentSDV;
        TextView nameTV;
        TextView commentTV;
        TextView dateTimeTV;
    }
}