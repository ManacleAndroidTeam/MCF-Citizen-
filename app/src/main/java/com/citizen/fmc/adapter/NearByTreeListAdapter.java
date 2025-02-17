package com.citizen.fmc.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.citizen.fmc.R;
import com.citizen.fmc.activity.NearByTreeListDetails;
import com.citizen.fmc.model.NearByTreeListModel;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class NearByTreeListAdapter extends ArrayAdapter<NearByTreeListModel> {
    private Activity activity;
    public static List<NearByTreeListModel> list;

    public NearByTreeListAdapter(@NonNull Activity activity, @NonNull List<NearByTreeListModel> list) {
        super(activity, 0, list);
        this.activity = activity;
        this.list = list;
    }

    @Nullable
    @Override
    public NearByTreeListModel getItem(int position) {
        return list.get(position);
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
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        NearByTreeListAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new NearByTreeListAdapter.ViewHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.layout_tree_near_by_me, parent, false);
            viewHolder.helpLineImageView = convertView.findViewById(R.id.list_image_view);
            viewHolder.treeNo = convertView.findViewById(R.id.tree_no);
            viewHolder.treeName = convertView.findViewById(R.id.tree_name);
            //viewHolder.helpLineDialerImageView = convertView.findViewById(R.id.list_dialer_image_view);
            viewHolder.diameter = convertView.findViewById(R.id.diameter);
            viewHolder.girth = convertView.findViewById(R.id.girth);
            viewHolder.height = convertView.findViewById(R.id.height);
            viewHolder.age = convertView.findViewById(R.id.age);
            viewHolder.relativeLayout = convertView.findViewById(R.id.relativeLayout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (NearByTreeListAdapter.ViewHolder) convertView.getTag();
        }

        //viewHolder.helpLineDialerImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.place_holder_no_image));
        viewHolder.helpLineImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.place_holder_no_image));
        final NearByTreeListModel helpLineModel = getItem(position);
        if (helpLineModel != null) {
/*            viewHolder.helpLineImageView.setController(Utils.simpleDraweeController(
                    Constants.IMAGE_PUBLIC_URL + helpLineModel.getImagePath(),
                    90, 90));*/

//            String girth =String.valueOf(helpLineModel.getGirth());
                viewHolder.treeNo.setText(helpLineModel.getTreeNo());
                viewHolder.treeName.setText(helpLineModel.getTreeName());
                viewHolder.diameter.setText(String.valueOf(helpLineModel.getDiameter()));
                viewHolder.girth.setText(String.valueOf(helpLineModel.getGirth()));
                viewHolder.height.setText(String.valueOf(helpLineModel.getHeight()));
                viewHolder.age.setText(String.valueOf(helpLineModel.getAge()));


            viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String treeId = String.valueOf(helpLineModel.getTreeID());
                    String treeNo = String.valueOf(helpLineModel.getTreeNo());
                    String treeName = String.valueOf(helpLineModel.getTreeName());
                    Intent intent = new Intent(getContext(), NearByTreeListDetails.class);
//                    intent.putParcelableArrayListExtra("data",list.get(position).getOtherAttributes());
                    intent.putExtra("position",position);
                    intent.putExtra("tree_no",treeNo);
                    intent.putExtra("tree_name",treeName);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.putExtra("treeId",treeId);
                    getContext().startActivity(intent);
                }
            });

        }

        return convertView;
    }

    private class ViewHolder {
        SimpleDraweeView helpLineImageView;
        TextView treeNo;
        TextView treeName;
        ImageView helpLineDialerImageView;
        TextView diameter;
        TextView girth;
        TextView height;
        TextView age;
        RelativeLayout relativeLayout;
    }
}