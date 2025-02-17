package com.citizen.fmc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.citizen.fmc.R;
import com.citizen.fmc.model.ComplaintCategoryModel;
import com.citizen.fmc.model.ComplaintSubCategoryModel;
import com.citizen.fmc.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * ======> Created by dheeraj-gangwar on 2018-01-13 <======
 */

public class CompCatExpListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<ComplaintCategoryModel> mainDataList = new ArrayList<>();
    public CompCatExpListAdapter() {
    }

    public CompCatExpListAdapter(Context context,
                                 ArrayList<ComplaintCategoryModel> categoryModels) {
        this.context = context;
        this.mainDataList = categoryModels;
    }

    @Override
    public List<ComplaintSubCategoryModel> getChild(int groupPosition, int childPosition) {
        return mainDataList.get(groupPosition).getSubCategoryModelList();
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

        childViewHolder.childTV.setText(getChild(groupPosition, childPosition).get(childPosition).getSubCategoryName());
        childViewHolder.iconTV.setText(Utils.setFontAwesome(context, childViewHolder.iconTV,
                getChild(groupPosition, childPosition).get(childPosition).getIconUnicode()));
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mainDataList.get(groupPosition).getSubCategoryModelList().size();
    }

    @Override
    public ComplaintCategoryModel getGroup(int groupPosition) {
        return mainDataList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
//        if (module_id.equals("68")){
//            return 1;
//        } else {
            return mainDataList.size();
//        }
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
            if (mainDataList.size() == 1){
                ExpandableListView elv = (ExpandableListView) parent;
                elv.expandGroup(groupPosition);
            }
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }


        groupViewHolder.groupTV.setText(getGroup(groupPosition).getDepartmentName());
        groupViewHolder.iconTV.setText(Utils.setFontAwesome(context, groupViewHolder.iconTV,
                getGroup(groupPosition).getIconUnicode()));
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
