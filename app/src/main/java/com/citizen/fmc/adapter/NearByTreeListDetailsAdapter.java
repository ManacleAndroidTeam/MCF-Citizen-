package com.citizen.fmc.adapter;

import android.app.Activity;
import android.text.TextUtils;
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
import com.citizen.fmc.model.OtherAttributes;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class NearByTreeListDetailsAdapter extends ArrayAdapter<OtherAttributes> {
    private Activity activity;
    List<OtherAttributes> otherAttributes;

    String treeId= "NA", commonTreeId="NA", botanicalName = "", marathiName = "", hindiName = "", family1="";
    String origin = "NA", category = "NA", use = "NA", crownShape = "NA", naturalSurvivalCapacity = "NA", latitude = "NA", longitude = "NA", floweringSeason = "NA",
    fruiting_season = "NA", flowering_color= "NA", treeNumber = "NA", treeName = "Na";

    public NearByTreeListDetailsAdapter(@NonNull Activity activity, @NonNull List<OtherAttributes> list,String treeNumber, String treeName) {
        super(activity, 0, list);
        this.activity = activity;
        this.otherAttributes = list;
        this.treeName = treeName;
        this.treeNumber = treeNumber;
    }

    @Nullable
    @Override
    public OtherAttributes getItem(int position) {
        return otherAttributes.get(position);
    }

    @Override
    public int getCount() {
        return otherAttributes.size();
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
        NearByTreeListDetailsAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new NearByTreeListDetailsAdapter.ViewHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.layout_tree_near_by_me_list, parent, false);
            viewHolder.helpLineImageView = convertView.findViewById(R.id.list_image_view);
            viewHolder.treeNo = convertView.findViewById(R.id.tree_no);
            viewHolder.treeName = convertView.findViewById(R.id.tree_name);
            viewHolder.tv_tree_id = convertView.findViewById(R.id.tv_tree_id);
            viewHolder.tv_common_tree_id = convertView.findViewById(R.id.tv_common_tree_id);
            //viewHolder.helpLineDialerImageView = convertView.findViewById(R.id.list_dialer_image_view);
            viewHolder.diameter = convertView.findViewById(R.id.diameter);
            viewHolder.girth = convertView.findViewById(R.id.girth);
            viewHolder.height = convertView.findViewById(R.id.height);
            viewHolder.age = convertView.findViewById(R.id.age);
            viewHolder.relativeLayout = convertView.findViewById(R.id.relativeLayout);
            viewHolder.tree_no = convertView.findViewById(R.id.tree_no);
            viewHolder.tree_name = convertView.findViewById(R.id.tree_name);

            viewHolder.tv_tree_no = convertView.findViewById(R.id.tv_tree_no);
            viewHolder.tv_tree_name = convertView.findViewById(R.id.tv_tree_name);
            viewHolder.tv_diameter = convertView.findViewById(R.id.tv_diameter);
            viewHolder.tv_girth = convertView.findViewById(R.id.tv_girth);
            viewHolder.tv_height = convertView.findViewById(R.id.tv_height);
            viewHolder.tv_age = convertView.findViewById(R.id.tv_age);

            viewHolder.tv_origin = convertView.findViewById(R.id.tv_origin);
            viewHolder.tv_category = convertView.findViewById(R.id.tv_category);
            viewHolder.tv_use = convertView.findViewById(R.id.tv_use);
            viewHolder.tv_crown_shape = convertView.findViewById(R.id.tv_crown_shape);
            viewHolder.tv_natural_survival_capacity = convertView.findViewById(R.id.tv_natural_survival_capacity);
            viewHolder.tv_lat = convertView.findViewById(R.id.tv_lat);
            viewHolder.tv_lng = convertView.findViewById(R.id.tv_lng);
            viewHolder.tv_flowering_season = convertView.findViewById(R.id.tv_flowering_season);
            viewHolder.tv_flowering_color = convertView.findViewById(R.id.tv_flowering_color);
            viewHolder.tv_fruting_season = convertView.findViewById(R.id.tv_fruting_season);




            convertView.setTag(viewHolder);
        } else {
            viewHolder = (NearByTreeListDetailsAdapter.ViewHolder) convertView.getTag();
        }

        //viewHolder.helpLineDialerImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.place_holder_no_image));
        viewHolder.helpLineImageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.place_holder_no_image));
        final OtherAttributes helpLineModel = getItem(position);
        if (helpLineModel != null) {
/*            viewHolder.helpLineImageView.setController(Utils.simpleDraweeController(
                    Constants.IMAGE_PUBLIC_URL + helpLineModel.getImagePath(),
                    90, 90));*/

//            String girth =String.valueOf(helpLineModel.getGirth());

//                treeNumber = String.valueOf(NearByTreeFragment.nearbyTreeModels.get(position).getTreeNo());
//                treeName = String.valueOf(NearByTreeFragment.nearbyTreeModels.get(position).getTreeName());
                treeId = String.valueOf(otherAttributes.get(position).getTreeID());
                commonTreeId = String.valueOf(otherAttributes.get(position).getCommonTreeID());
                botanicalName = String.valueOf(otherAttributes.get(position).getBotanicalName());
                marathiName = String.valueOf(otherAttributes.get(position).getMarathiName());
                hindiName = String.valueOf(otherAttributes.get(position).getHindiName());
                family1 = String.valueOf(otherAttributes.get(position).getFamily());

                origin = String.valueOf(otherAttributes.get(position).getOrigin());
                category = String.valueOf(otherAttributes.get(position).getCategory());
                use  = String.valueOf(otherAttributes.get(position).getUse());
                crownShape= String.valueOf(otherAttributes.get(position).getCrownShape());

                naturalSurvivalCapacity = String.valueOf(otherAttributes.get(position).getNaturalSurvivalCapacity());
                latitude = String.valueOf(otherAttributes.get(position).getLatitude());
                longitude = String.valueOf(otherAttributes.get(position).getLongitude());

                floweringSeason = String.valueOf(otherAttributes.get(position).getFloweringSeason());
                flowering_color = String.valueOf(otherAttributes.get(position).getFloweringColor());
                fruiting_season = String.valueOf(otherAttributes.get(position).getFruitingSeason());





            if (TextUtils.isEmpty(treeNumber) || treeNumber.equalsIgnoreCase("null") || treeNumber==null)
            {
                viewHolder.tree_no.setText("NA");
            }
            else
            {
                viewHolder.tree_no.setText(treeNumber);
            }

            if (TextUtils.isEmpty(treeName) || treeName.equalsIgnoreCase("null") || treeName==null)
            {
                viewHolder.tree_name.setText("NA");
            }
            else
            {
                viewHolder.tree_name.setText(treeName);
            }


            if (TextUtils.isEmpty(treeId) || treeId.equalsIgnoreCase("null") || treeId==null)
            {
                viewHolder.tv_tree_id.setText("NA");
            }
            else
            {
                viewHolder.tv_tree_id.setText(treeId);
            }


            if (TextUtils.isEmpty(commonTreeId) || commonTreeId.equalsIgnoreCase("null") || commonTreeId==null)
            {
                viewHolder.tv_common_tree_id.setText("NA");
            }
            else
            {
                viewHolder.tv_common_tree_id.setText(commonTreeId);
            }

            if (TextUtils.isEmpty(botanicalName) || botanicalName.equalsIgnoreCase("null") || botanicalName==null)
            {
                viewHolder.tv_diameter.setText("NA");
            }
            else
            {
                viewHolder.tv_diameter.setText(botanicalName);
            }

            if (TextUtils.isEmpty(marathiName) || marathiName.equalsIgnoreCase("null") || marathiName==null)
            {
                viewHolder.tv_girth.setText("NA");
            }
            else
            {
                viewHolder.tv_girth.setText(marathiName);
            }

            if (TextUtils.isEmpty(hindiName) || hindiName.equalsIgnoreCase("null") || hindiName==null)
            {
                viewHolder.tv_height.setText("NA");
            }
            else
            {
                viewHolder.tv_height.setText(hindiName);
            }

            if (TextUtils.isEmpty(family1) || family1.equalsIgnoreCase("null") || family1==null)
            {
                viewHolder.tv_age.setText("NA");
            }
            else
            {
                viewHolder.tv_age.setText(family1);
            }

            if (TextUtils.isEmpty(origin) || origin.equalsIgnoreCase("null") || origin==null)
            {
                viewHolder.tv_origin.setText("NA");
            }
            else
            {
                viewHolder.tv_origin.setText(origin);
            }

            if (TextUtils.isEmpty(category) || category.equalsIgnoreCase("null") || category==null)
            {
                viewHolder.tv_category.setText("NA");
            }
            else
            {
                viewHolder.tv_category.setText(category);
            }

            if (TextUtils.isEmpty(use) || use.equalsIgnoreCase("null") || use==null)
            {
                viewHolder.tv_use.setText("NA");
            }
            else
            {
                viewHolder.tv_use.setText(use);
            }

            if (TextUtils.isEmpty(crownShape) || crownShape.equalsIgnoreCase("null") || crownShape==null)
            {
                viewHolder.tv_crown_shape.setText("NA");
            }
            else
            {
                viewHolder.tv_crown_shape.setText(crownShape);
            }

            if (TextUtils.isEmpty(naturalSurvivalCapacity) || naturalSurvivalCapacity.equalsIgnoreCase("null") || naturalSurvivalCapacity==null)
            {
                viewHolder.tv_natural_survival_capacity.setText("NA");
            }
            else
            {
                viewHolder.tv_natural_survival_capacity.setText(naturalSurvivalCapacity);
            }

            if (TextUtils.isEmpty(latitude) || latitude.equalsIgnoreCase("null") || latitude==null)
            {
                viewHolder.tv_lat.setText("NA");
            }
            else
            {
                viewHolder.tv_lat.setText(latitude);
            }

            if (TextUtils.isEmpty(longitude) || longitude.equalsIgnoreCase("null") || longitude==null)
            {
                viewHolder.tv_lng.setText("NA");
            }
            else
            {
                viewHolder.tv_lng.setText(longitude);
            }

            if (TextUtils.isEmpty(floweringSeason) || floweringSeason.equalsIgnoreCase("null") || floweringSeason==null)
            {
                viewHolder.tv_flowering_season.setText("NA");
            }
            else
            {
                viewHolder.tv_flowering_season.setText(floweringSeason);
            }

            if (TextUtils.isEmpty(flowering_color) || flowering_color.equalsIgnoreCase("null") || flowering_color==null)
            {
                viewHolder.tv_flowering_color.setText("NA");
            }
            else
            {
                viewHolder.tv_flowering_color.setText(flowering_color);
            }

            if (TextUtils.isEmpty(fruiting_season) || fruiting_season.equalsIgnoreCase("null") || fruiting_season==null)
            {
                viewHolder.tv_fruting_season.setText("NA");
            }
            else
            {
                viewHolder.tv_fruting_season.setText(fruiting_season);
            }











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

        TextView tv_flowering_season, tv_flowering_color, tv_fruting_season, tv_lng, tv_lat, tv_natural_survival_capacity, tv_crown_shape, tv_use,
                tv_category, tv_origin;
        TextView tv_common_tree_id, tv_tree_id;
        TextView tree_no, tree_name;

        TextView tv_tree_no, tv_tree_name, tv_diameter, tv_girth,tv_height,tv_age;
    }
}
