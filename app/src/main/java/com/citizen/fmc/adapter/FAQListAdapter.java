package com.citizen.fmc.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.model.FAQModel;
import com.citizen.fmc.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * ======> Created by dheeraj-gangwar on 2018-04-21 <======
 */

public class FAQListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<FAQModel> mainDataList = new ArrayList<>();

    public FAQListAdapter() {
    }

    public FAQListAdapter(Context context, ArrayList<FAQModel> categoryModels) {
        this.context = context;
        this.mainDataList = categoryModels;
    }

    @Override
    public List<FAQModel> getChild(int groupPosition, int childPosition) {
        return mainDataList.get(groupPosition).getAnswersList();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            childViewHolder = new ChildViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_child_image_text_list_item, parent, false);
            childViewHolder.childTV = convertView.findViewById(R.id.title_text_view);
            childViewHolder.iconTV = convertView.findViewById(R.id.icon_text_view);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        childViewHolder.childTV.setTypeface(null, Typeface.ITALIC);
        childViewHolder.childTV.setText(getChild(groupPosition, childPosition).get(childPosition).getAnswer());
        childViewHolder.iconTV.setText(Utils.setFontAwesome(context, childViewHolder.iconTV, "f0da"));
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mainDataList.get(groupPosition).getAnswersList().size();
    }

    @Override
    public FAQModel getGroup(int groupPosition) {
        return mainDataList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mainDataList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            groupViewHolder = new GroupViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_icon_text_list_item, parent, false);
            groupViewHolder.groupTV = convertView.findViewById(R.id.title_text_view);
            groupViewHolder.iconTV = convertView.findViewById(R.id.icon_text_view);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder.groupTV.setTypeface(null, Typeface.BOLD);
        groupViewHolder.groupTV.setText(getGroup(groupPosition).getQuestion());
        groupViewHolder.groupTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_keyboard_arrow_down_24dp, 0);
        groupViewHolder.iconTV.setText(Utils.setFontAwesome(context, groupViewHolder.iconTV, "f059"));
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ChildViewHolder {
        TextView childTV;
        TextView iconTV;
    }

    class GroupViewHolder {
        TextView groupTV;
        TextView iconTV;
    }
}
