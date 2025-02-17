package com.citizen.fmc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.model.DrawerListItemModel;
import com.citizen.fmc.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * ======> Created by dheeraj-gangwar on 2018-01-22 <======
 */

public class DrawerExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<DrawerListItemModel> dataList = new ArrayList<>();

    public DrawerExpandableListAdapter() {
    }

    public DrawerExpandableListAdapter(Context context, List<DrawerListItemModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public List<DrawerListItemModel> getChild(int groupPosition, int childPosition) {
        return dataList.get(groupPosition).getSubItemModelList();
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
            childViewHolder.childIcon = convertView.findViewById(R.id.icon_text_view);
            childViewHolder.childTitleTextView = convertView.findViewById(R.id.title_text_view);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }

        childViewHolder.childTitleTextView.setText(getChild(groupPosition, childPosition).get(childPosition).getModuleTitleName());

        childViewHolder.childIcon.setText(Utils.setFontAwesome(context,
                childViewHolder.childIcon,
                getChild(groupPosition, childPosition).get(childPosition).getModuleIconUniCode()));
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return dataList.get(groupPosition).getSubItemModelList().size();
    }

    @Override
    public DrawerListItemModel getGroup(int groupPosition) {
        return dataList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return dataList.size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_image_text_list_item, parent, false);
            groupViewHolder.groupTextView = convertView.findViewById(R.id.title_text_view);
            groupViewHolder.groupIcon = convertView.findViewById(R.id.icon_text_view);

            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder.groupTextView.setText(getGroup(groupPosition).getModuleTitleName());

        groupViewHolder.groupIcon.setText(Utils.setFontAwesome(context,
                groupViewHolder.groupIcon,
                getGroup(groupPosition).getModuleIconUniCode()));
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
        TextView childTitleTextView;
        TextView childIcon;
    }

    class GroupViewHolder {
        TextView groupIcon;
        TextView groupTextView;
    }
}
