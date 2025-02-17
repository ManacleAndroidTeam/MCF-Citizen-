package com.citizen.fmc.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.citizen.fmc.R;
import com.citizen.fmc.interface_.GetOnItemClick;
import com.citizen.fmc.utils.Utils;

import java.util.ArrayList;

public class AirPolCompCatListAdapter extends RecyclerView.Adapter<AirPolCompCatListAdapter.ViewHolder> {
    Context context;
    ArrayList<ComplaintCatModel> airPolComCatList;
    private GetOnItemClick getOnItemClick;
    public AirPolCompCatListAdapter(Context context, ArrayList<ComplaintCatModel> airPolComCatList, GetOnItemClick getOnItemClick){
        this.context = context;
        this.airPolComCatList = airPolComCatList;
        this.getOnItemClick = getOnItemClick;
    }
    @NonNull
    @Override
    public AirPolCompCatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_child_image_text_list_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AirPolCompCatListAdapter.ViewHolder viewHolder, final int position) {
        ComplaintCatModel complaintCatModel = airPolComCatList.get(position);
        if (complaintCatModel != null){
            viewHolder.title_tv.setText(complaintCatModel.getName());
            viewHolder.icon_tv.setText(Utils.setFontAwesome(context,viewHolder.icon_tv,complaintCatModel.getIcon_unicode()));
            viewHolder.comp_cat_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getOnItemClick.onGetPosItemclick(position);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return airPolComCatList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView icon_tv, title_tv;
        RelativeLayout comp_cat_rl;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon_tv = itemView.findViewById(R.id.icon_text_view);
            title_tv = itemView.findViewById(R.id.title_text_view);
            comp_cat_rl = itemView.findViewById(R.id.comp_cat_rl);
        }

    }
}
